package net.grilledham.hamhacks.modules.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventRender3D;
import net.grilledham.hamhacks.event.events.EventTick;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.util.Color;
import net.grilledham.hamhacks.util.setting.BoolSetting;
import net.grilledham.hamhacks.util.setting.ColorSetting;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.*;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Stream;

public class Tracers extends Module {
	
	private final ArrayList<LivingEntity> entities = new ArrayList<>();
	
	@BoolSetting(name = "hamhacks.module.tracers.tracePlayers", defaultValue = true)
	public boolean tracePlayers = true;
	
	@ColorSetting(name = "hamhacks.module.tracers.playerColorClose", dependsOn = "tracePlayers")
	public Color playerClose = new Color(0xFFFF0000);
	
	@ColorSetting(name = "hamhacks.module.tracers.playerColorFar", dependsOn = "tracePlayers")
	public Color playerFar = new Color(0xFF00FF00);
	
	@BoolSetting(name = "hamhacks.module.tracers.traceHostile")
	public boolean traceHostile = false;
	
	@ColorSetting(name = "hamhacks.module.tracers.hostileColorClose", dependsOn = "traceHostile")
	public Color hostileClose = new Color(0xFFFF0000);
	
	@ColorSetting(name = "hamhacks.module.tracers.hostileColorFar", dependsOn = "traceHostile")
	public Color hostileFar = new Color(0xFF00FF00);
	
	@BoolSetting(name = "hamhacks.module.tracers.tracePassive")
	public boolean tracePassive = false;
	
	@ColorSetting(name = "hamhacks.module.tracers.passiveColorClose", dependsOn = "tracePassive")
	public Color passiveClose = new Color(0xFFFF0000);
	
	@ColorSetting(name = "hamhacks.module.tracers.passiveColorFar", dependsOn = "tracePassive")
	public Color passiveFar = new Color(0xFF00FF00);
	
	public Tracers() {
		super(Text.translatable("hamhacks.module.tracers"), Category.RENDER, new Keybind(0));
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
				.filter(entity -> entity != player)
				.filter(entity -> Math.abs(entity.getY() - mc.player.getY()) <= 1e6)
				.filter(entity -> (entity instanceof PlayerEntity && tracePlayers) || (entity instanceof HostileEntity && traceHostile) || (entity instanceof PassiveEntity && tracePassive));
		
		entities.addAll(stream.toList());
	}
	
	private void renderTracers(MatrixStack matrixStack, double partialTicks) {
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		RenderSystem.setShaderColor(1, 1, 1, 1);
		
		Matrix4f matrix = matrixStack.peek().getPositionMatrix();
		
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
		
		Vec3d start = getClientLookVec().add(getCameraPos());
		
		for(LivingEntity e : entities) {
			Vec3d interpolationOffset = new Vec3d(e.getX(), e.getY(), e.getZ()).subtract(e.prevX, e.prevY, e.prevZ).multiply(1 - partialTicks);
			Vec3d end = e.getBoundingBox().getCenter().subtract(interpolationOffset);
			
			float f = mc.player.distanceTo(e) / 20F;
			int cClose;
			int cFar;
			if(e instanceof PlayerEntity) {
				cClose = playerClose.getRGB();
				cFar = playerFar.getRGB();
			} else if(e instanceof HostileEntity) {
				cClose = hostileClose.getRGB();
				cFar = hostileFar.getRGB();
			} else if(e instanceof PassiveEntity) {
				cClose = passiveClose.getRGB();
				cFar = passiveFar.getRGB();
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
		}
		
		BufferRenderer.drawWithShader(bufferBuilder.end());
	}
	
	private void applyCameraOffset(MatrixStack matrixStack) {
		Vec3d camPos = getCameraPos();
		matrixStack.multiply(new Quaternion(mc.getBlockEntityRenderDispatcher().camera.getPitch(), (mc.getBlockEntityRenderDispatcher().camera.getYaw()) % 360 + 180, 0, true));
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
}
