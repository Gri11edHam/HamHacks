package net.grilledham.hamhacks.util;

import com.google.common.base.Charsets;
import net.grilledham.hamhacks.HamHacksClient;
import net.minecraft.SharedConstants;
import net.minecraft.resource.*;
import net.minecraft.resource.metadata.ResourceMetadataSerializer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.PathUtil;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class HamHacksResourcePack implements ResourcePack {
	
	private static final FileSystem DEFAULT_FS = FileSystems.getDefault();
	
	private final Path basePath;
	
	private final File zipFile;
	private ZipFile file;
	private boolean failedToOpen;
	
	{
		try {
			this.zipFile = new File(HamHacksClient.class.getProtectionDomain().getCodeSource().getLocation().toURI());
			basePath = Path.of(HamHacksClient.class.getProtectionDomain().getCodeSource().getLocation().toURI());
		} catch(URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
	
	private ZipFile getZipFile() {
		if(failedToOpen) {
			return null;
		} else {
			if(file == null) {
				try {
					file = new ZipFile(zipFile);
				} catch(IOException e) {
					HamHacksClient.LOGGER.warn("Failed to open: {} (if this is a development environment, you can ignore this)", zipFile);
					failedToOpen = true;
					return null;
				}
			}
			return file;
		}
	}
	
	private Path getPath(String name) {
		Path path = basePath.resolve(name.replace("/", basePath.getFileSystem().getSeparator())).toAbsolutePath().normalize();
		if(path.startsWith(basePath.toAbsolutePath().normalize()) && exists(path)) {
			return path;
		}
		return null;
	}
	
	private InputSupplier<InputStream> openFile(String name) {
		ZipFile zip = getZipFile();
		if(zip == null) {
			Path path = getPath(name);
			
			if(path != null && Files.isRegularFile(path)) {
				return () -> Files.newInputStream(path);
			}
			
			if(name.equals("pack.png")) {
				return () -> Files.newInputStream(getPath("assets/" + HamHacksClient.MOD_ID + "/icon.png"));
			}
			
		} else {
			ZipEntry entry = zip.getEntry(name);
			if(entry != null) {
				return InputSupplier.create(zip, entry);
			}
			
			if(name.equals("pack.png")) {
				return InputSupplier.create(zip, new ZipEntry("assets/" + HamHacksClient.MOD_ID + "/icon.png"));
			}
			
		}
		
		if(name.equals("pack.mcmeta")) {
			return () -> IOUtils.toInputStream(
					"{\"pack\":{\"pack_format\":"
							+ SharedConstants.getGameVersion().getResourceVersion(ResourceType.CLIENT_RESOURCES)
							+ ",\"description\":\"HamHacks resources\"}}",
					Charsets.UTF_8);
		}
		
		return null;
	}
	
	@Nullable
	@Override
	public InputSupplier<InputStream> openRoot(String... segments) {
		PathUtil.validatePath(segments);
		return openFile(String.join("/", segments));
	}
	
	@Nullable
	@Override
	public InputSupplier<InputStream> open(ResourceType type, Identifier id) {
		return openFile(getFilename(type, id));
	}
	
	@Override
	public void findResources(ResourceType type, String namespace, String prefix, ResultConsumer consumer) {
		if(!namespace.equals(HamHacksClient.MOD_ID)) return;
		
		ZipFile zip = getZipFile();
		if(zip != null) {
			Enumeration<? extends ZipEntry> enumeration = zip.entries();
			String path = type.getDirectory() + "/" + namespace + "/";
			String file = path + prefix + "/";
			
			while(enumeration.hasMoreElements()) {
				ZipEntry entry = enumeration.nextElement();
				if(!entry.isDirectory()) {
					String name = entry.getName();
					if(name.startsWith(file)) {
						String s = name.substring(path.length());
						Identifier id = Identifier.of(namespace, s);
						if(id != null) {
							consumer.accept(id, InputSupplier.create(zip, entry));
						} else {
							HamHacksClient.LOGGER.warn("Invalid path: {}:{}", namespace, s);
						}
					}
				}
			}
		} else {
			String separator = basePath.getFileSystem().getSeparator();
			Path nsPath = basePath.resolve(namespace);
			Path searchPath = nsPath.resolve(prefix.replace("/", separator)).normalize();
			if(!exists(searchPath)) return;
			
			try {
				Files.walkFileTree(searchPath, new SimpleFileVisitor<>() {
					@Override
					public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
						String filename = nsPath.relativize(file).toString().replace(separator, "/");
						Identifier identifier = Identifier.of(namespace, filename);
						
						if(identifier == null) {
							HamHacksClient.LOGGER.error("Invalid path: {}:{}", namespace, filename);
						} else {
							consumer.accept(identifier, InputSupplier.create(file));
						}
						
						return FileVisitResult.CONTINUE;
					}
				});
			} catch(IOException e) {
				HamHacksClient.LOGGER.warn("findResources {} {}", prefix, namespace, e);
			}
		}
	}
	
	@Override
	public Set<String> getNamespaces(ResourceType type) {
		return Collections.singleton(HamHacksClient.MOD_ID);
	}
	
	@Nullable
	@Override
	public <T> T parseMetadata(ResourceMetadataSerializer<T> metaReader) throws IOException {
		try(InputStream is = openFile("pack.mcmeta").get()) {
			return AbstractFileResourcePack.parseMetadata(metaReader, is);
		}
	}
	
	@Override
	public ResourcePackInfo getInfo() {
		return new ResourcePackInfo(HamHacksClient.MOD_ID, Text.literal("HamHacks"), ResourcePackSource.BUILTIN, Optional.empty());
	}
	
	@Override
	public void close() {
		if(file != null) {
			IOUtils.closeQuietly(file);
			file = null;
		}
	}
	
	private static boolean exists(Path path) {
		return path.getFileSystem() == DEFAULT_FS ? path.toFile().exists() : Files.exists(path);
	}
	
	private static String getFilename(ResourceType type, Identifier id) {
		return String.format(Locale.ROOT, "%s/%s/%s", type.getDirectory(), id.getNamespace(), id.getPath());
	}
}
