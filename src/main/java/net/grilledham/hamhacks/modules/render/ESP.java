package net.grilledham.hamhacks.modules.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventRender2D;
import net.grilledham.hamhacks.event.events.EventRender3D;
import net.grilledham.hamhacks.event.events.EventTick;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.util.Color;
import net.grilledham.hamhacks.util.ProjectionUtil;
import net.grilledham.hamhacks.util.SelectableList;
import net.grilledham.hamhacks.util.math.Vec3;
import net.grilledham.hamhacks.util.setting.BoolSetting;
import net.grilledham.hamhacks.util.setting.ColorSetting;
import net.grilledham.hamhacks.util.setting.SelectionSetting;
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
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Stream;

public class ESP extends Module {
	
	private final ArrayList<LivingEntity> entities = new ArrayList<>();
	
	@SelectionSetting(name = "hamhacks.module.esp.mode")
	public SelectableList mode = new SelectableList("hamhacks.module.esp.mode.3d", "hamhacks.module.esp.mode.2d", "hamhacks.module.esp.mode.3d");
	
	@BoolSetting(name = "hamhacks.module.esp.players", defaultValue = true)
	public boolean players = true;
	
	@BoolSetting(name = "hamhacks.module.esp.self", defaultValue = true, dependsOn = "players")
	public boolean self = true;
	
	@ColorSetting(name = "hamhacks.module.esp.playerOutlineColor", dependsOn = "players")
	public Color playerOutline = new Color(0xFF00FFFF);
	
	@ColorSetting(name = "hamhacks.module.esp.playerFillColor", dependsOn = "players")
	public Color playerFill = new Color(0x4000FFFF);
	
	@BoolSetting(name = "hamhacks.module.esp.hostiles")
	public boolean hostiles = false;
	
	@ColorSetting(name = "hamhacks.module.esp.hostileOutlineColor", dependsOn = "hostiles")
	public Color hostileOutline = new Color(0xFFFF0000);
	
	@ColorSetting(name = "hamhacks.module.esp.hostileFillColor", dependsOn = "hostiles")
	public Color hostileFill = new Color(0x40FF0000);
	
	@BoolSetting(name = "hamhacks.module.esp.passives")
	public boolean passives = false;
	
	@ColorSetting(name = "hamhacks.module.esp.passiveOutlineColor", dependsOn = "passives")
	public Color passiveOutline = new Color(0xFF00FF00);
	
	@ColorSetting(name = "hamhacks.module.esp.passiveFillColor", dependsOn = "passives")
	public Color passiveFill = new Color(0x4000FF00);
	
	public ESP() {
		super(Text.translatable("hamhacks.module.esp"), Category.RENDER, new Keybind(0));
	}
	
	@EventListener
	public void onRender3D(EventRender3D e) {
		if(mode.get().equals("hamhacks.module.esp.mode.2d")) {
			return;
		}
		
		MatrixStack matrixStack = e.matrices;
		float partialTicks = e.tickDelta;
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		matrixStack.push();
		
		matrixStack.loadIdentity();
		applyCameraOffset(matrixStack);
		
		render3D(matrixStack, partialTicks);
		
		matrixStack.pop();
		
		// GL resets
		RenderSystem.setShaderColor(1, 1, 1, 1);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
	}
	
	@EventListener
	public void onRender2D(EventRender2D e) {
		if(!mode.get().equals("hamhacks.module.esp.mode.2d")) {
			return;
		}
		
		MatrixStack matrixStack = e.matrices;
		float partialTicks = e.tickDelta;
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		matrixStack.push();
		
		matrixStack.loadIdentity();
		matrixStack.scale((float)(1 / mc.getWindow().getScaleFactor()), (float)(1 / mc.getWindow().getScaleFactor()), 1);
		
		render2D(matrixStack, partialTicks);
		
		matrixStack.pop();
		
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
				.filter(entity -> entity != player || Freecam.getInstance().isEnabled() || self)
				.filter(entity -> Math.abs(entity.getY() - mc.player.getY()) <= 1e6)
				.filter(entity -> (entity instanceof PlayerEntity && players) || (entity instanceof HostileEntity && hostiles) || ((entity instanceof PassiveEntity || entity instanceof WaterCreatureEntity) && passives));
		
		entities.addAll(stream.toList());
	}
	
	private void render2D(MatrixStack matrixStack, double partialTicks) {
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		RenderSystem.setShaderColor(1, 1, 1, 1);
		
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		
		for(LivingEntity e : entities) {
			Vec3 interpolationOffset = new Vec3(e.getX(), e.getY(), e.getZ()).sub(e.prevX, e.prevY, e.prevZ).mul(1 - partialTicks);
			Box box = e.getBoundingBox(e.getPose());
			float x = (float)(e.getX() - interpolationOffset.getX());
			float y = (float)(e.getY() - interpolationOffset.getY());
			float z = (float)(e.getZ() - interpolationOffset.getZ());
			
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
				oc = playerOutline.getRGB();
				fc = playerFill.getRGB();
			} else if(e instanceof HostileEntity) {
				oc = hostileOutline.getRGB();
				fc = hostileFill.getRGB();
			} else if(e instanceof PassiveEntity || e instanceof WaterCreatureEntity) {
				oc = passiveOutline.getRGB();
				fc = passiveFill.getRGB();
			} else {
				oc = 0xFFFFFFFF;
				fc = 0x40FFFFFF;
			}
			
			Matrix4f matrix = matrixStack.peek().getPositionMatrix();
			
			GL11.glDisable(GL11.GL_CULL_FACE);
			
			// fill
			bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
			bufferBuilder.vertex(matrix, (float)pos1.getX(), (float)pos1.getY(), 0).color(fc).next();
			bufferBuilder.vertex(matrix, (float)pos1.getX(), (float)pos2.getY(), 0).color(fc).next();
			bufferBuilder.vertex(matrix, (float)pos2.getX(), (float)pos2.getY(), 0).color(fc).next();
			bufferBuilder.vertex(matrix, (float)pos2.getX(), (float)pos1.getY(), 0).color(fc).next();
			BufferRenderer.drawWithShader(bufferBuilder.end());
			
			GL11.glEnable(GL11.GL_CULL_FACE);
			
			// outline
			bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
			bufferBuilder.vertex(matrix, (float)pos1.getX(), (float)pos1.getY(), 0).color(oc).next();
			bufferBuilder.vertex(matrix, (float)pos1.getX(), (float)pos2.getY(), 0).color(oc).next();
			bufferBuilder.vertex(matrix, (float)pos2.getX(), (float)pos2.getY(), 0).color(oc).next();
			bufferBuilder.vertex(matrix, (float)pos2.getX(), (float)pos1.getY(), 0).color(oc).next();
			bufferBuilder.vertex(matrix, (float)pos1.getX(), (float)pos1.getY(), 0).color(oc).next();
			BufferRenderer.drawWithShader(bufferBuilder.end());
		}
	}
	
	private boolean checkPos(double x, double y, double z, Vec3 min, Vec3 max) {
		Vec3 pos = new Vec3(x, y, z);
		
		if(!ProjectionUtil.to2D(pos)) return true;
		
		if (pos.getX() < min.getX()) min.setX(pos.getX());
		if (pos.getY() < min.getY()) min.setY(pos.getY());
		if (pos.getZ() < min.getZ()) min.setZ(pos.getZ());
		
		if (pos.getX() > max.getX()) max.setX(pos.getX());
		if (pos.getY() > max.getY()) max.setY(pos.getY());
		if (pos.getZ() > max.getZ()) max.setZ(pos.getZ());
		return false;
	}
	
	private void render3D(MatrixStack matrixStack, double partialTicks) {
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		RenderSystem.setShaderColor(1, 1, 1, 1);
		
		Matrix4f matrix = matrixStack.peek().getPositionMatrix();
		
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		
		for(LivingEntity e : entities) {
			Vec3d interpolationOffset = new Vec3d(e.getX(), e.getY(), e.getZ()).subtract(e.prevX, e.prevY, e.prevZ).multiply(1 - partialTicks);
			Box box = e.getBoundingBox(e.getPose());
			float x1 = (float)(box.minX + e.getX() - interpolationOffset.getX());
			float y1 = (float)(box.minY + e.getY() - interpolationOffset.getY());
			float z1 = (float)(box.minZ + e.getZ() - interpolationOffset.getZ());
			float x2 = (float)(box.maxX + e.getX() - interpolationOffset.getX());
			float y2 = (float)(box.maxY + e.getY() - interpolationOffset.getY());
			float z2 = (float)(box.maxZ + e.getZ() - interpolationOffset.getZ());
			
			int oc;
			int fc;
			if(e instanceof PlayerEntity) {
				oc = playerOutline.getRGB();
				fc = playerFill.getRGB();
			} else if(e instanceof HostileEntity) {
				oc = hostileOutline.getRGB();
				fc = hostileFill.getRGB();
			} else if(e instanceof PassiveEntity || e instanceof WaterCreatureEntity) {
				oc = passiveOutline.getRGB();
				fc = passiveFill.getRGB();
			} else {
				oc = 0xFFFFFFFF;
				fc = 0x40FFFFFF;
			}
			
			GL11.glDisable(GL11.GL_CULL_FACE);
			
			// fill
			bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
			// top
			bufferBuilder.vertex(matrix, x1, y1, z1).color(fc).next();
			bufferBuilder.vertex(matrix, x1, y1, z2).color(fc).next();
			bufferBuilder.vertex(matrix, x2, y1, z2).color(fc).next();
			bufferBuilder.vertex(matrix, x2, y1, z1).color(fc).next();
			// bottom
			bufferBuilder.vertex(matrix, x1, y2, z1).color(fc).next();
			bufferBuilder.vertex(matrix, x1, y2, z2).color(fc).next();
			bufferBuilder.vertex(matrix, x2, y2, z2).color(fc).next();
			bufferBuilder.vertex(matrix, x2, y2, z1).color(fc).next();
			// front
			bufferBuilder.vertex(matrix, x1, y1, z1).color(fc).next();
			bufferBuilder.vertex(matrix, x1, y2, z1).color(fc).next();
			bufferBuilder.vertex(matrix, x2, y2, z1).color(fc).next();
			bufferBuilder.vertex(matrix, x2, y1, z1).color(fc).next();
			// back
			bufferBuilder.vertex(matrix, x1, y1, z2).color(fc).next();
			bufferBuilder.vertex(matrix, x1, y2, z2).color(fc).next();
			bufferBuilder.vertex(matrix, x2, y2, z2).color(fc).next();
			bufferBuilder.vertex(matrix, x2, y1, z2).color(fc).next();
			// left
			bufferBuilder.vertex(matrix, x1, y1, z1).color(fc).next();
			bufferBuilder.vertex(matrix, x1, y2, z1).color(fc).next();
			bufferBuilder.vertex(matrix, x1, y2, z2).color(fc).next();
			bufferBuilder.vertex(matrix, x1, y1, z2).color(fc).next();
			// right
			bufferBuilder.vertex(matrix, x2, y1, z1).color(fc).next();
			bufferBuilder.vertex(matrix, x2, y2, z1).color(fc).next();
			bufferBuilder.vertex(matrix, x2, y2, z2).color(fc).next();
			bufferBuilder.vertex(matrix, x2, y1, z2).color(fc).next();
			
			BufferRenderer.drawWithShader(bufferBuilder.end());
			
			GL11.glEnable(GL11.GL_CULL_FACE);
			
			// outline
			bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
			// faces
			// top
			bufferBuilder.vertex(matrix, x1, y1, z1).color(oc).next();
			bufferBuilder.vertex(matrix, x1, y1, z2).color(oc).next();
			
			bufferBuilder.vertex(matrix, x1, y1, z2).color(oc).next();
			bufferBuilder.vertex(matrix, x2, y1, z2).color(oc).next();
			
			bufferBuilder.vertex(matrix, x2, y1, z2).color(oc).next();
			bufferBuilder.vertex(matrix, x2, y1, z1).color(oc).next();
			
			bufferBuilder.vertex(matrix, x2, y1, z1).color(oc).next();
			bufferBuilder.vertex(matrix, x1, y1, z1).color(oc).next();
			// bottom
			bufferBuilder.vertex(matrix, x1, y2, z1).color(oc).next();
			bufferBuilder.vertex(matrix, x1, y2, z2).color(oc).next();
			
			bufferBuilder.vertex(matrix, x1, y2, z2).color(oc).next();
			bufferBuilder.vertex(matrix, x2, y2, z2).color(oc).next();
			
			bufferBuilder.vertex(matrix, x2, y2, z2).color(oc).next();
			bufferBuilder.vertex(matrix, x2, y2, z1).color(oc).next();
			
			bufferBuilder.vertex(matrix, x2, y2, z1).color(oc).next();
			bufferBuilder.vertex(matrix, x1, y2, z1).color(oc).next();
			// edges
			bufferBuilder.vertex(matrix, x1, y1, z1).color(oc).next();
			bufferBuilder.vertex(matrix, x1, y2, z1).color(oc).next();
			
			bufferBuilder.vertex(matrix, x1, y1, z2).color(oc).next();
			bufferBuilder.vertex(matrix, x1, y2, z2).color(oc).next();
			
			bufferBuilder.vertex(matrix, x2, y1, z2).color(oc).next();
			bufferBuilder.vertex(matrix, x2, y2, z2).color(oc).next();
			
			bufferBuilder.vertex(matrix, x2, y1, z1).color(oc).next();
			bufferBuilder.vertex(matrix, x2, y2, z1).color(oc).next();
			
			BufferRenderer.drawWithShader(bufferBuilder.end());
		}
	}
	
	private void applyCameraOffset(MatrixStack matrixStack) {
		Vec3d camPos = getCameraPos();
		matrixStack.multiply(new Quaternion(mc.getBlockEntityRenderDispatcher().camera.getPitch(), (mc.getBlockEntityRenderDispatcher().camera.getYaw()) % 360 + 180, 0, true));
		matrixStack.translate(-camPos.x, -camPos.y, -camPos.z);
	}
	
	private Vec3d getCameraPos() {
		return mc.getBlockEntityRenderDispatcher().camera.getPos();
	}
}
