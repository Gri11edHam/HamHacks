package net.grilledham.hamhacks.modules.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventRender3D;
import net.grilledham.hamhacks.event.events.EventTick;
import net.grilledham.hamhacks.modules.Category;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.modules.ModuleManager;
import net.grilledham.hamhacks.setting.BoolSetting;
import net.grilledham.hamhacks.setting.ColorSetting;
import net.grilledham.hamhacks.setting.SelectionSetting;
import net.grilledham.hamhacks.setting.SettingCategory;
import net.grilledham.hamhacks.util.Color;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Stream;

public class Tracers extends Module {
	
	private final ArrayList<LivingEntity> entities = new ArrayList<>();
	
	private final SettingCategory OPTIONS_CATEGORY = new SettingCategory("hamhacks.module.tracers.category.options");
	
	private final BoolSetting drawStem = new BoolSetting("hamhacks.module.tracers.drawStem", true, () -> true);
	
	private final SelectionSetting endPos = new SelectionSetting("hamhacks.module.tracers.endPosition", 1, () -> true, "hamhacks.module.tracers.endPosition.eyes", "hamhacks.module.tracers.endPosition.center", "hamhacks.module.tracers.endPosition.feet");
	
	private final SettingCategory PLAYERS_CATEGORY = new SettingCategory("hamhacks.module.tracers.category.players");
	
	private final BoolSetting tracePlayers = new BoolSetting("hamhacks.module.tracers.tracePlayers", true, () -> true);
	
	private final ColorSetting playerClose = new ColorSetting("hamhacks.module.tracers.playerColorClose", new Color(0xFFFF0000), tracePlayers::get);
	
	private final ColorSetting playerFar = new ColorSetting("hamhacks.module.tracers.playerColorFar", new Color(0xFF00FF00), tracePlayers::get);
	
	private final SettingCategory HOSTILE_CATEGORY = new SettingCategory("hamhacks.module.tracers.category.hostile");
	
	private final BoolSetting traceHostile = new BoolSetting("hamhacks.module.tracers.traceHostile", false, () -> true);
	
	private final ColorSetting hostileClose = new ColorSetting("hamhacks.module.tracers.hostileColorClose", new Color(0xFFFF0000), traceHostile::get);
	
	private final ColorSetting hostileFar = new ColorSetting("hamhacks.module.tracers.hostileColorFar", new Color(0xFF00FF00), traceHostile::get);
	
	private final SettingCategory PASSIVE_CATEGORY = new SettingCategory("hamhacks.module.tracers.category.passive");
	
	private final BoolSetting tracePassive = new BoolSetting("hamhacks.module.tracers.tracePassive", false, () -> true);
	
	private final ColorSetting passiveClose = new ColorSetting("hamhacks.module.tracers.passiveColorClose", new Color(0xFFFF0000), tracePassive::get);
	
	private final ColorSetting passiveFar = new ColorSetting("hamhacks.module.tracers.passiveColorFar", new Color(0xFF00FF00), tracePassive::get);
	
	public Tracers() {
		super(Text.translatable("hamhacks.module.tracers"), Category.RENDER, new Keybind(0));
		settingCategories.add(0, OPTIONS_CATEGORY);
		OPTIONS_CATEGORY.add(drawStem);
		OPTIONS_CATEGORY.add(endPos);
		settingCategories.add(1, PLAYERS_CATEGORY);
		PLAYERS_CATEGORY.add(tracePlayers);
		PLAYERS_CATEGORY.add(playerClose);
		PLAYERS_CATEGORY.add(playerFar);
		settingCategories.add(2, HOSTILE_CATEGORY);
		HOSTILE_CATEGORY.add(traceHostile);
		HOSTILE_CATEGORY.add(hostileClose);
		HOSTILE_CATEGORY.add(hostileFar);
		settingCategories.add(3, PASSIVE_CATEGORY);
		PASSIVE_CATEGORY.add(tracePassive);
		PASSIVE_CATEGORY.add(passiveClose);
		PASSIVE_CATEGORY.add(passiveFar);
	}
	
	@EventListener
	public void onRender3D(EventRender3D e) {
		MatrixStack matrixStack = e.matrices;
		float partialTicks = e.tickDelta;
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		matrixStack.push();
		matrixStack.loadIdentity();
		if(mc.options.getBobView().getValue() && !(ModuleManager.getModule(Bob.class).isEnabled() && ModuleManager.getModule(Bob.class).modelBobbingOnly.get())) {
			PlayerEntity playerEntity = (PlayerEntity)mc.getCameraEntity();
			float f = playerEntity.horizontalSpeed - playerEntity.prevHorizontalSpeed;
			float g = -(playerEntity.horizontalSpeed + f * partialTicks);
			
			float start = playerEntity.prevStrideDistance;
			float end = playerEntity.strideDistance;
			if(ModuleManager.getModule(Zoom.class).isEnabled()) {
				double divisor = Math.sqrt(ModuleManager.getModule(Zoom.class).getZoomAmount());
				start = (float)(start / divisor);
				end = (float)(end / divisor);
			}
			
			float h = MathHelper.lerp(partialTicks, start, end);
			matrixStack.translate(-MathHelper.sin(g * 3.1415927F) * h * 0.5F, Math.abs(MathHelper.cos(g * 3.1415927F) * h), 0.0F);
			matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-MathHelper.sin(g * 3.1415927F) * h * 3.0F));
			matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-Math.abs(MathHelper.cos(g * 3.1415927F - 0.2F) * h) * 5.0F));
		}
		applyCameraOffset(matrixStack);
		
		renderTracers(matrixStack, partialTicks);
		
		matrixStack.pop();
		
		// GL resets
		RenderSystem.setShaderColor(1, 1, 1, 1);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
	}
	
	@EventListener
	public void onTick(EventTick e) {
		if(mc.world == null) {
			return;
		}
		PlayerEntity player = mc.player;
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
				.filter(entity -> !entity.isRemoved() && entity.isAlive())
				.filter(entity -> entity != player || ModuleManager.getModule(Freecam.class).isEnabled())
				.filter(entity -> Math.abs(entity.getY() - mc.player.getY()) <= 1e6)
				.filter(entity -> (entity instanceof PlayerEntity && tracePlayers.get()) || (entity instanceof HostileEntity && traceHostile.get()) || ((entity instanceof PassiveEntity || entity instanceof WaterCreatureEntity) && tracePassive.get()));
		
		entities.addAll(stream.toList());
	}
	
	private void renderTracers(MatrixStack matrixStack, double partialTicks) {
		RenderSystem.setShader(GameRenderer::getPositionColorProgram);
		RenderSystem.setShaderColor(1, 1, 1, 1);
		
		Matrix4f matrix = matrixStack.peek().getPositionMatrix();
		
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
		
		Vec3d start = getClientLookVec().add(getCameraPos());
		
		for(LivingEntity e : entities) {
			if(!shouldRender(e)) continue;
			
			Vec3d interpolationOffset = new Vec3d(e.getX(), e.getY(), e.getZ()).subtract(e.prevX, e.prevY, e.prevZ).multiply(1 - partialTicks);
			Vec3d endTop = e.getBoundingBox().getCenter().add(0, e.getEyeHeight(e.getPose()) / 2, 0).subtract(interpolationOffset);
			Vec3d endCenter = e.getBoundingBox().getCenter().subtract(interpolationOffset);
			Vec3d endBottom = e.getBoundingBox().getCenter().subtract(0, e.getEyeHeight(e.getPose()) / 2, 0).subtract(interpolationOffset);
			
			Vec3d end = switch(endPos.get()) {
				case 0 -> endTop;
				case 2 -> endBottom;
				default -> endCenter;
			};
			
			float f = (float)(getCameraPos().distanceTo(e.getPos()) / 20F);
			int cClose;
			int cFar;
			if(e instanceof PlayerEntity) {
				cClose = playerClose.get().getRGB();
				cFar = playerFar.get().getRGB();
			} else if(e instanceof HostileEntity) {
				cClose = hostileClose.get().getRGB();
				cFar = hostileFar.get().getRGB();
			} else if(e instanceof PassiveEntity || e instanceof WaterCreatureEntity) {
				cClose = passiveClose.get().getRGB();
				cFar = passiveFar.get().getRGB();
			} else {
				cClose = 0x80ff0000;
				cFar = 0x8000ff00;
			}
			int c = mix(cClose, cFar, f);
			float a = (c >> 24 & 255) / 256f;
			float r = (c >> 16 & 255) / 256f;
			float g = (c >> 8 & 255) / 256f;
			float b = (c & 255) / 256f;
			
			bufferBuilder.vertex(matrix, (float)start.x, (float)start.y, (float)start.z).color(r, g, b, a).next();
			bufferBuilder.vertex(matrix, (float)end.x, (float)end.y, (float)end.z).color(r, g, b, a).next();
			
			if(drawStem.get()) {
				bufferBuilder.vertex(matrix, (float)endTop.x, (float)endTop.y, (float)endTop.z).color(r, g, b, a).next();
				bufferBuilder.vertex(matrix, (float)endBottom.x, (float)endBottom.y, (float)endBottom.z).color(r, g, b, a).next();
			}
		}
		
		BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
	}
	
	private void applyCameraOffset(MatrixStack matrixStack) {
		Vec3d camPos = getCameraPos();
		Quaternionf q = new Quaternionf();
		q.rotateXYZ((float)Math.toRadians(mc.getBlockEntityRenderDispatcher().camera.getPitch()), (float)Math.toRadians((mc.getBlockEntityRenderDispatcher().camera.getYaw()) % 360 + 180), 0);
		matrixStack.peek().getPositionMatrix().rotate(q);
		matrixStack.translate(-camPos.x, -camPos.y, -camPos.z);
	}
	
	private Vec3d getClientLookVec() {
		Camera camera = mc.getBlockEntityRenderDispatcher().camera;
		float f = -0.017453292F;
		
		float f1 = MathHelper.cos(camera.getYaw() * f);
		float f2 = MathHelper.sin(camera.getYaw() * f);
		float f3 = MathHelper.cos(camera.getPitch() * f);
		float f4 = MathHelper.sin(camera.getPitch() * f);
		
		return new Vec3d(f2 * f3, f4, f1 * f3);
	}
	
	private Vec3d getCameraPos() {
		return mc.getBlockEntityRenderDispatcher().camera.getPos();
	}
	
	private int mix(int c1, int c2, float f) {
		f = MathHelper.clamp(f, 0, 1);
		float c1a = (c1 >> 24 & 255) / 256f;
		float c1r = (c1 >> 16 & 255) / 256f;
		float c1g = (c1 >> 8 & 255) / 256f;
		float c1b = (c1 & 255) / 256f;
		float c2a = (c2 >> 24 & 255) / 256f;
		float c2r = (c2 >> 16 & 255) / 256f;
		float c2g = (c2 >> 8 & 255) / 256f;
		float c2b = (c2 & 255) / 256f;
		float finalA = c1a * (1 - f) + c2a * f;
		float finalR = c1r * (1 - f) + c2r * f;
		float finalG = c1g * (1 - f) + c2g * f;
		float finalB = c1b * (1 - f) + c2b * f;
		return ((int)(finalA * 256) << 24) + ((int)(finalR * 256) << 16) + ((int)(finalG * 256) << 8) + (int)(finalB * 256);
	}
	
	public boolean shouldRender(Entity entity) {
		boolean isAlive = !entity.isRemoved() && entity.isAlive();
		boolean player = entity != mc.player || ModuleManager.getModule(Freecam.class).isEnabled();
		boolean b = Math.abs(entity.getY() - mc.player.getY()) <= 1e6;
		boolean shouldRender = (entity instanceof PlayerEntity && tracePlayers.get()) || (entity instanceof HostileEntity && traceHostile.get()) || ((entity instanceof PassiveEntity || entity instanceof WaterCreatureEntity) && tracePassive.get());
		return isEnabled() && isAlive && player && b && shouldRender;
	}
}
