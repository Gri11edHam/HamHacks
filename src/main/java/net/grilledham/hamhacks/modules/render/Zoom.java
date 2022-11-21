package net.grilledham.hamhacks.modules.render;

import net.grilledham.hamhacks.animation.Animation;
import net.grilledham.hamhacks.animation.AnimationBuilder;
import net.grilledham.hamhacks.animation.AnimationType;
import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventScroll;
import net.grilledham.hamhacks.modules.Category;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.setting.BoolSetting;
import net.grilledham.hamhacks.setting.KeySetting;
import net.grilledham.hamhacks.setting.NumberSetting;
import net.grilledham.hamhacks.setting.SettingCategory;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

public class Zoom extends Module {
	
	private final SettingCategory OPTIONS_CATEGORY = new SettingCategory("hamhacks.module.zoom.category.options");
	
	private final KeySetting zoomKey = new KeySetting("hamhacks.module.zoom.zoomKey", new Keybind(GLFW.GLFW_KEY_V), () -> true);
	
	private final NumberSetting initialZoom = new NumberSetting("hamhacks.module.zoom.initialZoom", 4, () -> true, 1, 50);
	
	private final SettingCategory SCROLL_CATEGORY = new SettingCategory("hamhacks.module.zoom.category.scroll");
	
	private final BoolSetting scrollToZoom = new BoolSetting("hamhacks.module.zoom.scrollToZoom", false, () -> true);
	
	private final BoolSetting linearScrollSpeed = new BoolSetting("hamhacks.module.zoom.linearScrollSpeed", false, scrollToZoom::get);
	
	private final NumberSetting scrollSpeed = new NumberSetting("hamhacks.module.zoom.scrollSpeed", 1, scrollToZoom::get, 1, 10);
	
	private final SettingCategory CLAMP_CATEGORY = new SettingCategory("hamhacks.module.zoom.category.clamp");
	
	private final BoolSetting clampZoom = new BoolSetting("hamhacks.module.zoom.clampZoom", true, scrollToZoom::get);
	
	private final NumberSetting minZoom = new NumberSetting("hamhacks.module.zoom.minZoom", 1, () -> clampZoom.get() || scrollToZoom.get(), 1, 4);
	
	private final NumberSetting maxZoom = new NumberSetting("hamhacks.module.zoom.maxZoom", 50, () -> clampZoom.get() || scrollToZoom.get(), 4, 500);
	
	private final SettingCategory ADVANCED_CATEGORY = new SettingCategory("hamhacks.module.zoom.category.advanced");
	
	private final BoolSetting adjustSensitivity = new BoolSetting("hamhacks.module.zoom.adjustSensitivity", false, () -> true);
	
	private final BoolSetting smoothZoom = new BoolSetting("hamhacks.module.zoom.smoothZoom", false, () -> true);
	
	private final NumberSetting animationSpeed = new NumberSetting("hamhacks.module.zoom.animationSpeed", 0.5, smoothZoom::get, 0, 1);
	
	private final BoolSetting smoothCamera = new BoolSetting("hamhacks.module.zoom.smoothCamera", false, () -> true);
	
	public final BoolSetting renderHand = new BoolSetting("hamhacks.module.zoom.renderHand", true, () -> true);
	
	private boolean zooming = false;
	
	private double zoom = 1;
	private double zoomAmount = 1;
	
	private double prevSensitivity = 0;
	private boolean wasSmoothCameraEnabled = false;
	
	private final Animation animation = AnimationBuilder.create(AnimationType.IN_OUT_QUAD, animationSpeed.get(), true).build();
	
	public Zoom() {
		super(Text.translatable("hamhacks.module.zoom"), Category.RENDER, new Keybind(0));
		setEnabled(true);
		animation.setAbsolute(1);
		settingCategories.add(0, OPTIONS_CATEGORY);
		OPTIONS_CATEGORY.add(zoomKey);
		OPTIONS_CATEGORY.add(initialZoom);
		settingCategories.add(1, SCROLL_CATEGORY);
		SCROLL_CATEGORY.add(scrollToZoom);
		SCROLL_CATEGORY.add(linearScrollSpeed);
		SCROLL_CATEGORY.add(scrollSpeed);
		settingCategories.add(2, CLAMP_CATEGORY);
		CLAMP_CATEGORY.add(clampZoom);
		CLAMP_CATEGORY.add(minZoom);
		CLAMP_CATEGORY.add(maxZoom);
		settingCategories.add(3, ADVANCED_CATEGORY);
		ADVANCED_CATEGORY.add(adjustSensitivity);
		ADVANCED_CATEGORY.add(smoothZoom);
		ADVANCED_CATEGORY.add(animationSpeed);
		ADVANCED_CATEGORY.add(smoothCamera);
		ADVANCED_CATEGORY.add(renderHand);
	}
	
	@Override
	public String getHUDText() {
		return super.getHUDText() + " \u00a77" + String.format("%.2f", smoothZoom.get() ? zoomAmount : zoom);
	}
	
	
	public double modifyFov(double fov) {
		if(isEnabled()) {
			if(zoomKey.get().isPressed() && !zooming) {
				zoom = initialZoom.get();
				prevSensitivity = mc.options.getMouseSensitivity().getValue().floatValue();
				wasSmoothCameraEnabled = mc.options.smoothCameraEnabled;
				zooming = true;
			} else if(!zoomKey.get().isPressed() && zooming) {
				zoom = 1;
				mc.options.getMouseSensitivity().setValue(prevSensitivity);
				mc.options.smoothCameraEnabled = wasSmoothCameraEnabled;
				zooming = false;
			}
			
			if(zooming) {
				if(adjustSensitivity.get()) {
					if(zoom == 0) {
						mc.options.getMouseSensitivity().setValue(prevSensitivity);
					} else {
						double newSens = prevSensitivity / (zoom * zoom);
						if(newSens <= prevSensitivity && newSens >= 0) {
							mc.options.getMouseSensitivity().setValue(newSens);
						}
					}
				}
				if(smoothCamera.get()) {
					mc.options.smoothCameraEnabled = true;
				}
				if(clampZoom.get()) {
					zoom = MathHelper.clamp(zoom, minZoom.get(), maxZoom.get());
				}
			}
			
			if(smoothZoom.get()) {
				animation.set(zoom);
				animation.setDuration(animationSpeed.get());
				animation.update();
				zoomAmount = animation.get();
				if(clampZoom.get()) {
					zoomAmount = MathHelper.clamp(zoomAmount, minZoom.get(), maxZoom.get());
				}
				if(zoomAmount != 0) {
					fov /= zoomAmount;
				}
			} else {
				if(zoom != 0) {
					fov /= zoom;
				}
			}
		}
		return fov;
	}
	
	public double getZoomAmount() {
		return smoothZoom.get() ? zoomAmount : zoom;
	}
	
	@EventListener
	public void onScroll(EventScroll e) {
		if(!scrollToZoom.get() || !zooming) {
			return;
		}
		e.canceled = true;
		if(linearScrollSpeed.get()) {
			float direction = 0;
			if(e.vertical > 0) {
				direction = 1;
			} else if(e.vertical < 0) {
				direction = -1;
			}
			zoom += scrollSpeed.get() * direction;
		} else {
			float direction = 1;
			if(e.vertical > 0) {
				direction = 2;
			} else if(e.vertical < 0) {
				direction = 0.5f;
			}
			zoom *= scrollSpeed.get() * direction;
		}
	}
}
