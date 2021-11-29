package net.grilledham.hamhacks.modules.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.grilledham.hamhacks.event.Event;
import net.grilledham.hamhacks.event.EventRender3D;
import net.grilledham.hamhacks.event.EventTick;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.modules.Setting;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.*;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Stream;

public class Tracers extends Module {
	
	private final ArrayList<LivingEntity> entities = new ArrayList<>();
	
	private Setting tracePlayers;
	private Setting playerClose;
	private Setting playerFar;
	private Setting traceHostile;
	private Setting hostileClose;
	private Setting hostileFar;
	private Setting tracePassive;
	private Setting passiveClose;
	private Setting passiveFar;
	
	public Tracers() {
		super("Tracers", Category.RENDER, new Keybind(0));
	}
	
	@Override
	public void addSettings() {
		super.addSettings();
		tracePlayers = new Setting("Player Tracer", true) {
			@Override
			protected void valueChanged() {
				super.valueChanged();
				updateSettings();
			}
		};
		playerClose = new Setting("Player Color (Close)", 0x80ff0000);
		playerFar = new Setting("Player Color (Far)", 0x8000ff00);
		traceHostile = new Setting("Hostile Tracer", false) {
			@Override
			protected void valueChanged() {
				super.valueChanged();
				updateSettings();
			}
		};
		hostileClose = new Setting("Hostile Color (Close)", 0x80ff0000);
		hostileFar = new Setting("Hostile Color (Far)", 0x8000ff00);
		tracePassive = new Setting("Passive Tracer", false) {
			@Override
			protected void valueChanged() {
				super.valueChanged();
				updateSettings();
			}
		};
		passiveClose = new Setting("Passive Color (Close)", 0x80ff0000);
		passiveFar = new Setting("Passive Color (Far)", 0x8000ff00);
		
		settings.add(tracePlayers);
		settings.add(traceHostile);
		settings.add(tracePassive);
		updateSettings();
	}
	
	private void updateSettings() {
		settings.remove(playerClose);
		settings.remove(playerFar);
		settings.remove(hostileClose);
		settings.remove(hostileFar);
		settings.remove(passiveClose);
		settings.remove(passiveFar);
		if(tracePlayers.getBool()) {
			settings.add(settings.indexOf(tracePlayers) + 1, playerFar);
			settings.add(settings.indexOf(tracePlayers) + 1, playerClose);
		}
		if(traceHostile.getBool()) {
			settings.add(settings.indexOf(traceHostile) + 1, hostileFar);
			settings.add(settings.indexOf(traceHostile) + 1, hostileClose);
		}
		if(tracePassive.getBool()) {
			settings.add(settings.indexOf(tracePassive) + 1, passiveFar);
			settings.add(settings.indexOf(tracePassive) + 1, passiveClose);
		}
	}
	
	@Override
	public boolean onEvent(Event e) {
		boolean superReturn = super.onEvent(e);
		if(superReturn) {
			if(e instanceof EventRender3D) {
				MatrixStack matrixStack = ((EventRender3D)e).matrices;
				float partialTicks = ((EventRender3D)e).tickDelta;
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				GL11.glEnable(GL11.GL_LINE_SMOOTH);
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				
				matrixStack.push();
				applyRegionalRenderOffset(matrixStack);
				
				BlockPos camPos = getCameraBlockPos();
				int regionX = (camPos.getX() >> 9) * 512;
				int regionZ = (camPos.getZ() >> 9) * 512;
				
				renderTracers(matrixStack, partialTicks, regionX, regionZ);
				
				matrixStack.pop();
				
				// GL resets
				RenderSystem.setShaderColor(1, 1, 1, 1);
				GL11.glEnable(GL11.GL_DEPTH_TEST);
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glDisable(GL11.GL_LINE_SMOOTH);
			} else if(e instanceof EventTick) {
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
						}, new Box(mc.player.getBlockPos().add(-64, -64, -64), mc.player.getBlockPos().add(64, 64, 64)), Objects::nonNull).stream()
						.filter(entity -> !entity.isRemoved() && entity.isAlive())
						.filter(entity -> entity != player)
						.filter(entity -> Math.abs(entity.getY() - mc.player.getY()) <= 1e6)
						.filter(entity -> (entity instanceof PlayerEntity && tracePlayers.getBool()) || (entity instanceof HostileEntity && traceHostile.getBool()) || (entity instanceof PassiveEntity && tracePassive.getBool()));
				
				entities.addAll(stream.toList());
			}
		}
		return superReturn;
	}
	
	private void renderTracers(MatrixStack matrixStack, double partialTicks, int regionX, int regionZ) {
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		RenderSystem.setShaderColor(1, 1, 1, 1);
		
		Matrix4f matrix = matrixStack.peek().getModel();
		
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
		
		Vec3d start = getClientLookVec().add(getCameraPos()).subtract(regionX, 0, regionZ);
		
		for(LivingEntity e : entities) {
			Vec3d interpolationOffset = new Vec3d(e.getX(), e.getY(), e.getZ()).subtract(e.prevX, e.prevY, e.prevZ).multiply(1 - partialTicks);
			Vec3d end = e.getBoundingBox().getCenter().subtract(interpolationOffset).subtract(regionX, 0, regionZ).add(getClientLookVec().multiply(mc.player.distanceTo(e) / 4f));
			
			float f = mc.player.distanceTo(e) / 20F;
			int cClose;
			int cFar;
			if(e instanceof PlayerEntity) {
				cClose = (int)playerClose.getColor();
				cFar = (int)playerFar.getColor();
			} else if(e instanceof HostileEntity) {
				cClose = (int)hostileClose.getColor();
				cFar = (int)hostileFar.getColor();
			} else if(e instanceof PassiveEntity) {
				cClose = (int)passiveClose.getColor();
				cFar = (int)passiveFar.getColor();
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
		
		bufferBuilder.end();
		BufferRenderer.draw(bufferBuilder);
	}
	
	private void applyRegionalRenderOffset(MatrixStack matrixStack) {
		Camera camera = mc.getBlockEntityRenderDispatcher().camera;
		matrixStack.multiply(new Quaternion(new Vec3f(1, 0, 0), MathHelper.wrapDegrees(camera.getPitch()), true));
		matrixStack.multiply(new Quaternion(new Vec3f(0, 1, 0), MathHelper.wrapDegrees(camera.getYaw() + 180), true));
		
		Vec3d camPos = getCameraPos();
		BlockPos blockPos = getCameraBlockPos();
		
		int regionX = (blockPos.getX() >> 9) * 512;
		int regionZ = (blockPos.getZ() >> 9) * 512;
		
		matrixStack.translate(regionX - camPos.x, -camPos.y, regionZ - camPos.z);
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
	
	private BlockPos getCameraBlockPos() {
		return mc.getBlockEntityRenderDispatcher().camera.getBlockPos();
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
