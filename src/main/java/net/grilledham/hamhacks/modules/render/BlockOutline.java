package net.grilledham.hamhacks.modules.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventRenderBlockOverlay;
import net.grilledham.hamhacks.modules.Category;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.setting.BoolSetting;
import net.grilledham.hamhacks.setting.ColorSetting;
import net.grilledham.hamhacks.setting.NumberSetting;
import net.grilledham.hamhacks.util.Color;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import org.lwjgl.opengl.GL11;

public class BlockOutline extends Module {
	
	public final BoolSetting disableDepthTest = new BoolSetting("hamhacks.module.blockOutline.disableDepthTest", false, () -> true);
	public final BoolSetting outlineEnabled = new BoolSetting("hamhacks.module.blockOutline.outlineEnabled", true, () -> true);
	public final ColorSetting outlineColor = new ColorSetting("hamhacks.module.blockOutline.outlineColor", new Color(0, 0, 0, 0.4f), outlineEnabled::get);
	public final NumberSetting lineWidth = new NumberSetting("hamhacks.module.blockOutline.lineWidth", 2, outlineEnabled::get, 1, 20, 1, false);
	public final BoolSetting overlayEnabled = new BoolSetting("hamhacks.module.blockOutline.overlayEnabled", false, () -> true);
	public final ColorSetting overlayColor = new ColorSetting("hamhacks.module.blockOutline.overlayColor", new Color(0, 0, 0, 0.4f), overlayEnabled::get);
	
	public BlockOutline() {
		super(Text.translatable("hamhacks.module.blockOutline"), Category.RENDER, new Keybind());
		GENERAL_CATEGORY.add(disableDepthTest);
		GENERAL_CATEGORY.add(outlineEnabled);
		GENERAL_CATEGORY.add(outlineColor);
		GENERAL_CATEGORY.add(lineWidth);
		GENERAL_CATEGORY.add(overlayEnabled);
		GENERAL_CATEGORY.add(overlayColor);
	}
	
	@EventListener
	public void onRenderBlockOverlay(EventRenderBlockOverlay event) {
		event.matrices.push();
		VoxelShape shape = event.state.getOutlineShape(event.entity.getWorld(), event.pos, ShapeContext.of(event.entity));
		double offsetX = event.pos.getX() - event.cameraX;
		double offsetY = event.pos.getY() - event.cameraY;
		double offsetZ = event.pos.getZ() - event.cameraZ;
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		if(disableDepthTest.get()) {
			GL11.glDisable(GL11.GL_DEPTH_TEST);
		}
		BufferBuilder buf = Tessellator.getInstance().getBuffer();
		MatrixStack.Entry entry = event.matrices.peek();
		if(overlayEnabled.get()) {
			for(Box box : shape.getBoundingBoxes()) {
				RenderSystem.setShader(GameRenderer::getPositionColorProgram);
				RenderSystem.setShaderColor(1, 1, 1, 1);
				
				GL11.glDisable(GL11.GL_CULL_FACE);
				
				buf.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
				box = box.expand(0.0005);
				buf.vertex(entry.getPositionMatrix(), (float)(box.minX + offsetX), (float)(box.minY + offsetY), (float)(box.minZ + offsetZ)).color(overlayColor.get().getR() / 255f, overlayColor.get().getG() / 255f, overlayColor.get().getB() / 255f, overlayColor.get().getAlpha()).next();
				buf.vertex(entry.getPositionMatrix(), (float)(box.minX + offsetX), (float)(box.maxY + offsetY), (float)(box.minZ + offsetZ)).color(overlayColor.get().getR() / 255f, overlayColor.get().getG() / 255f, overlayColor.get().getB() / 255f, overlayColor.get().getAlpha()).next();
				buf.vertex(entry.getPositionMatrix(), (float)(box.maxX + offsetX), (float)(box.maxY + offsetY), (float)(box.minZ + offsetZ)).color(overlayColor.get().getR() / 255f, overlayColor.get().getG() / 255f, overlayColor.get().getB() / 255f, overlayColor.get().getAlpha()).next();
				buf.vertex(entry.getPositionMatrix(), (float)(box.maxX + offsetX), (float)(box.minY + offsetY), (float)(box.minZ + offsetZ)).color(overlayColor.get().getR() / 255f, overlayColor.get().getG() / 255f, overlayColor.get().getB() / 255f, overlayColor.get().getAlpha()).next();
				
				buf.vertex(entry.getPositionMatrix(), (float)(box.minX + offsetX), (float)(box.minY + offsetY), (float)(box.maxZ + offsetZ)).color(overlayColor.get().getR() / 255f, overlayColor.get().getG() / 255f, overlayColor.get().getB() / 255f, overlayColor.get().getAlpha()).next();
				buf.vertex(entry.getPositionMatrix(), (float)(box.minX + offsetX), (float)(box.maxY + offsetY), (float)(box.maxZ + offsetZ)).color(overlayColor.get().getR() / 255f, overlayColor.get().getG() / 255f, overlayColor.get().getB() / 255f, overlayColor.get().getAlpha()).next();
				buf.vertex(entry.getPositionMatrix(), (float)(box.maxX + offsetX), (float)(box.maxY + offsetY), (float)(box.maxZ + offsetZ)).color(overlayColor.get().getR() / 255f, overlayColor.get().getG() / 255f, overlayColor.get().getB() / 255f, overlayColor.get().getAlpha()).next();
				buf.vertex(entry.getPositionMatrix(), (float)(box.maxX + offsetX), (float)(box.minY + offsetY), (float)(box.maxZ + offsetZ)).color(overlayColor.get().getR() / 255f, overlayColor.get().getG() / 255f, overlayColor.get().getB() / 255f, overlayColor.get().getAlpha()).next();
				
				buf.vertex(entry.getPositionMatrix(), (float)(box.minX + offsetX), (float)(box.minY + offsetY), (float)(box.minZ + offsetZ)).color(overlayColor.get().getR() / 255f, overlayColor.get().getG() / 255f, overlayColor.get().getB() / 255f, overlayColor.get().getAlpha()).next();
				buf.vertex(entry.getPositionMatrix(), (float)(box.minX + offsetX), (float)(box.maxY + offsetY), (float)(box.minZ + offsetZ)).color(overlayColor.get().getR() / 255f, overlayColor.get().getG() / 255f, overlayColor.get().getB() / 255f, overlayColor.get().getAlpha()).next();
				buf.vertex(entry.getPositionMatrix(), (float)(box.minX + offsetX), (float)(box.maxY + offsetY), (float)(box.maxZ + offsetZ)).color(overlayColor.get().getR() / 255f, overlayColor.get().getG() / 255f, overlayColor.get().getB() / 255f, overlayColor.get().getAlpha()).next();
				buf.vertex(entry.getPositionMatrix(), (float)(box.minX + offsetX), (float)(box.minY + offsetY), (float)(box.maxZ + offsetZ)).color(overlayColor.get().getR() / 255f, overlayColor.get().getG() / 255f, overlayColor.get().getB() / 255f, overlayColor.get().getAlpha()).next();
				
				buf.vertex(entry.getPositionMatrix(), (float)(box.maxX + offsetX), (float)(box.minY + offsetY), (float)(box.minZ + offsetZ)).color(overlayColor.get().getR() / 255f, overlayColor.get().getG() / 255f, overlayColor.get().getB() / 255f, overlayColor.get().getAlpha()).next();
				buf.vertex(entry.getPositionMatrix(), (float)(box.maxX + offsetX), (float)(box.maxY + offsetY), (float)(box.minZ + offsetZ)).color(overlayColor.get().getR() / 255f, overlayColor.get().getG() / 255f, overlayColor.get().getB() / 255f, overlayColor.get().getAlpha()).next();
				buf.vertex(entry.getPositionMatrix(), (float)(box.maxX + offsetX), (float)(box.maxY + offsetY), (float)(box.maxZ + offsetZ)).color(overlayColor.get().getR() / 255f, overlayColor.get().getG() / 255f, overlayColor.get().getB() / 255f, overlayColor.get().getAlpha()).next();
				buf.vertex(entry.getPositionMatrix(), (float)(box.maxX + offsetX), (float)(box.minY + offsetY), (float)(box.maxZ + offsetZ)).color(overlayColor.get().getR() / 255f, overlayColor.get().getG() / 255f, overlayColor.get().getB() / 255f, overlayColor.get().getAlpha()).next();
				
				buf.vertex(entry.getPositionMatrix(), (float)(box.minX + offsetX), (float)(box.minY + offsetY), (float)(box.maxZ + offsetZ)).color(overlayColor.get().getR() / 255f, overlayColor.get().getG() / 255f, overlayColor.get().getB() / 255f, overlayColor.get().getAlpha()).next();
				buf.vertex(entry.getPositionMatrix(), (float)(box.minX + offsetX), (float)(box.minY + offsetY), (float)(box.minZ + offsetZ)).color(overlayColor.get().getR() / 255f, overlayColor.get().getG() / 255f, overlayColor.get().getB() / 255f, overlayColor.get().getAlpha()).next();
				buf.vertex(entry.getPositionMatrix(), (float)(box.maxX + offsetX), (float)(box.minY + offsetY), (float)(box.minZ + offsetZ)).color(overlayColor.get().getR() / 255f, overlayColor.get().getG() / 255f, overlayColor.get().getB() / 255f, overlayColor.get().getAlpha()).next();
				buf.vertex(entry.getPositionMatrix(), (float)(box.maxX + offsetX), (float)(box.minY + offsetY), (float)(box.maxZ + offsetZ)).color(overlayColor.get().getR() / 255f, overlayColor.get().getG() / 255f, overlayColor.get().getB() / 255f, overlayColor.get().getAlpha()).next();
				
				buf.vertex(entry.getPositionMatrix(), (float)(box.minX + offsetX), (float)(box.maxY + offsetY), (float)(box.maxZ + offsetZ)).color(overlayColor.get().getR() / 255f, overlayColor.get().getG() / 255f, overlayColor.get().getB() / 255f, overlayColor.get().getAlpha()).next();
				buf.vertex(entry.getPositionMatrix(), (float)(box.minX + offsetX), (float)(box.maxY + offsetY), (float)(box.minZ + offsetZ)).color(overlayColor.get().getR() / 255f, overlayColor.get().getG() / 255f, overlayColor.get().getB() / 255f, overlayColor.get().getAlpha()).next();
				buf.vertex(entry.getPositionMatrix(), (float)(box.maxX + offsetX), (float)(box.maxY + offsetY), (float)(box.minZ + offsetZ)).color(overlayColor.get().getR() / 255f, overlayColor.get().getG() / 255f, overlayColor.get().getB() / 255f, overlayColor.get().getAlpha()).next();
				buf.vertex(entry.getPositionMatrix(), (float)(box.maxX + offsetX), (float)(box.maxY + offsetY), (float)(box.maxZ + offsetZ)).color(overlayColor.get().getR() / 255f, overlayColor.get().getG() / 255f, overlayColor.get().getB() / 255f, overlayColor.get().getAlpha()).next();
				
				BufferRenderer.drawWithGlobalProgram(buf.end());
				
				GL11.glEnable(GL11.GL_CULL_FACE);
			}
		}
		if(outlineEnabled.get()) {
			RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
			RenderSystem.setShaderColor(1, 1, 1, 1);
			
			RenderSystem.lineWidth(lineWidth.get().floatValue());
			
			GL11.glDisable(GL11.GL_CULL_FACE);
			
			buf.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
			shape.forEachEdge((minX, minY, minZ, maxX, maxY, maxZ) -> {
				float k = (float)(maxX - minX);
				float l = (float)(maxY - minY);
				float m = (float)(maxZ - minZ);
				float n = MathHelper.sqrt(k * k + l * l + m * m);
				k /= n;
				l /= n;
				m /= n;
				buf.vertex(entry.getPositionMatrix(), (float)(minX + offsetX), (float)(minY + offsetY), (float)(minZ + offsetZ)).color(outlineColor.get().getR() / 255f, outlineColor.get().getG() / 255f, outlineColor.get().getB() / 255f, outlineColor.get().getAlpha()).normal(k, l, m).next();
				buf.vertex(entry.getPositionMatrix(), (float)(maxX + offsetX), (float)(maxY + offsetY), (float)(maxZ + offsetZ)).color(outlineColor.get().getR() / 255f, outlineColor.get().getG() / 255f, outlineColor.get().getB() / 255f, outlineColor.get().getAlpha()).normal(k, l, m).next();
			});
			BufferRenderer.drawWithGlobalProgram(buf.end());
			
			GL11.glEnable(GL11.GL_CULL_FACE);
		}
		RenderSystem.setShaderColor(1, 1, 1, 1);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		event.matrices.pop();
		event.canceled = true;
	}
}