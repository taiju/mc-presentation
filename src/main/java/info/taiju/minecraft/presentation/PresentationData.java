package info.taiju.minecraft.presentation;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * Class to hold presentation information
 */
public class PresentationData {
    public static final Codec<PresentationData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("id").forGetter(PresentationData::getId),
            SlideData.CODEC.listOf().fieldOf("slides").forGetter(PresentationData::getSlides),
            Codec.INT.fieldOf("currentSlideIndex").forGetter(PresentationData::getCurrentSlideIndex),
            Codec.DOUBLE.fieldOf("x").forGetter(PresentationData::getX),
            Codec.DOUBLE.fieldOf("y").forGetter(PresentationData::getY),
            Codec.DOUBLE.fieldOf("z").forGetter(PresentationData::getZ),
            Codec.DOUBLE.fieldOf("width").forGetter(PresentationData::getWidth),
            Codec.DOUBLE.fieldOf("height").forGetter(PresentationData::getHeight),
            Codec.INT.fieldOf("rotation").orElse(0).forGetter(PresentationData::getRotation),
            Codec.BOOL.fieldOf("isPlaced").forGetter(PresentationData::isPlaced))
            .apply(instance, PresentationData::new));

    private final String id;
    private final List<SlideData> slides;
    private int currentSlideIndex;

    private double x;
    private double y;
    private double z;
    private double width;
    private double height;
    private int rotation;
    private boolean isPlaced;

    public PresentationData(String id) {
        this.id = id;
        this.slides = new ArrayList<>();
        this.currentSlideIndex = 0;
        this.isPlaced = false;
        this.rotation = 0;
    }

    public PresentationData(String id, List<SlideData> slides, int currentSlideIndex,
            double x, double y, double z, double width, double height, int rotation, boolean isPlaced) {
        this.id = id;
        this.slides = new ArrayList<>(slides);
        this.currentSlideIndex = currentSlideIndex;
        this.x = x;
        this.y = y;
        this.z = z;
        this.width = width;
        this.height = height;
        this.rotation = rotation;
        this.isPlaced = isPlaced;
    }

    public String getId() {
        return id;
    }

    public void addSlide(SlideData slide) {
        slides.add(slide);
        slides.sort((a, b) -> Integer.compare(a.getSlideNumber(), b.getSlideNumber()));
    }

    public List<SlideData> getSlides() {
        return slides;
    }

    public SlideData getCurrentSlide() {
        if (slides.isEmpty()) {
            return null;
        }
        return slides.get(currentSlideIndex);
    }

    public int getCurrentSlideIndex() {
        return currentSlideIndex;
    }

    public boolean nextSlide() {
        if (currentSlideIndex < slides.size() - 1) {
            currentSlideIndex++;
            return true;
        }
        return false;
    }

    public boolean prevSlide() {
        if (currentSlideIndex > 0) {
            currentSlideIndex--;
            return true;
        }
        return false;
    }

    public boolean firstSlide() {
        if (!slides.isEmpty()) {
            currentSlideIndex = 0;
            return true;
        }
        return false;
    }

    public boolean lastSlide() {
        if (!slides.isEmpty()) {
            currentSlideIndex = slides.size() - 1;
            return true;
        }
        return false;
    }

    /**
     * Jump to a specific slide by slide number
     * * @param slideNumber Slide number to jump to
     * 
     * @return Whether the jump was successful
     */
    public boolean jumpToSlide(int slideNumber) {
        for (int i = 0; i < slides.size(); i++) {
            if (slides.get(i).getSlideNumber() == slideNumber) {
                currentSlideIndex = i;
                return true;
            }
        }
        return false;
    }

    /**
     * Remove a slide by slide number
     * * @param slideNumber Slide number to remove
     * 
     * @return Whether the removal was successful
     */
    public boolean removeSlide(int slideNumber) {
        int indexToRemove = -1;
        for (int i = 0; i < slides.size(); i++) {
            if (slides.get(i).getSlideNumber() == slideNumber) {
                indexToRemove = i;
                break;
            }
        }

        if (indexToRemove == -1) {
            return false;
        }

        slides.remove(indexToRemove);

        if (indexToRemove < currentSlideIndex) {
            currentSlideIndex--;
        } else if (indexToRemove == currentSlideIndex && currentSlideIndex >= slides.size()) {
            currentSlideIndex = Math.max(0, slides.size() - 1);
        }

        return true;
    }

    public void setPosition(double x, double y, double z, double width, double height, int rotation) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.width = width;
        this.height = height;
        this.rotation = rotation;
        this.isPlaced = true;
    }

    public void unsetPosition() {
        this.isPlaced = false;
    }

    public boolean isPlaced() {
        return isPlaced;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public int getRotation() {
        return rotation;
    }
}
