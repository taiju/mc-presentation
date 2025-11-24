package info.taiju.minecraft.presentation.client;

import com.mojang.blaze3d.platform.NativeImage;
import info.taiju.minecraft.presentation.Presentation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Image file loading and texture management
 */
public class ImageLoader {
    private static ImageLoader instance;
    private final Map<String, ResourceLocation> textureCache;
    private final Path baseDir;

    private ImageLoader() {
        this.textureCache = new HashMap<>();
        // Get path to mods/{MODID}/ directory
        this.baseDir = FMLPaths.GAMEDIR.get().resolve("mods").resolve(Presentation.MODID);

        // Create base directory if it does not exist
        try {
            if (!Files.exists(baseDir)) {
                Files.createDirectories(baseDir);
                Presentation.LOGGER.info("Created presentation base directory: {}", baseDir);
            }
        } catch (IOException e) {
            Presentation.LOGGER.error("Failed to create presentation base directory", e);
        }
    }

    public static ImageLoader getInstance() {
        if (instance == null) {
            instance = new ImageLoader();
        }
        return instance;
    }

    /**
     * Load image file and register as texture
     * 
     * @param presentationId Presentation ID
     * @param filename       File name
     * @return ResourceLocation of the texture, or null if failed
     */
    public ResourceLocation loadTexture(String presentationId, String filename) {
        if (!filename.toLowerCase().endsWith(".png")) {
            Presentation.LOGGER.error("Unsupported image format: {}", filename);
            return null;
        }

        String cacheKey = presentationId + "/" + filename;

        // Check cache
        if (textureCache.containsKey(cacheKey)) {
            return textureCache.get(cacheKey);
        }

        // Load from directory for each presentation ID
        Path presentationDir = baseDir.resolve(presentationId);
        Path imagePath = presentationDir.resolve(filename);

        // Create directory if it does not exist
        try {
            if (!Files.exists(presentationDir)) {
                Files.createDirectories(presentationDir);
                Presentation.LOGGER.info("Created presentation directory: {}", presentationDir);
            }
        } catch (IOException e) {
            Presentation.LOGGER.error("Failed to create presentation directory: {}", presentationDir, e);
        }

        if (!Files.exists(imagePath)) {
            Presentation.LOGGER.error("Image file not found: {}", imagePath);
            return null;
        }

        try {
            // Load image
            NativeImage image = NativeImage.read(Files.newInputStream(imagePath));

            // Register as dynamic texture
            Minecraft minecraft = Minecraft.getInstance();
            String textureName = Presentation.MODID + "/" + presentationId + "/"
                    + filename.replaceAll("[^a-z0-9/_.-]", "_");
            DynamicTexture texture = new DynamicTexture(() -> textureName, image);
            ResourceLocation location = ResourceLocation.fromNamespaceAndPath(
                    Presentation.MODID,
                    textureName);

            minecraft.getTextureManager().register(location, texture);

            // Save to cache
            textureCache.put(cacheKey, location);

            Presentation.LOGGER.info("Loaded texture: {} from {}/{}", location, presentationId, filename);
            return location;

        } catch (IOException e) {
            Presentation.LOGGER.error("Failed to load image: {}", imagePath, e);
            return null;
        }
    }

    /**
     * Clear texture cache
     */
    public void clearCache() {
        Minecraft minecraft = Minecraft.getInstance();
        for (ResourceLocation location : textureCache.values()) {
            minecraft.getTextureManager().release(location);
        }
        textureCache.clear();
    }

    /**
     * Release specific texture
     * 
     * @param presentationId Presentation ID
     * @param filename       File name
     */
    public void releaseTexture(String presentationId, String filename) {
        String cacheKey = presentationId + "/" + filename;
        ResourceLocation location = textureCache.remove(cacheKey);
        if (location != null) {
            Minecraft.getInstance().getTextureManager().release(location);
        }
    }
}
