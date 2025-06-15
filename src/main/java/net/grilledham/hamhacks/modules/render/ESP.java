package net.grilledham.hamhacks.modules.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventRender2D;
import net.grilledham.hamhacks.event.events.EventRender3D;
import net.grilledham.hamhacks.event.events.EventTick;
import net.grilledham.hamhacks.modules.Category;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.modules.ModuleManager;
import net.grilledham.hamhacks.setting.*;
import net.grilledham.hamhacks.util.Color;
import net.grilledham.hamhacks.util.ProjectionUtil;
import net.grilledham.hamhacks.util.RenderUtil;
import net.grilledham.hamhacks.util.math.Vec3;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.*;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Stream;

public class ESP extends Module {
	
	private final ArrayList<Entity> entities = new ArrayList<>();
	
	private final SettingCategory OPTIONS_CATEGORY = new SettingCategory("hamhacks.module.esp.category.options");
	
	private final SelectionSetting mode = new SelectionSetting("hamhacks.module.esp.mode", 1, () -> true, "hamhacks.module.esp.mode.2d", "hamhacks.module.esp.mode.3d");
	
	private final BoolSetting self = new BoolSetting("hamhacks.module.esp.self", true, () -> true);
	
	private final EntityTypeSelector entitySelector = new EntityTypeSelector("hamhacks.module.esp.entitySelector", () -> true, EntityType.PLAYER);
	
	private final SettingCategory COLOR_CATEGORY = new SettingCategory("hamhacks.module.esp.category.color");
	
	private final ColorSetting playerOutline = new ColorSetting("hamhacks.module.esp.playerOutlineColor", new Color(0xFF00FFFF), () -> true);
	
	private final ColorSetting playerFill = new ColorSetting("hamhacks.module.esp.playerFillColor", new Color(0x4000FFFF), () -> true);
	
	private final ColorSetting animalOutline = new ColorSetting("hamhacks.module.esp.animalOutlineColor", new Color(0xFF00FF00), () -> true);
	
	private final ColorSetting animalFill = new ColorSetting("hamhacks.module.esp.animalFillColor", new Color(0x4000FF00), () -> true);
	
	private final ColorSetting waterAnimalOutline = new ColorSetting("hamhacks.module.esp.waterAnimalOutlineColor", new Color(0xFF0000FF), () -> true);
	
	private final ColorSetting waterAnimalFill = new ColorSetting("hamhacks.module.esp.waterAnimalFillColor", new Color(0x400000FF), () -> true);
	
	private final ColorSetting monsterOutline = new ColorSetting("hamhacks.module.esp.monsterOutlineColor", new Color(0xFFFF0000), () -> true);
	
	private final ColorSetting monsterFill = new ColorSetting("hamhacks.module.esp.monsterFillColor", new Color(0x40FF0000), () -> true);
	
	private final ColorSetting ambientOutline = new ColorSetting("hamhacks.module.esp.ambientOutlineColor", new Color(0xFF000000), () -> true);
	
	private final ColorSetting ambientFill = new ColorSetting("hamhacks.module.esp.ambientFillColor", new Color(0x40000000), () -> true);
	
	private final ColorSetting miscOutline = new ColorSetting("hamhacks.module.esp.miscOutlineColor", new Color(0xFFFFFFFF), () -> true);
	
	private final ColorSetting miscFill = new ColorSetting("hamhacks.module.esp.miscFillColor", new Color(0x40FFFFFF), () -> true);
	
	public ESP() {
		super(Text.translatable("hamhacks.module.esp"), Category.RENDER, new Keybind(0));
		settingCategories.add(0, OPTIONS_CATEGORY);
		OPTIONS_CATEGORY.add(mode);
		OPTIONS_CATEGORY.add(self);
		OPTIONS_CATEGORY.add(entitySelector);
		settingCategories.add(1, COLOR_CATEGORY);
		COLOR_CATEGORY.add(playerOutline);
		COLOR_CATEGORY.add(playerFill);
		COLOR_CATEGORY.add(animalOutline);
		COLOR_CATEGORY.add(animalFill);
		COLOR_CATEGORY.add(waterAnimalOutline);
		COLOR_CATEGORY.add(waterAnimalFill);
		COLOR_CATEGORY.add(monsterOutline);
		COLOR_CATEGORY.add(monsterFill);
		COLOR_CATEGORY.add(ambientOutline);
		COLOR_CATEGORY.add(ambientFill);
		COLOR_CATEGORY.add(miscOutline);
		COLOR_CATEGORY.add(miscFill);
	}
	
	@EventListener
	public void onRender3D(EventRender3D e) {
		if(mode.get() == 0) {
			return;
		}
		
		MatrixStack matrixStack = e.matrices;
		float partialTicks = e.tickDelta;
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_CULL_FACE);
		
		matrixStack.push();
		
		matrixStack.loadIdentity();
		applyCameraOffset(matrixStack);
		
		render3D(matrixStack, partialTicks);
		
		matrixStack.pop();
		
		// GL resets
		RenderSystem.setShaderColor(1, 1, 1, 1);
	}
	
	@EventListener
	public void onRender2D(EventRender2D e) {
		if(mode.get() != 0) {
			return;
		}
		
		MatrixStack matrixStack = e.matrices;
		float partialTicks = e.tickDelta;
		
		RenderUtil.preRender();
		matrixStack.push();
		
		matrixStack.loadIdentity();
		matrixStack.scale((float)(1 / mc.getWindow().getScaleFactor()), (float)(1 / mc.getWindow().getScaleFactor()), 1);
		
		render2D(matrixStack, partialTicks);
		
		matrixStack.pop();
		RenderUtil.postRender();
	}
	
	@EventListener
	public void onTick(EventTick e) {
		if(mc.world == null) {
			return;
		}
		ClientWorld world = mc.world;
		
		entities.clear();
		Stream<Entity> stream = world.getEntitiesByType(TypeFilter.instanceOf(Entity.class), new Box(mc.player.getBlockPos().add(-256, -256, -256).toCenterPos(), mc.player.getBlockPos().add(256, 256, 256).toCenterPos()), Objects::nonNull).stream()
				.filter(this::shouldRender)
				.sorted((a, b) -> Double.compare(b.squaredDistanceTo(mc.getCameraEntity().getEyePos()), a.squaredDistanceTo(mc.getCameraEntity().getEyePos())));
		
		entities.addAll(stream.toList());
	}
	
	private void render2D(MatrixStack matrixStack, double partialTicks) {
		RenderSystem.setShaderColor(1, 1, 1, 1);
		
		matrixStack.push();
		matrixStack.translate(0, 0, -entities.size());
		for(Entity e : entities) {
			if(!shouldRender(e)) continue;
			
			Vec3 interpolationOffset = new Vec3(e.getX(), e.getY(), e.getZ()).sub(e.lastX, e.lastY, e.lastZ).mul(1 - partialTicks);
			Box box = e.getBoundingBox();
			float x = (float)(e.getX() - interpolationOffset.getX());
			float y = (float)(e.getY() - interpolationOffset.getY());
			float z = (float)(e.getZ() - interpolationOffset.getZ());
			if(e instanceof LivingEntity le) {
				box = le.getBoundingBox(le.getPose());
			} else {
				x -= (float)getCameraPos().x;
				y -= (float)getCameraPos().y;
				z -= (float)getCameraPos().z;
			}
			
			Vec3 pos1 = new Vec3(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
			Vec3 pos2 = new Vec3(0, 0, 0);
			
			if(checkPos(box.minX + x, box.minY + y, box.minZ + z, pos1, pos2)) continue;
			if(checkPos(box.maxX + x, box.minY + y, box.minZ + z, pos1, pos2)) continue;
			if(checkPos(box.minX + x, box.minY + y, box.maxZ + z, pos1, pos2)) continue;
			if(checkPos(box.maxX + x, box.minY + y, box.maxZ + z, pos1, pos2)) continue;
			
			if(checkPos(box.minX + x, box.maxY + y, box.minZ + z, pos1, pos2)) continue;
			if(checkPos(box.maxX + x, box.maxY + y, box.minZ + z, pos1, pos2)) continue;
			if(checkPos(box.minX + x, box.maxY + y, box.maxZ + z, pos1, pos2)) continue;
			if(checkPos(box.maxX + x, box.maxY + y, box.maxZ + z, pos1, pos2)) continue;
			
			int oc;
			int fc;
			if(e instanceof PlayerEntity) {
				oc = playerOutline.get().getRGB();
				fc = playerFill.get().getRGB();
			} else {
				switch(e.getType().getSpawnGroup()) {
					case CREATURE -> {
						oc = animalOutline.get().getRGB();
						fc = animalFill.get().getRGB();
					}
					case WATER_AMBIENT, WATER_CREATURE, UNDERGROUND_WATER_CREATURE, AXOLOTLS -> {
						oc = waterAnimalOutline.get().getRGB();
						fc = waterAnimalFill.get().getRGB();
					}
					case MONSTER -> {
						oc = monsterOutline.get().getRGB();
						fc = monsterFill.get().getRGB();
					}
					case AMBIENT -> {
						oc = ambientOutline.get().getRGB();
						fc = ambientFill.get().getRGB();
					}
					case MISC -> {
						oc = miscOutline.get().getRGB();
						fc = miscFill.get().getRGB();
					}
					default -> {
						oc = 0xFFFFFFFF;
						fc = 0x40FFFFFF;
					}
				}
			}
			
			Matrix4f matrix = matrixStack.peek().getPositionMatrix();
			
			GL11.glDisable(GL11.GL_CULL_FACE);
			
			VertexConsumerProvider vcp = mc.getBufferBuilders().getEntityVertexConsumers();
			// fill
			VertexConsumer bufferBuilder = vcp.getBuffer(RenderLayer.getDebugQuads());
//			BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
			bufferBuilder.vertex(matrix, (float)pos1.getX(), (float)pos1.getY(), 0).color(fc);
			bufferBuilder.vertex(matrix, (float)pos1.getX(), (float)pos2.getY(), 0).color(fc);
			bufferBuilder.vertex(matrix, (float)pos2.getX(), (float)pos2.getY(), 0).color(fc);
			bufferBuilder.vertex(matrix, (float)pos2.getX(), (float)pos1.getY(), 0).color(fc);
			
			GL11.glEnable(GL11.GL_CULL_FACE);
			
			// outline
			bufferBuilder = vcp.getBuffer(RenderLayer.getDebugLineStrip(1));
//			bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
			bufferBuilder.vertex(matrix, (float)pos1.getX(), (float)pos1.getY(), 0).color(oc);
			bufferBuilder.vertex(matrix, (float)pos1.getX(), (float)pos2.getY(), 0).color(oc);
			bufferBuilder.vertex(matrix, (float)pos2.getX(), (float)pos2.getY(), 0).color(oc);
			bufferBuilder.vertex(matrix, (float)pos2.getX(), (float)pos1.getY(), 0).color(oc);
			bufferBuilder.vertex(matrix, (float)pos1.getX(), (float)pos1.getY(), 0).color(oc);
			
			mc.getBufferBuilders().getEntityVertexConsumers().draw();
			
			matrixStack.translate(0, 0, 1);
		}
		matrixStack.pop();
	}
	
	private boolean checkPos(double x, double y, double z, Vec3 min, Vec3 max) {
		Vec3 pos = new Vec3(x, y, z);
		
		if(!ProjectionUtil.to2D(pos, 1, false)) return true;
		
		if (pos.getX() < min.getX()) min.setX(pos.getX());
		if (pos.getY() < min.getY()) min.setY(pos.getY());
		if (pos.getZ() < min.getZ()) min.setZ(pos.getZ());
		
		if (pos.getX() > max.getX()) max.setX(pos.getX());
		if (pos.getY() > max.getY()) max.setY(pos.getY());
		if (pos.getZ() > max.getZ()) max.setZ(pos.getZ());
		return false;
	}
	
	private void render3D(MatrixStack matrixStack, double partialTicks) {
		RenderSystem.setShaderColor(1, 1, 1, 1);
		
		Matrix4f matrix = matrixStack.peek().getPositionMatrix();
		
		for(Entity e : entities) {
			if(!shouldRender(e)) continue;
			
			Vec3d interpolationOffset = new Vec3d(e.getX(), e.getY(), e.getZ()).subtract(e.lastX, e.lastY, e.lastZ).multiply(1 - partialTicks);
			Box box = e.getBoundingBox();
			if(e instanceof LivingEntity le) {
				box = le.getBoundingBox(le.getPose());
			}
			float x1 = (float)(box.minX + e.getX() - interpolationOffset.getX());
			float y1 = (float)(box.minY + e.getY() - interpolationOffset.getY());
			float z1 = (float)(box.minZ + e.getZ() - interpolationOffset.getZ());
			float x2 = (float)(box.maxX + e.getX() - interpolationOffset.getX());
			float y2 = (float)(box.maxY + e.getY() - interpolationOffset.getY());
			float z2 = (float)(box.maxZ + e.getZ() - interpolationOffset.getZ());
			if(!(e instanceof LivingEntity)) {
				x1 -= (float)getCameraPos().x;
				x2 -= (float)getCameraPos().x;
				y1 -= (float)getCameraPos().y;
				y2 -= (float)getCameraPos().y;
				z1 -= (float)getCameraPos().z;
				z2 -= (float)getCameraPos().z;
			}
			
			int oc;
			int fc;
			if(e instanceof PlayerEntity) {
				oc = playerOutline.get().getRGB();
				fc = playerFill.get().getRGB();
			} else {
				switch(e.getType().getSpawnGroup()) {
					case CREATURE -> {
						oc = animalOutline.get().getRGB();
						fc = animalFill.get().getRGB();
					}
					case WATER_AMBIENT, WATER_CREATURE, UNDERGROUND_WATER_CREATURE, AXOLOTLS -> {
						oc = waterAnimalOutline.get().getRGB();
						fc = waterAnimalFill.get().getRGB();
					}
					case MONSTER -> {
						oc = monsterOutline.get().getRGB();
						fc = monsterFill.get().getRGB();
					}
					case AMBIENT -> {
						oc = ambientOutline.get().getRGB();
						fc = ambientFill.get().getRGB();
					}
					case MISC -> {
						oc = miscOutline.get().getRGB();
						fc = miscFill.get().getRGB();
					}
					default -> {
						oc = 0xFFFFFFFF;
						fc = 0x40FFFFFF;
					}
				}
			}
			
			VertexConsumerProvider vcp = mc.getBufferBuilders().getEntityVertexConsumers();
			// fill
			VertexConsumer bufferBuilder = vcp.getBuffer(RenderLayer.getDebugQuads());
//			BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
			// top
			bufferBuilder.vertex(matrix, x1, y1, z1).color(fc);
			bufferBuilder.vertex(matrix, x1, y1, z2).color(fc);
			bufferBuilder.vertex(matrix, x2, y1, z2).color(fc);
			bufferBuilder.vertex(matrix, x2, y1, z1).color(fc);
			// bottom
			bufferBuilder.vertex(matrix, x1, y2, z1).color(fc);
			bufferBuilder.vertex(matrix, x1, y2, z2).color(fc);
			bufferBuilder.vertex(matrix, x2, y2, z2).color(fc);
			bufferBuilder.vertex(matrix, x2, y2, z1).color(fc);
			// front
			bufferBuilder.vertex(matrix, x1, y1, z1).color(fc);
			bufferBuilder.vertex(matrix, x1, y2, z1).color(fc);
			bufferBuilder.vertex(matrix, x2, y2, z1).color(fc);
			bufferBuilder.vertex(matrix, x2, y1, z1).color(fc);
			// back
			bufferBuilder.vertex(matrix, x1, y1, z2).color(fc);
			bufferBuilder.vertex(matrix, x1, y2, z2).color(fc);
			bufferBuilder.vertex(matrix, x2, y2, z2).color(fc);
			bufferBuilder.vertex(matrix, x2, y1, z2).color(fc);
			// left
			bufferBuilder.vertex(matrix, x1, y1, z1).color(fc);
			bufferBuilder.vertex(matrix, x1, y2, z1).color(fc);
			bufferBuilder.vertex(matrix, x1, y2, z2).color(fc);
			bufferBuilder.vertex(matrix, x1, y1, z2).color(fc);
			// right
			bufferBuilder.vertex(matrix, x2, y1, z1).color(fc);
			bufferBuilder.vertex(matrix, x2, y2, z1).color(fc);
			bufferBuilder.vertex(matrix, x2, y2, z2).color(fc);
			bufferBuilder.vertex(matrix, x2, y1, z2).color(fc);
			
			// outline
			bufferBuilder = vcp.getBuffer(RenderLayer.getDebugCrosshair(1));
//			bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
			// faces
			// top
			VoxelShape shape = VoxelShapes.cuboid(x1, y1, z1, x2, y2, z2);
			VertexConsumer buf = bufferBuilder;
			MatrixStack.Entry entry = matrixStack.peek();
			shape.forEachEdge((minX, minY, minZ, maxX, maxY, maxZ) -> {
				float k = (float)(maxX - minX);
				float l = (float)(maxY - minY);
				float m = (float)(maxZ - minZ);
				float n = MathHelper.sqrt(k * k + l * l + m * m);
				k /= n;
				l /= n;
				m /= n;
				buf.vertex(entry.getPositionMatrix(), (float)minX, (float)minY, (float)minZ).color(oc).normal(entry, k, l, m);
				buf.vertex(entry.getPositionMatrix(), (float)maxX, (float)maxY, (float)maxZ).color(oc).normal(entry, k, l, m);
			});
			
			mc.getBufferBuilders().getEntityVertexConsumers().draw();
		}
	}
	
	private void applyCameraOffset(MatrixStack matrixStack) {
		Vec3d camPos = getCameraPos();
		Quaternionf q = new Quaternionf();
		q.rotateXYZ((float)Math.toRadians(mc.getBlockEntityRenderDispatcher().camera.getPitch()), (float)Math.toRadians((mc.getBlockEntityRenderDispatcher().camera.getYaw()) % 360 + 180), 0);
		matrixStack.peek().getPositionMatrix().rotate(q);
		matrixStack.translate(-camPos.x, -camPos.y, -camPos.z);
	}
	
	private Vec3d getCameraPos() {
		return mc.getBlockEntityRenderDispatcher().camera.getPos();
	}
	
	public boolean shouldRender(Entity entity) {
		boolean isAlive = !entity.isRemoved() && entity.isAlive();
		boolean player = entity != mc.player || ModuleManager.getModule(Freecam.class).isEnabled() || (self.get() && mc.options.getPerspective() != Perspective.FIRST_PERSON);
		boolean b = Math.abs(entity.getY() - mc.player.getY()) <= 1e6;
		boolean shouldRender = entitySelector.get(entity.getType());
		return isEnabled() && isAlive && player && b && shouldRender;
	}
}
