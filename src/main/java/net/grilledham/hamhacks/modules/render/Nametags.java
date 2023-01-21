package net.grilledham.hamhacks.modules.render;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventRender2D;
import net.grilledham.hamhacks.event.events.EventTick;
import net.grilledham.hamhacks.modules.Category;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.modules.ModuleManager;
import net.grilledham.hamhacks.modules.misc.NameHider;
import net.grilledham.hamhacks.setting.BoolSetting;
import net.grilledham.hamhacks.setting.ColorSetting;
import net.grilledham.hamhacks.setting.NumberSetting;
import net.grilledham.hamhacks.setting.SettingCategory;
import net.grilledham.hamhacks.util.Color;
import net.grilledham.hamhacks.util.EnchantUtil;
import net.grilledham.hamhacks.util.ProjectionUtil;
import net.grilledham.hamhacks.util.RenderUtil;
import net.grilledham.hamhacks.util.math.Vec3;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.util.*;
import java.util.stream.Stream;

public class Nametags extends Module {
	
	private final ArrayList<LivingEntity> entities = new ArrayList<>();
	
	private final Map<LivingEntity, String> names = new HashMap<>();
	
	private final SettingCategory ENTITIES_CATEGORY = new SettingCategory("hamhacks.module.nametags.category.entities");
	
	private final BoolSetting self = new BoolSetting("hamhacks.module.nametags.self", true, () -> true);
	
	private final BoolSetting hostiles = new BoolSetting("hamhacks.module.nametags.hostiles", true, () -> true);
	
	private final BoolSetting passives = new BoolSetting("hamhacks.module.nametags.passives", true, () -> true);
	
	private final SettingCategory ELEMENTS_CATEGORY = new SettingCategory("hamhacks.module.nametags.category.elements");
	
	private final BoolSetting entityItems = new BoolSetting("hamhacks.module.nametags.entityItems", true, () -> true);
	
	private final BoolSetting enchants = new BoolSetting("hamhacks.module.nametags.enchants", true, entityItems::get);
	
	private final BoolSetting gamemode = new BoolSetting("hamhacks.module.nametags.gamemode", true, () -> true);
	
	private final BoolSetting distance = new BoolSetting("hamhacks.module.nametags.distance", true, () -> true);
	
	private final BoolSetting ping = new BoolSetting("hamhacks.module.nametags.ping", true, () -> true);
	
	private final SettingCategory APPEARANCE_CATEGORY = new SettingCategory("hamhacks.module.nametags.category.appearance");
	
	private final NumberSetting scale = new NumberSetting("hamhacks.module.nametags.scale", 2, () -> true, 0.25, 4, 0.25, false);
	
	private final NumberSetting itemScale = new NumberSetting("hamhacks.module.nametags.itemScale", 2,entityItems::get, 0.25, 4, 0.25, false);
	
	private final BoolSetting scaleWithZoom = new BoolSetting("hamhacks.module.nametags.scaleWithZoom", true, () -> true);
	
	private final ColorSetting outlineColor = new ColorSetting("hamhacks.module.nametags.outlineColor", new Color(0x80AA0000), () -> true);
	
	private final ColorSetting fillColor = new ColorSetting("hamhacks.module.nametags.fillColor", new Color(0x80000000), () -> true);
	
	public Nametags() {
		super(Text.translatable("hamhacks.module.nametags"), Category.RENDER, new Keybind(0));
		settingCategories.add(0, ENTITIES_CATEGORY);
		ENTITIES_CATEGORY.add(self);
		ENTITIES_CATEGORY.add(hostiles);
		ENTITIES_CATEGORY.add(passives);
		settingCategories.add(1, ELEMENTS_CATEGORY);
		ELEMENTS_CATEGORY.add(entityItems);
		ELEMENTS_CATEGORY.add(enchants);
		ELEMENTS_CATEGORY.add(gamemode);
		ELEMENTS_CATEGORY.add(distance);
		ELEMENTS_CATEGORY.add(ping);
		settingCategories.add(2, APPEARANCE_CATEGORY);
		APPEARANCE_CATEGORY.add(scale);
		APPEARANCE_CATEGORY.add(itemScale);
		APPEARANCE_CATEGORY.add(scaleWithZoom);
		APPEARANCE_CATEGORY.add(outlineColor);
		APPEARANCE_CATEGORY.add(fillColor);
	}
	
	@EventListener
	public void onRender2D(EventRender2D e) {
		MatrixStack matrixStack = e.matrices;
		float partialTicks = e.tickDelta;
		
		matrixStack.push();
		
		matrixStack.loadIdentity();
		matrixStack.scale((float)(1 / mc.getWindow().getScaleFactor()), (float)(1 / mc.getWindow().getScaleFactor()), 1);
		
		render(matrixStack, partialTicks);
		
		matrixStack.pop();
	}
	
	@EventListener
	public void onTick(EventTick e) {
		if(mc.world == null) {
			return;
		}
		ClientWorld world = mc.world;
		
		entities.clear();
		Stream<LivingEntity> stream = world.getEntitiesByType(new TypeFilter<Entity, LivingEntity>() {
					@Nullable
					@Override
					public LivingEntity downcast(Entity entity) {
						return (LivingEntity)entity;
					}
					
					@Override
					public Class<? extends Entity> getBaseClass() {
						return LivingEntity.class;
					}
				}, new Box(mc.player.getBlockPos().add(-256, -256, -256), mc.player.getBlockPos().add(256, 256, 256)), Objects::nonNull).stream()
				.filter(this::shouldRender);
		
		entities.addAll(stream.toList());
		
		names.clear();
		for(LivingEntity entity : entities) {
			String gmString = "";
			if(gamemode.get() && entity instanceof PlayerEntity p) {
				PlayerListEntry playerListEntry = mc.getNetworkHandler().getPlayerListEntry(p.getUuid());
				if(playerListEntry == null) {
					gmString = "\u00a77BOT";
				} else {
					GameMode mode = playerListEntry.getGameMode();
					if(mode == null) {
						gmString = "\u00a77BOT";
					} else {
						gmString = switch(mode) {
							case SURVIVAL -> "\u00a79S";
							case CREATIVE -> "\u00a7cC";
							case ADVENTURE -> "\u00a7aA";
							case SPECTATOR -> "\u00a77SP";
						};
					}
				}
				gmString = "\u00a77[" + gmString + "\u00a77] ";
			}
			
			String name;
			if(entity == mc.player) {
				name = ModuleManager.getModule(NameHider.class).modifyName(entity.getEntityName());
			} else {
				name = entity.getName().getString();
			}
			String nameColor = "\u00a7f";
			if(entity.isSneaking()) {
				nameColor = "\u00a77";
			}
			name = nameColor + name + " ";
			
			float hp = entity.getHealth() + entity.getAbsorptionAmount();
			float healthPercentage = Math.round((hp / entity.getMaxHealth()) * 1000) / 10f;
			String healthColor = "\u00a72";
			if(healthPercentage <= 25) {
				healthColor = "\u00a74";
			} else if(healthPercentage <= 50) {
				healthColor = "\u00a7c";
			} else if(healthPercentage <= 75) {
				healthColor = "\u00a7e";
			} else if(healthPercentage < 100) {
				healthColor = "\u00a7a";
			} else if(healthPercentage > 100) {
				healthColor = "\u00a76";
			}
			String health = healthColor + healthPercentage + "% ";
			
			String distanceString = "";
			if(distance.get()) {
				Vec3d distFrom = ModuleManager.getModule(Freecam.class).isEnabled() ? mc.gameRenderer.getCamera().getPos() : mc.cameraEntity.getPos();
				float dist = Math.round(distFrom.distanceTo(entity.getPos()) * 10) / 10f;
				distanceString = "\u00a79" + dist + "m ";
			}
			
			String pingString = "";
			if(ping.get() && entity instanceof PlayerEntity p) {
				PlayerListEntry playerListEntry = mc.getNetworkHandler().getPlayerListEntry(p.getUuid());
				int latency = -1;
				if(playerListEntry != null) {
					latency = playerListEntry.getLatency();
				}
				String pingColor = "\u00a71";
				if(latency >= 250) {
					pingColor = "\u00a74";
				} else if(latency >= 150) {
					pingColor = "\u00a7c";
				} else if(latency >= 75) {
					pingColor = "\u00a7e";
				} else if(latency >= 0) {
					pingColor = "\u00a7a";
				}
				pingString = pingColor + latency + "ms ";
			}
			
			String display = gmString + name + health + distanceString + pingString;
			
			names.put(entity, display.trim());
		}
	}
	
	private void render(MatrixStack matrixStack, double partialTicks) {
		RenderSystem.setShader(GameRenderer::getPositionColorProgram);
		RenderSystem.setShaderColor(1, 1, 1, 1);
		
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		
		for(LivingEntity e : entities) {
			if(!shouldRender(e)) continue;
			
			Vec3 interpolationOffset = new Vec3(e.getX(), e.getY(), e.getZ()).sub(e.prevX, e.prevY, e.prevZ).mul(1 - partialTicks);
			float ex = (float)(e.getX() - interpolationOffset.getX());
			float ey = (float)(e.getY() - interpolationOffset.getY());
			float ez = (float)(e.getZ() - interpolationOffset.getZ());
			
			Vec3 pos = new Vec3(ex, ey + e.getHeight() + 0.2, ez);
			
			if(ProjectionUtil.to2D(pos, scale.get() * (scaleWithZoom.get() ? ModuleManager.getModule(Zoom.class).getZoomAmount() : 1), true)) {
				TextRenderer textRenderer = mc.textRenderer;
				
				String display = names.get(e);
				
				float width = textRenderer.getWidth(display);
				
				float xCenter = width / 2;
				float height = textRenderer.fontHeight;
				
				matrixStack.push();
				matrixStack.translate(pos.getX(), pos.getY(), 0);
				matrixStack.scale((float)ProjectionUtil.scale, (float)ProjectionUtil.scale, 1);
				
				Matrix4f matrix = matrixStack.peek().getPositionMatrix();
				
				drawBackground(bufferBuilder, matrix, -xCenter - 2, -height - 2, width + 3, height + 3);
				
				textRenderer.drawWithShadow(matrixStack, display, -xCenter, -height, -1);
				
				if(entityItems.get()) {
					float[] itemWidths = new float[6];
					boolean hasItems = false;
					int enchantCount = 0;
					
					for(int i = 0; i < 6; i++) {
						ItemStack stack = getItem(e, i);
						
						if(!stack.isEmpty()) {
							itemWidths[i] = (16 + (i < 5 ? 2 : 0)) * (float)(double)itemScale.get();
							hasItems = true;
						} else {
							itemWidths[i] = 0;
						}
						
						if(enchants.get() && !stack.isEmpty()) {
							Map<Enchantment, Integer> enchantments = EnchantmentHelper.get(stack);
							
							int size = 0;
							for(Enchantment enchantment : enchantments.keySet()) {
								String enchantName = EnchantUtil.getShortName(enchantment) + " " + enchantments.get(enchantment);
								itemWidths[i] = Math.max(itemWidths[i], textRenderer.getWidth(enchantName + " "));
								size++;
							}
							
							enchantCount = Math.max(enchantCount, size);
						}
					}
					
					float itemsHeight = hasItems ? 16 * (float)(double)itemScale.get() : 0;
					float itemsWidth = 0;
					for(float w : itemWidths) itemsWidth += w;
					float itemsXCenter = itemsWidth / 2;
					
					float y = -height - 15 - itemsHeight / 2;
					float x = -itemsXCenter - 7;
					
					for(int i = 0; i < 6; i++) {
						ItemStack stack = getItem(e, i);
						
						RenderUtil.drawItem(matrixStack, stack, x + itemWidths[i] / 2, y, (float)(double)itemScale.get(), true);
						
						if(enchantCount > 0 && enchants.get() && !stack.isEmpty()) {
							Map<Enchantment, Integer> enchantments = EnchantmentHelper.get(stack);
							
							float itemWidth = itemWidths[i];
							float enchantY =  -itemsHeight / 2 - (enchantments.size() * textRenderer.fontHeight) + 12;
							float enchantX;
							
							for(Enchantment enchantment : enchantments.keySet()) {
								String enchantColor = enchantment.isCursed() ? "\u00a7c" : "\u00a7f";
								String enchantName = enchantColor + EnchantUtil.getShortName(enchantment) + " " + enchantments.get(enchantment);
								
								enchantX = x + (itemWidth / 2) - (textRenderer.getWidth(enchantName) / 2f) + 8;
								
								matrixStack.translate(0, 0, 300);
								textRenderer.drawWithShadow(matrixStack, enchantName, enchantX, y + enchantY, -1);
								matrixStack.translate(0, 0, -300);
								
								enchantY += textRenderer.fontHeight;
							}
						}
						x += itemWidths[i];
					}
				}
				
				matrixStack.pop();
			}
		}
	}
	
	private ItemStack getItem(LivingEntity entity, int index) {
		List<ItemStack> armor = Lists.newArrayList(entity.getArmorItems());
		return switch(index) {
			case 0 -> entity.getMainHandStack();
			case 1 -> entity.getOffHandStack();
			case 2 -> armor.get(3);
			case 3 -> armor.get(2);
			case 4 -> armor.get(1);
			case 5 -> armor.get(0);
			default -> ItemStack.EMPTY;
		};
	}
	
	private void drawBackground(BufferBuilder bufferBuilder, Matrix4f matrix, float x, float y, float w, float h) {
		int oc = outlineColor.get().getRGB();
		int fc = fillColor.get().getRGB();
		
		RenderUtil.preRender();
		
		// fill
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex(matrix, x, y, 0).color(fc).next();
		bufferBuilder.vertex(matrix, x, y + h, 0).color(fc).next();
		bufferBuilder.vertex(matrix, x + w, y + h, 0).color(fc).next();
		bufferBuilder.vertex(matrix, x + w, y, 0).color(fc).next();
		BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
		
		GL11.glEnable(GL11.GL_CULL_FACE);
		
		// outline
		bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex(matrix, x, y, 0).color(oc).next();
		bufferBuilder.vertex(matrix, x, y + h, 0).color(oc).next();
		bufferBuilder.vertex(matrix, x + w, y + h, 0).color(oc).next();
		bufferBuilder.vertex(matrix, x + w, y, 0).color(oc).next();
		bufferBuilder.vertex(matrix, x, y, 0).color(oc).next();
		BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
		
		RenderUtil.postRender();
	}
	
	public boolean shouldRender(Entity entity) {
		boolean isAlive = !entity.isRemoved() && entity.isAlive();
		boolean player = entity != mc.player || ModuleManager.getModule(Freecam.class).isEnabled() || (self.get() && mc.options.getPerspective() != Perspective.FIRST_PERSON);
		boolean b = Math.abs(entity.getY() - mc.player.getY()) <= 1e6;
		boolean shouldRender = (entity instanceof PlayerEntity) || (entity instanceof HostileEntity && hostiles.get()) || ((entity instanceof PassiveEntity || entity instanceof WaterCreatureEntity) && passives.get());
		return isEnabled() && isAlive && player && b && shouldRender;
	}
}
