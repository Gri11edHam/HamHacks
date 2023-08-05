package net.grilledham.hamhacks.modules.misc;

import net.grilledham.hamhacks.HamHacksClient;
import net.grilledham.hamhacks.mixininterface.IWindow;
import net.grilledham.hamhacks.modules.Category;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.setting.SelectionSetting;
import net.grilledham.hamhacks.setting.StringSetting;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Icons;
import net.minecraft.resource.InputSupplier;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.ArrayUtils;
import oshi.util.tuples.Pair;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class TitleBar extends Module {
	
	public final StringSetting title = new StringSetting("hamhacks.module.titleBar.title", "HamHacks v{ver}+mc{mcVer}[[ - {enabledM}/{totalM}] - {server}]", () -> true, "Minecraft " + SharedConstants.getGameVersion().getName()) {
		@Override
		public void onChange() {
			titleProvider = new TitleProvider(get());
			mc.updateWindowTitle();
		}
	};
	public final SelectionSetting icon = new SelectionSetting("hamhacks.module.titleBar.icon", 2, () -> true, "hamhacks.module.titleBar.icon.default", "hamhacks.module.titleBar.icon.old", "hamhacks.module.titleBar.icon.hamhacks", "hamhacks.module.titleBar.icon.custom") {
		@Override
		public void onChange() {
			updateIcon();
			setIcon();
		}
	};
	public final StringSetting iconPath = new StringSetting("hamhacks.module.titleBar.iconPath", "", () -> icon.get() == 3, "Path to custom icons") {
		@Override
		public void onChange() {
			updateIcon();
			setIcon();
		}
	};
	
	public TitleProvider titleProvider;
	public IconProvider iconProvider;
	
	public TitleBar() {
		super(Text.translatable("hamhacks.module.titleBar"), Category.MISC, new Keybind());
		setEnabled(true);
		GENERAL_CATEGORY.add(title);
		GENERAL_CATEGORY.add(icon);
		GENERAL_CATEGORY.add(iconPath);
		titleProvider = new TitleProvider(title.get());
	}
	
	private void updateIcon() {
		if(icon.get() == 0) return;
		boolean isCustom = icon.get() == 3;
		
		String[] strings;
		if(!isCustom) {
			switch(icon.get()) {
				case 1 -> strings = new String[]{"icons", "old"};
				case 2 -> strings = new String[]{"icons", "hamhacks"};
				default -> throw new IllegalStateException("Unexpected value: " + icon.get());
			}
		} else {
			strings = new String[]{Files.exists(Path.of(iconPath.get())) ? iconPath.get() : null};
		}
		iconProvider = new IconProvider(isCustom, strings);
	}
	
	private void setIcon() {
		try {
			if(icon.get() == 0) {
				mc.getWindow().setIcon(mc.getDefaultResourcePack(), SharedConstants.getGameVersion().isStable() ? Icons.RELEASE : Icons.SNAPSHOT);
			} else {
				try {
					((IWindow)(Object)mc.getWindow()).hamhacks$setIcon(iconProvider);
				} catch(Exception e) {
					mc.getWindow().setIcon(mc.getDefaultResourcePack(), SharedConstants.getGameVersion().isStable() ? Icons.RELEASE : Icons.SNAPSHOT);
				}
			}
		} catch(IOException e) {
			HamHacksClient.LOGGER.error("Setting window icon", e);
		}
	}
	
	@Override
	public void onEnable() {
		super.onEnable();
		mc.updateWindowTitle();
		if(iconProvider == null && icon.get() != 0) {
			updateIcon();
		}
		setIcon();
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
		mc.updateWindowTitle();
		try {
			mc.getWindow().setIcon(mc.getDefaultResourcePack(), SharedConstants.getGameVersion().isStable() ? Icons.RELEASE : Icons.SNAPSHOT);
		} catch(IOException e) {
			HamHacksClient.LOGGER.error("Setting window icon", e);
		}
	}
	
	public static class IconProvider {
		
		private final boolean absolutePath;
		private final String[] path;
		
		private IconProvider(boolean absolutePath, String... path) {
			this.absolutePath = absolutePath;
			this.path = path;
		}
		
		public InputSupplier<InputStream> getMacIcon() throws IOException {
			return this.getIcon("minecraft.icns");
		}
		
		public List<InputSupplier<InputStream>> getIcons() throws IOException {
			return List.of(this.getIcon("icon_16x16.png"), this.getIcon("icon_32x32.png"), this.getIcon("icon_48x48.png"), this.getIcon("icon_128x128.png"), this.getIcon("icon_256x256.png"));
		}
		
		private InputSupplier<InputStream> getIcon(String fileName) throws IOException {
			if(absolutePath) {
				return InputSupplier.create(Path.of(path[0], fileName));
			} else {
				String[] strings = ArrayUtils.add(this.path, fileName);
				InputSupplier<InputStream> inputSupplier = null;
				for(ResourcePack pack : MinecraftClient.getInstance().getResourceManager().streamResourcePacks().toList()) {
					inputSupplier = pack.open(ResourceType.CLIENT_RESOURCES, Identifier.of(HamHacksClient.MOD_ID, String.join("/", strings)));
					if(inputSupplier != null) {
						break;
					}
				}
				if(inputSupplier == null) {
					throw new FileNotFoundException(String.join("/", strings));
				} else {
					return inputSupplier;
				}
			}
		}
	}
	
	public static class TitleProvider {
		
		private final String title;
		private final boolean isSubProvider;
		private final List<TitleProvider> providers = new ArrayList<>();
		
		private final List<Pair<String, Class<?>>> argNames = List.of(
				new Pair<>("{modded}", Boolean.class),
				new Pair<>("{ver}", String.class),
				new Pair<>("{mcVer}", String.class),
				new Pair<>("{server}", String.class),
				new Pair<>("{enabledM}", Integer.class),
				new Pair<>("{totalM}", Integer.class));
		
		private TitleProvider(String title) {
			this(title, false);
		}
		
		private TitleProvider(String title, boolean isSubProvider) {
			this.isSubProvider = isSubProvider;
			int supplierStart = 0;
			int layers = 0;
			for(int i = 0; i < title.length(); i++) {
				if(title.charAt(i) == '[') {
					if(layers == 0) {
						supplierStart = i + 1;
					}
					layers++;
				} else if(title.charAt(i) == ']') {
					layers--;
					if(layers == 0) {
						providers.add(new TitleProvider(title.substring(supplierStart, i), true));
						title = title.substring(0, supplierStart) + (providers.size() - 1) + title.substring(i);
						i -= i - supplierStart;
					}
				}
			}
			this.title = title;
		}
		
		public String getTitle(Object... args) {
			String finalTitle = title;
			for(int i = 0; i < providers.size(); i++) {
				finalTitle = finalTitle.replace("[" + i + "]", providers.get(i).getTitle(args));
			}
			boolean shouldShow = !isSubProvider;
			for(int i = 0; i < argNames.size(); i++) {
				String argName = argNames.get(i).getA();
				if(argNames.get(i).getB() == Boolean.class) {
					if((boolean)args[i] && finalTitle.contains(argName)) {
						finalTitle = finalTitle.replaceAll(Pattern.quote(argName), "*");
						shouldShow = true;
					} else {
						finalTitle = finalTitle.replaceAll(Pattern.quote(argName), "");
					}
				} else if(argNames.get(i).getB() == String.class) {
					if(!args[i].equals("") && finalTitle.contains(argName)) {
						shouldShow = true;
					}
					finalTitle = finalTitle.replaceAll(Pattern.quote(argName), (String)args[i]);
				} else if(argNames.get(i).getB() == Integer.class) {
					if((int)args[i] != 0 && finalTitle.contains(argName)) {
						shouldShow = true;
					}
					finalTitle = finalTitle.replaceAll(Pattern.quote(argName), String.valueOf((int)args[i]));
				}
			}
			return shouldShow ? finalTitle : "";
		}
		
		@Override
		public String toString() {
			String finalTitle = title;
			for(int i = 0; i < providers.size(); i++) {
				finalTitle = finalTitle.replace("[" + i + "]", providers.get(i).toString());
			}
			return finalTitle;
		}
	}
}
