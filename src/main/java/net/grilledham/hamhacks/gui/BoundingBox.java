package net.grilledham.hamhacks.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;

public class BoundingBox {
	
	protected float centerX;
	protected float centerY;
	
	protected float anchorX;
	protected float anchorY;
	
	protected float x;
	protected float y;
	
	protected float lastX;
	protected float lastY;
	
	protected float width;
	protected float height;
	
	protected float scale;
	
	protected ScreenQuad screenQuad;
	
	protected int scaleFactor;
	protected int scaledWidth;
	protected int scaledHeight;
	private double scaledWidthD;
	private double scaledHeightD;
	
	public BoundingBox(float x, float y, float width, float height, ScreenQuad screenQuad) {
		this.x = x;
		this.y = y;
		lastX = x;
		lastY = y;
		this.width = width;
		this.height = height;
		this.screenQuad = screenQuad;
		setScaleFactor(2);
		clampPos();
		updateCenterWithScreenPos();
		updateAnchor();
		updateLastPos();
	}
	
	public float getAnchorX() {
		return anchorX;
	}
	
	public float getAnchorY() {
		return anchorY;
	}
	
	public float getWidth() {
		return width;
	}
	
	public float getHeight() {
		return height;
	}
	
	public void setScaleFactor(int scaleFactor) {
		this.scaleFactor = 1;
		
		this.scaledWidth = MinecraftClient.getInstance().getWindow().getScaledWidth();
		this.scaledHeight = MinecraftClient.getInstance().getWindow().getScaledHeight();
		
		boolean unicode = MinecraftClient.getInstance().forcesUnicodeFont();
		int guiScale = scaleFactor;
		if (guiScale == 0) {
			guiScale = 1000;
		}
		
		while(this.scaleFactor < guiScale && this.scaledWidth / (this.scaleFactor + 1) >= 320 && this.scaledHeight / (this.scaleFactor + 1) >= 240) {
			++this.scaleFactor;
		}
		
		if (unicode && this.scaleFactor % 2 != 0 && this.scaleFactor != 1) {
			--this.scaleFactor;
		}
		
		this.scaledWidthD = (double)this.scaledWidth / (double)this.scaleFactor;
		this.scaledHeightD = (double)this.scaledHeight / (double)this.scaleFactor;
		this.scaledWidth = MathHelper.ceil(this.scaledWidthD);
		this.scaledHeight = MathHelper.ceil(this.scaledHeightD);
		
		updateCenter();
		updateScreenPos();
		clampPos();
	}
	
	public void move(float x, float y) {
		centerX += x;
		centerY += y;
		updateScreenPos();
		screenQuad = getNewScreenQuad(centerX, centerY);
		clampPos();
	}
	
	public void setAnchor(float x, float y, ScreenQuad quad) {
		anchorX = x;
		anchorY = y;
		screenQuad = quad;
		updateCenter();
		updateScreenPos();
		clampPos();
	}
	
	public void resize(float width, float height) {
		this.width = width;
		this.height = height;
		updateCenter();
		updateScreenPos();
		clampPos();
	}
	
	public void setScale(float scale) {
		this.scale = scale;
		updateCenter();
		updateScreenPos();
		clampPos();
	}
	
	public void resizeScreen() {
		updateCenter();
		updateAnchor();
		updateScreenPos();
		clampPos();
	}
	
	private ScreenQuad getNewScreenQuad(float x, float y) {
		int horizontal;
		int vertical;
		if(x < scaledWidth / 3f) {
			horizontal = -1;
		} else if(x < (scaledWidth / 3f) * 2f) {
			horizontal = 0;
		} else {
			horizontal = 1;
		}
		if(y < scaledHeight / 3f) {
			vertical = -1;
		} else if(y < (scaledHeight / 3f) * 2f) {
			vertical = 0;
		} else {
			vertical = 1;
		}
		
		if(horizontal == -1) {
			if(vertical == -1) {
				return ScreenQuad.TOP_LEFT;
			} else if(vertical == 0) {
				return ScreenQuad.LEFT;
			} else {
				return ScreenQuad.BOTTOM_LEFT;
			}
		} else if(horizontal == 0) {
			if(vertical == -1) {
				return ScreenQuad.TOP;
			} else if(vertical == 0) {
				return ScreenQuad.CENTER;
			} else {
				return ScreenQuad.BOTTOM;
			}
		} else {
			if(vertical == -1) {
				return ScreenQuad.TOP_RIGHT;
			} else if(vertical == 0) {
				return ScreenQuad.RIGHT;
			} else {
				return ScreenQuad.BOTTOM_RIGHT;
			}
		}
	}
	
	private void clampPos() {
		if(x < 0) {
			x = 0;
		}
		if(x + (width * scale) > scaledWidth) {
			x = scaledWidth - (width * scale);
		}
		if(y < 0) {
			y = 0;
		}
		if(y + (height * scale) > scaledHeight) {
			y = scaledHeight - (height * scale);
		}
		updateCenterWithScreenPos();
		updateAnchor();
	}
	
	private void updateCenterWithScreenPos() {
		centerX = x + (width * scale) / 2f;
		centerY = y + (height * scale) / 2f;
	}
	
	private void updateAnchor() {
		switch(screenQuad) {
			case CENTER:
				anchorX = centerX - scaledWidth / 2f;
				anchorY = centerY - scaledHeight / 2f;
				break;
			case LEFT:
				anchorX = centerX - (width * scale) / 2f;
				anchorY = centerY - scaledHeight / 2f;
				break;
			case RIGHT:
				anchorX = centerX + (width * scale) / 2f - scaledWidth;
				anchorY = centerY - scaledHeight / 2f;
				break;
			case TOP:
				anchorX = centerX - scaledWidth / 2f;
				anchorY = centerY - (height * scale) / 2f;
				break;
			case BOTTOM:
				anchorX = centerX - scaledWidth / 2f;
				anchorY = centerY + (height * scale) / 2f - scaledHeight;
				break;
			case TOP_LEFT:
				anchorX = centerX - (width * scale) / 2f;
				anchorY = centerY - (height * scale) / 2f;
				break;
			case TOP_RIGHT:
				anchorX = centerX + (width * scale) / 2f - scaledWidth;
				anchorY = centerY - (height * scale) / 2f;
				break;
			case BOTTOM_LEFT:
				anchorX = centerX - (width * scale) / 2f;
				anchorY = centerY + (height * scale) / 2f - scaledHeight;
				break;
			case BOTTOM_RIGHT:
				anchorX = centerX + (width * scale) / 2f - scaledWidth;
				anchorY = centerY + (height * scale) / 2f - scaledHeight;
				break;
		}
	}
	
	private void updateCenter() {
		switch(screenQuad) {
			case CENTER:
				centerX = anchorX + scaledWidth / 2f;
				centerY = anchorY + scaledHeight / 2f;
				break;
			case LEFT:
				centerX = anchorX + (width * scale) / 2f;
				centerY = anchorY + scaledHeight / 2f;
				break;
			case RIGHT:
				centerX = anchorX - (width * scale) / 2f + scaledWidth;
				centerY = anchorY + scaledHeight / 2f;
				break;
			case TOP:
				centerX = anchorX + scaledWidth / 2f;
				centerY = anchorY + (height * scale) / 2f;
				break;
			case BOTTOM:
				centerX = anchorX + scaledWidth / 2f;
				centerY = anchorY - (height * scale) / 2f + scaledHeight;
				break;
			case TOP_LEFT:
				centerX = anchorX + (width * scale) / 2f;
				centerY = anchorY + (height * scale) / 2f;
				break;
			case TOP_RIGHT:
				centerX = anchorX - (width * scale) / 2f + scaledWidth;
				centerY = anchorY + (height * scale) / 2f;
				break;
			case BOTTOM_LEFT:
				centerX = anchorX + (width * scale) / 2f;
				centerY = anchorY - (height * scale) / 2f + scaledHeight;
				break;
			case BOTTOM_RIGHT:
				centerX = anchorX - (width * scale) / 2f + scaledWidth;
				centerY = anchorY - (height * scale) / 2f + scaledHeight;
				break;
		}
	}
	
	private void updateScreenPos() {
		updateLastPos();
		x = centerX - (width * scale) / 2f;
		y = centerY - (height * scale) / 2f;
	}
	
	public boolean collidesWith(float x, float y) {
		float thisX = this.x;
		float thisY = this.y;
		return thisX < x && thisX + (width * scale) > x && thisY < y && thisY + (height * scale) > y;
	}
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
	
	public float getScale() {
		return scale;
	}
	
	public int getScaleFactor() {
		return scaleFactor;
	}
	
	public float getLastX() {
		return lastX;
	}
	
	public float getLastY() {
		return lastY;
	}
	
	public void updateLastPos() {
		lastX = x;
		lastY = y;
	}
	
	public ScreenQuad getScreenQuad() {
		return screenQuad;
	}
	
	public enum ScreenQuad {
		TOP_LEFT,
		TOP,
		TOP_RIGHT,
		LEFT,
		CENTER,
		RIGHT,
		BOTTOM_LEFT,
		BOTTOM,
		BOTTOM_RIGHT
	}
}
