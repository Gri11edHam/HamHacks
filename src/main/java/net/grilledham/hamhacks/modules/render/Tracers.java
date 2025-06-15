package net.grilledham.hamhacks.modules.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventRender3D;
import net.grilledham.hamhacks.event.events.EventTick;
import net.grilledham.hamhacks.modules.Category;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.modules.ModuleManager;
import net.grilledham.hamhacks.setting.*;
import net.grilledham.hamhacks.util.Color;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Stream;

public class Tracers extends Module {
	
	private final ArrayList<Entity> entities = new ArrayList<>();
	
	private final SettingCategory OPTIONS_CATEGORY = new SettingCategory("hamhacks.module.tracers.category.options");
	
	private final BoolSetting drawStem = new BoolSetting("hamhacks.module.tracers.drawStem", true, () -> true);
	
	private final SelectionSetting endPos = new SelectionSetting("hamhacks.module.tracers.endPosition", 1, () -> true, "hamhacks.module.tracers.endPosition.eyes", "hamhacks.module.tracers.endPosition.center", "hamhacks.module.tracers.endPosition.feet");
	
	private final EntityTypeSelector entitySelector = new EntityTypeSelector("hamhacks.module.tracers.entitySelector", () -> true, EntityType.PLAYER);
	
	public final NumberSetting lineWidth = new NumberSetting("hamhacks.module.tracers.lineWidth", 1, () -> true, 1, 20, 1, false);
	
	private final SettingCategory COLOR_CATEGORY = new SettingCategory("hamhacks.module.tracers.category.color");
	
	private final ColorSetting playerClose = new ColorSetting("hamhacks.module.tracers.playerColorClose", new Color(0xFF00FFFF), () -> true);
	
	private final ColorSetting playerFar = new ColorSetting("hamhacks.module.tracers.playerColorFar", new Color(0xFF00FFFF), () -> true);
	
	private final ColorSetting animalClose = new ColorSetting("hamhacks.module.tracers.animalColorClose", new Color(0xFF00FF00), () -> true);
	
	private final ColorSetting animalFar = new ColorSetting("hamhacks.module.tracers.animalColorFar", new Color(0x0000FF00), () -> true);
	
	private final ColorSetting waterAnimalClose = new ColorSetting("hamhacks.module.tracers.waterAnimalColorClose", new Color(0xFF0000FF), () -> true);
	
	private final ColorSetting waterAnimalFar = new ColorSetting("hamhacks.module.tracers.waterAnimalColorFar", new Color(0x000000FF), () -> true);
	
	private final ColorSetting monsterClose = new ColorSetting("hamhacks.module.tracers.monsterColorClose", new Color(0xFFFF0000), () -> true);
	
	private final ColorSetting monsterFar = new ColorSetting("hamhacks.module.tracers.monsterColorFar", new Color(0x00FF0000), () -> true);
	
	private final ColorSetting ambientClose = new ColorSetting("hamhacks.module.tracers.ambientColorClose", new Color(0xFF000000), () -> true);
	
	private final ColorSetting ambientFar = new ColorSetting("hamhacks.module.tracers.ambientColorFar", new Color(0x00000000), () -> true);
	
	private final ColorSetting miscClose = new ColorSetting("hamhacks.module.tracers.miscColorClose", new Color(0xFFFFFFFF), () -> true);
	
	private final ColorSetting miscFar = new ColorSetting("hamhacks.module.tracers.miscColorFar", new Color(0x00FFFFFF), () -> true);
	
	public Tracers() {
		super(Text.translatable("hamhacks.module.tracers"), Category.RENDER, new Keybind(0));
		settingCategories.add(0, OPTIONS_CATEGORY);
		OPTIONS_CATEGORY.add(drawStem);
		OPTIONS_CATEGORY.add(endPos);
		OPTIONS_CATEGORY.add(entitySelector);
		OPTIONS_CATEGORY.add(lineWidth);
		settingCategories.add(1, COLOR_CATEGORY);
		COLOR_CATEGORY.add(playerClose);
		COLOR_CATEGORY.add(playerFar);
		COLOR_CATEGORY.add(animalClose);
		COLOR_CATEGORY.add(animalFar);
		COLOR_CATEGORY.add(waterAnimalClose);
		COLOR_CATEGORY.add(waterAnimalFar);
		COLOR_CATEGORY.add(monsterClose);
		COLOR_CATEGORY.add(monsterFar);
		COLOR_CATEGORY.add(ambientClose);
		COLOR_CATEGORY.add(ambientFar);
		COLOR_CATEGORY.add(miscClose);
		COLOR_CATEGORY.add(miscFar);
	}
	
	@EventListener
	public void onRender3D(EventRender3D e) {
		MatrixStack matrixStack = e.matrices;
		float partialTicks = e.tickDelta;
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		matrixStack.push();
		matrixStack.loadIdentity();
		if(mc.options.getBobView().getValue() && !(ModuleManager.getModule(Bob.class).isEnabled() && ModuleManager.getModule(Bob.class).modelBobbingOnly.get())) {
			AbstractClientPlayerEntity playerEntity = (AbstractClientPlayerEntity)mc.getCameraEntity();
			float f = playerEntity.distanceMoved - playerEntity.lastDistanceMoved;
			float g = -(playerEntity.distanceMoved + f * partialTicks);
			
			float start = playerEntity.lastStrideDistance;
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
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
	}
	
	@EventListener
	public void onTick(EventTick e) {
		if(mc.world == null) {
			return;
		}
		ClientWorld world = mc.world;
		
		entities.clear();
		Stream<Entity> stream = world.getEntitiesByType(TypeFilter.instanceOf(Entity.class), new Box(mc.player.getBlockPos().add(-256, -256, -256).toCenterPos(), mc.player.getBlockPos().add(256, 256, 256).toCenterPos()), Objects::nonNull).stream()
				.filter(this::shouldRender);
		
		entities.addAll(stream.toList());
	}
	
	private void renderTracers(MatrixStack matrixStack, double partialTicks) {
		RenderSystem.setShaderColor(1, 1, 1, 1);
		
		Matrix4f matrix = matrixStack.peek().getPositionMatrix();
		MatrixStack.Entry entry = matrixStack.peek();
		
		VertexConsumerProvider vcp = mc.getBufferBuilders().getEntityVertexConsumers();
		VertexConsumer bufferBuilder = vcp.getBuffer(RenderLayer.getDebugCrosshair(lineWidth.get()));
//		BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
		
		Vec3d start = getClientLookVec().add(getCameraPos());
		
		for(Entity e : entities) {
			if(!shouldRender(e)) continue;
			
			Vec3d interpolationOffset = new Vec3d(e.getX(), e.getY(), e.getZ()).subtract(e.lastX, e.lastY, e.lastZ).multiply(1 - partialTicks);
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
			} else {
				switch(e.getType().getSpawnGroup()) {
					case CREATURE -> {
						cClose = animalClose.get().getRGB();
						cFar = animalFar.get().getRGB();
					}
					case WATER_AMBIENT, WATER_CREATURE, UNDERGROUND_WATER_CREATURE, AXOLOTLS -> {
						cClose = waterAnimalClose.get().getRGB();
						cFar = waterAnimalFar.get().getRGB();
					}
					case MONSTER -> {
						cClose = monsterClose.get().getRGB();
						cFar = monsterFar.get().getRGB();
					}
					case AMBIENT -> {
						cClose = ambientClose.get().getRGB();
						cFar = ambientFar.get().getRGB();
					}
					case MISC -> {
						cClose = miscClose.get().getRGB();
						cFar = miscFar.get().getRGB();
					}
					default -> {
						cClose = 0xFFFFFFFF;
						cFar = 0x00FFFFFF;
					}
				}
			}
			int c = mix(cClose, cFar, f);
			float a = (c >> 24 & 255) / 256f;
			float r = (c >> 16 & 255) / 256f;
			float g = (c >> 8 & 255) / 256f;
			float b = (c & 255) / 256f;
			
			float k = (float)(end.x - start.x);
			float l = (float)(end.y - start.y);
			float m = (float)(end.z - start.z);
			float n = MathHelper.sqrt(k * k + l * l + m * m);
			k /= n;
			l /= n;
			m /= n;
			
			bufferBuilder.vertex(matrix, (float)start.x, (float)start.y, (float)start.z).color(r, g, b, a).normal(entry, k, l, m);
			bufferBuilder.vertex(matrix, (float)end.x, (float)end.y, (float)end.z).color(r, g, b, a).normal(entry, k, l, m);
			
			if(drawStem.get()) {
				k = (float)(endTop.x - endBottom.x);
				l = (float)(endTop.y - endBottom.y);
				m = (float)(endTop.z - endBottom.z);
				n = MathHelper.sqrt(k * k + l * l + m * m);
				k /= n;
				l /= n;
				m /= n;
				bufferBuilder.vertex(matrix, (float)endTop.x, (float)endTop.y, (float)endTop.z).color(r, g, b, a).normal(entry, k, l, m);
				bufferBuilder.vertex(matrix, (float)endBottom.x, (float)endBottom.y, (float)endBottom.z).color(r, g, b, a).normal(entry, k, l, m);
			}
		}
		
		mc.getBufferBuilders().getEntityVertexConsumers().draw();
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
		boolean shouldRender = entitySelector.get(entity.getType());
		return isEnabled() && isAlive && player && b && shouldRender;
	}
}
