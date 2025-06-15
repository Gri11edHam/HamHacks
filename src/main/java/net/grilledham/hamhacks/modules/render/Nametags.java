package net.grilledham.hamhacks.modules.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventRender2D;
import net.grilledham.hamhacks.event.events.EventTick;
import net.grilledham.hamhacks.modules.Category;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.modules.ModuleManager;
import net.grilledham.hamhacks.modules.misc.NameHider;
import net.grilledham.hamhacks.setting.*;
import net.grilledham.hamhacks.util.Color;
import net.grilledham.hamhacks.util.EnchantUtil;
import net.grilledham.hamhacks.util.ProjectionUtil;
import net.grilledham.hamhacks.util.RenderUtil;
import net.grilledham.hamhacks.util.math.Vec3;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.text.Text;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class Nametags extends Module {
	
	private final ArrayList<Entity> entities = new ArrayList<>();
	
	private final Map<Entity, String> names = new HashMap<>();
	
	private final SettingCategory ENTITIES_CATEGORY = new SettingCategory("hamhacks.module.nametags.category.entities");
	
	private final BoolSetting self = new BoolSetting("hamhacks.module.nametags.self", true, () -> true);
	
	private final EntityTypeSelector entitySelector = new EntityTypeSelector("hamhacks.module.nametags.entitySelector", () -> true, EntityType.PLAYER);
	
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
	
	public final NumberSetting lineWidth = new NumberSetting("hamhacks.module.nametags.lineWidth", 1, () -> true, 1, 20, 1, false);
	
	public Nametags() {
		super(Text.translatable("hamhacks.module.nametags"), Category.RENDER, new Keybind(0));
		settingCategories.add(0, ENTITIES_CATEGORY);
		ENTITIES_CATEGORY.add(self);
		ENTITIES_CATEGORY.add(entitySelector);
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
		APPEARANCE_CATEGORY.add(lineWidth);
	}
	
	@EventListener
	public void onRender2D(EventRender2D e) {
		MatrixStack matrixStack = e.matrices;
		float partialTicks = e.tickDelta;
		
		matrixStack.push();
		
		matrixStack.loadIdentity();
		matrixStack.scale((float)(1 / mc.getWindow().getScaleFactor()), (float)(1 / mc.getWindow().getScaleFactor()), 1);
		
		render(e.context, partialTicks);
		
		matrixStack.pop();
	}
	
	@EventListener
	public void onTick(EventTick e) {
		if(mc.world == null) {
			return;
		}
		ClientWorld world = mc.world;
		
		entities.clear();
		Stream<Entity> stream = world.getEntitiesByType(TypeFilter.instanceOf(Entity.class), new Box(mc.player.getBlockPos().add(-256, -256, -256).toCenterPos(), mc.player.getBlockPos().add(256, 256, 256).toCenterPos()), Objects::nonNull).stream()
				.filter(this::shouldRender).sorted((a, b) -> Double.compare(b.squaredDistanceTo(mc.getCameraEntity().getEyePos()), a.squaredDistanceTo(mc.getCameraEntity().getEyePos())));
		
		entities.addAll(stream.toList());
		
		names.clear();
		for(Entity entity : entities) {
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
				name = ModuleManager.getModule(NameHider.class).modifyName(entity.getName().getString());
			} else {
				name = entity.getName().getString();
			}
			String nameColor = "\u00a7f";
			if(entity.isSneaking()) {
				nameColor = "\u00a77";
			}
			name = nameColor + name + " ";
			
			float hp = 0;
			float healthPercentage = 100;
			if(entity instanceof LivingEntity living) {
				hp = living.getHealth() + living.getAbsorptionAmount();
				healthPercentage = Math.round((hp / living.getMaxHealth()) * 1000) / 10f;
			}
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
	
	private void render(DrawContext ctx, double partialTicks) {
		MatrixStack matrixStack = ctx.getMatrices();
		RenderSystem.setShaderColor(1, 1, 1, 1);
		
		matrixStack.push();
		matrixStack.translate(0, 0, -entities.size());
		for(Entity e : entities) {
			if(!shouldRender(e)) continue;
			
			Vec3 interpolationOffset = new Vec3(e.getX(), e.getY(), e.getZ()).sub(e.lastX, e.lastY, e.lastZ).mul(1 - partialTicks);
			float ex = (float)(e.getX() - interpolationOffset.getX());
			float ey = (float)(e.getY() - interpolationOffset.getY());
			float ez = (float)(e.getZ() - interpolationOffset.getZ());
			
			Vec3 pos = new Vec3(ex, ey + e.getHeight() + 0.2, ez);
			
			if(ProjectionUtil.to2D(pos, scale.get() * (scaleWithZoom.get() ? ModuleManager.getModule(Zoom.class).getZoomAmount() : 1), true)) {
				TextRenderer textRenderer = mc.textRenderer;
				
				String display = names.get(e);
				
				float width = RenderUtil.getStringWidth(display);
				
				float xCenter = width / 2;
				float height = RenderUtil.getFontHeight();
				
				matrixStack.push();
				matrixStack.translate(pos.getX(), pos.getY(), 0);
				matrixStack.scale((float)ProjectionUtil.scale, (float)ProjectionUtil.scale, 1);
				
				Matrix4f matrix = matrixStack.peek().getPositionMatrix();
				
				drawBackground(matrix, -xCenter - 2, -height - 2, width + 3, height + 3);
				
				RenderUtil.drawString(ctx, display, -xCenter, -height, -1, true);
				
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
							ItemEnchantmentsComponent enchantments = EnchantmentHelper.getEnchantments(stack);
							
							int size = 0;
							for(RegistryEntry<Enchantment> enchantment : enchantments.getEnchantments()) {
								String enchantName = EnchantUtil.getShortName(enchantment) + " " + enchantments.getLevel(enchantment);
								itemWidths[i] = Math.max(itemWidths[i], RenderUtil.getStringWidth(enchantName + " "));
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
						
						RenderUtil.drawItem(ctx, stack, x + itemWidths[i] / 2, y, (float)(double)itemScale.get(), true, true);
						
						if(enchantCount > 0 && enchants.get() && !stack.isEmpty()) {
							ItemEnchantmentsComponent enchantments = EnchantmentHelper.getEnchantments(stack);
							
							float itemWidth = itemWidths[i];
							float enchantY =  -itemsHeight / 2 - (enchantments.getSize() * RenderUtil.getFontHeight()) + 12;
							float enchantX;
							
							for(RegistryEntry<Enchantment> enchantment : enchantments.getEnchantments()) {
								String enchantColor = enchantment.streamTags().toList().contains(EnchantmentTags.CURSE) ? "\u00a7c" : "\u00a7f";
								String enchantName = enchantColor + EnchantUtil.getShortName(enchantment) + " " + enchantments.getLevel(enchantment);
								
								enchantX = x + (itemWidth / 2) - (RenderUtil.getStringWidth(enchantName) / 2f) + 8;
								
								matrixStack.translate(0, 0, 300);
								RenderUtil.drawString(ctx, enchantName, enchantX, y + enchantY, -1, true);
								matrixStack.translate(0, 0, -300);
								
								enchantY += RenderUtil.getFontHeight();
							}
						}
						x += itemWidths[i];
					}
				}
				
				matrixStack.pop();
			}
			matrixStack.translate(0, 0, 1);
		}
		matrixStack.pop();
	}
	
	private ItemStack getItem(Entity entity, int index) {
		if(entity instanceof LivingEntity living) {
			return switch(index) {
				case 0 -> living.getMainHandStack();
				case 1 -> living.getOffHandStack();
				case 2 -> living.getEquippedStack(EquipmentSlot.HEAD);
				case 3 -> living.getEquippedStack(EquipmentSlot.BODY);
				case 4 -> living.getEquippedStack(EquipmentSlot.LEGS);
				case 5 -> living.getEquippedStack(EquipmentSlot.FEET);
				default -> ItemStack.EMPTY;
			};
		}
		return ItemStack.EMPTY;
	}
	
	private void drawBackground(Matrix4f matrix, float x, float y, float w, float h) {
		int oc = outlineColor.get().getRGB();
		int fc = fillColor.get().getRGB();
		
		RenderUtil.preRender();
		
		VertexConsumerProvider vcp = mc.getBufferBuilders().getEntityVertexConsumers();
		// fill
		VertexConsumer bufferBuilder = vcp.getBuffer(RenderLayer.getDebugQuads());
//		BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex(matrix, x, y, 0).color(fc);
		bufferBuilder.vertex(matrix, x, y + h, 0).color(fc);
		bufferBuilder.vertex(matrix, x + w, y + h, 0).color(fc);
		bufferBuilder.vertex(matrix, x + w, y, 0).color(fc);
		
		GL11.glEnable(GL11.GL_CULL_FACE);
		
		// outline
		bufferBuilder = vcp.getBuffer(RenderLayer.getDebugLineStrip(lineWidth.get()));
//		bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex(matrix, x, y, 0).color(oc);
		bufferBuilder.vertex(matrix, x, y + h, 0).color(oc);
		bufferBuilder.vertex(matrix, x + w, y + h, 0).color(oc);
		bufferBuilder.vertex(matrix, x + w, y, 0).color(oc);
		bufferBuilder.vertex(matrix, x, y, 0).color(oc);
		
		mc.getBufferBuilders().getEntityVertexConsumers().draw();
		
		RenderUtil.postRender();
	}
	
	public boolean shouldRender(Entity entity) {
		boolean isAlive = !entity.isRemoved() && entity.isAlive();
		boolean player = entity != mc.player || ModuleManager.getModule(Freecam.class).isEnabled() || (self.get() && mc.options.getPerspective() != Perspective.FIRST_PERSON);
		boolean b = Math.abs(entity.getY() - mc.player.getY()) <= 1e6;
		boolean shouldRender = entitySelector.get(entity.getType());
		return isEnabled() && isAlive && player && b && shouldRender;
	}
}
