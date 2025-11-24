package info.taiju.minecraft.presentation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;

/**
 * Class to hold slide information
 */
public class SlideData {
    public static final Codec<SlideData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("slideNumber").forGetter(SlideData::getSlideNumber),
            Codec.STRING.fieldOf("filename").forGetter(SlideData::getFilename)).apply(instance, SlideData::new));

    private final int slideNumber;
    private final String filename;
    private ResourceLocation textureLocation;

    public SlideData(int slideNumber, String filename) {
        this.slideNumber = slideNumber;
        this.filename = filename;
        this.textureLocation = null;
    }

    public int getSlideNumber() {
        return slideNumber;
    }

    public String getFilename() {
        return filename;
    }

    public ResourceLocation getTextureLocation() {
        return textureLocation;
    }

    public void setTextureLocation(ResourceLocation textureLocation) {
        this.textureLocation = textureLocation;
    }
}
