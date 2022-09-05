package net.grilledham.hamhacks.modules.render;

import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventScroll;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.util.setting.BoolSetting;
import net.grilledham.hamhacks.util.setting.KeySetting;
import net.grilledham.hamhacks.util.setting.NumberSetting;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

public class Zoom extends Module {
	
	@KeySetting(name = "hamhacks.module.zoom.zoomKey")
	public Keybind zoomKey = new Keybind(GLFW.GLFW_KEY_V);
	
	@NumberSetting(
			name = "hamhacks.module.zoom.initialZoom",
			defaultValue = 4,
			min = 1,
			max = 50
	)
	public float initialZoom = 4;
	
	@BoolSetting(name = "hamhacks.module.zoom.scrollToZoom")
	public boolean scrollToZoom = false;
	
	@BoolSetting(name = "hamhacks.module.zoom.linearScrollSpeed", dependsOn = "scrollToZoom")
	public boolean linearScrollSpeed = false;
	
	@NumberSetting(
			name = "hamhacks.module.zoom.scrollSpeed",
			defaultValue = 1,
			min = 1,
			max = 10,
			dependsOn = "scrollToZoom"
	)
	public float scrollSpeed = 1;
	
	@BoolSetting(name = "hamhacks.module.zoom.clampZoom", defaultValue = true, dependsOn = "scrollToZoom")
	public boolean clampZoom = true;
	
	@NumberSetting(
			name = "hamhacks.module.zoom.minZoom",
			min = 1,
			max = 4,
			dependsOn = {"clampZoom", "scrollToZoom"}
	)
	public float minZoom = 0;
	
	@NumberSetting(
			name = "hamhacks.module.zoom.maxZoom",
			defaultValue = 50,
			min = 4,
			max = 500,
			dependsOn = {"clampZoom", "scrollToZoom"}
	)
	public float maxZoom = 50;
	
	@BoolSetting(name = "hamhacks.module.zoom.adjustSensitivity")
	public boolean adjustSensitivity = false;
	
	@BoolSetting(name = "hamhacks.module.zoom.smoothZoom")
	public boolean smoothZoom = false;
	
	@NumberSetting(
			name = "hamhacks.module.zoom.animationSpeed",
			defaultValue = 0.5f,
			min = 0.001f,
			max = 1,
			dependsOn = "smoothZoom"
	)
	public float animationSpeed = 0.5f;
	
	@BoolSetting(name = "hamhacks.module.zoom.smoothCamera")
	public boolean smoothCamera = false;
	
	@BoolSetting(name = "hamhacks.module.zoom.renderHand", defaultValue = true)
	public boolean renderHand = true;
	
	private boolean zooming = false;
	
	private double prevZoomAmount = 1;
	private double zoom = 1;
	private double prevZoom = 1;
	private double zoomProgress = 0;
	private double zoomAmount = 1;
	
	private double prevSensitivity = 0;
	private boolean wasSmoothCameraEnabled = false;
	
	private long lastTime = System.currentTimeMillis();
	
	private static Zoom INSTANCE;
	
	public Zoom() {
		super(Text.translatable("hamhacks.module.zoom"), Category.RENDER, new Keybind(0));
		setEnabled(true);
		INSTANCE = this;
	}
	
	public static Zoom getInstance() {
		return INSTANCE;
	}
	
	
	public double modifyFov(double fov) {
		if(isEnabled()) {
			if(zoomKey.isPressed() && !zooming) {
				zoom = initialZoom;
				prevSensitivity = mc.options.getMouseSensitivity().getValue().floatValue();
				wasSmoothCameraEnabled = mc.options.smoothCameraEnabled;
				zooming = true;
			} else if(!zoomKey.isPressed() && zooming) {
				zoom = 1;
				mc.options.getMouseSensitivity().setValue(prevSensitivity);
				mc.options.smoothCameraEnabled = wasSmoothCameraEnabled;
				zooming = false;
			}
			
			if(zooming) {
				if(adjustSensitivity) {
					if(zoom == 0) {
						mc.options.getMouseSensitivity().setValue(prevSensitivity);
					} else {
						double newSens = prevSensitivity / (zoom * zoom);
						if(newSens <= prevSensitivity && newSens >= 0) {
							mc.options.getMouseSensitivity().setValue(newSens);
						}
					}
				}
				if(smoothCamera) {
					mc.options.smoothCameraEnabled = true;
				}
				if(clampZoom) {
					zoom = MathHelper.clamp(zoom, minZoom, maxZoom);
				}
			}
			
			if(smoothZoom) {
				double progressMultiplier;
				
				if(prevZoom != zoom) {
					prevZoomAmount = zoomAmount;
					zoomProgress = 0;
				}
				if(zoomProgress < 1) {
					zoomProgress += (System.currentTimeMillis() - lastTime) / (animationSpeed * 1000);
				} else {
					zoomProgress = 1;
				}
				
				if(zooming) {
					progressMultiplier = getProgressMultiplier(zoomProgress);
				} else {
					progressMultiplier = 1 - getProgressMultiplier(1 - zoomProgress);
				}
				
				zoomAmount = prevZoomAmount + (zoom - prevZoomAmount) * progressMultiplier;
				if(zoomAmount != 0) {
					fov /= zoomAmount;
				}
			} else {
				if(zoom != 0) {
					fov /= zoom;
				}
			}
			prevZoom = zoom;
		}
		lastTime = System.currentTimeMillis();
		return fov;
	}
	
	public double getZoomAmount() {
		return smoothZoom ? zoomAmount : zoom;
	}
	
	@EventListener
	public void onScroll(EventScroll e) {
		if(!scrollToZoom || !zooming) {
			return;
		}
		e.canceled = true;
		if(linearScrollSpeed) {
			float direction = 0;
			if(e.vertical > 0) {
				direction = 1;
			} else if(e.vertical < 0) {
				direction = -1;
			}
			zoom += scrollSpeed * direction;
		} else {
			float direction = 1;
			if(e.vertical > 0) {
				direction = 2;
			} else if(e.vertical < 0) {
				direction = 0.5f;
			}
			zoom *= scrollSpeed * direction;
		}
	}
	
	private double getProgressMultiplier(double t) {
		if(t < 0.5) {
			return 2 * Math.pow(t, 2);
		} else {
			return 1 - Math.pow(-2 * t + 2, 2) / 2;
		}
	}
}
