package info.taiju.minecraft.presentation;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.server.level.ServerLevel;

/**
 * Singleton manager to manage presentation data
 */
public class PresentationManager {
    private static PresentationManager instance;
    private final Map<String, PresentationData> presentations;
    private PresentationSavedData savedData;

    private PresentationManager() {
        this.presentations = new HashMap<>();
        this.savedData = null;
    }

    public static PresentationManager getInstance() {
        if (instance == null) {
            instance = new PresentationManager();
        }
        return instance;
    }

    /**
     * Load presentation data from world
     * 
     * @param level Server level (use Overworld for global data)
     */
    public void loadFromWorld(ServerLevel level) {
        // Get or create saved data from the level's data storage
        this.savedData = level.getDataStorage().computeIfAbsent(PresentationSavedData.TYPE);

        // Load presentations from saved data
        this.presentations.clear();
        this.presentations.putAll(savedData.getPresentations());

        Presentation.LOGGER.info("Loaded {} presentation(s) from world", presentations.size());
    }

    /**
     * Create a new presentation
     * 
     * @return Whether the creation was successful
     */
    public boolean createPresentation(String id) {
        if (presentations.containsKey(id)) {
            return false;
        }
        presentations.put(id, new PresentationData(id));
        if (savedData != null) {
            savedData.setPresentations(presentations);
        }
        return true;
    }

    /**
     * Get a presentation
     * 
     * @return Presentation data, or null if it does not exist
     */
    public PresentationData getPresentation(String id) {
        return presentations.get(id);
    }

    /**
     * Delete a presentation
     * 
     * @return Whether the deletion was successful
     */
    public boolean deletePresentation(String id) {
        boolean removed = presentations.remove(id) != null;
        if (removed && savedData != null) {
            savedData.setPresentations(presentations);
        }
        return removed;
    }

    /**
     * Get all presentations
     * 
     * @return Map of presentations
     */
    public Map<String, PresentationData> getAllPresentations() {
        return presentations;
    }

    /**
     * Add a slide to a presentation
     * 
     * @param id          Presentation ID
     * @param slideNumber Slide number
     * @param filename    File name
     * @return Whether the addition was successful
     */
    public boolean addSlide(String id, int slideNumber, String filename) {
        PresentationData presentation = presentations.get(id);
        if (presentation == null) {
            return false;
        }
        presentation.addSlide(new SlideData(slideNumber, filename));
        if (savedData != null) {
            savedData.setPresentations(presentations);
        }
        return true;
    }

    /**
     * Add a slide to the end of a presentation
     * 
     * @param id       Presentation ID
     * @param filename File name
     * @return Whether the addition was successful
     */
    public boolean addSlide(String id, String filename) {
        PresentationData presentation = presentations.get(id);
        if (presentation == null) {
            return false;
        }
        int nextSlideNumber = presentation.getSlides().stream()
                .mapToInt(SlideData::getSlideNumber)
                .max()
                .orElse(0) + 1;
        presentation.addSlide(new SlideData(nextSlideNumber, filename));
        if (savedData != null) {
            savedData.setPresentations(presentations);
        }
        return true;
    }

    /**
     * Remove a slide from a presentation
     * 
     * @param id          Presentation ID
     * @param slideNumber Slide number to remove
     * @return Whether the removal was successful
     */
    public boolean removeSlide(String id, int slideNumber) {
        PresentationData presentation = presentations.get(id);
        if (presentation == null) {
            return false;
        }
        boolean removed = presentation.removeSlide(slideNumber);
        if (removed && savedData != null) {
            savedData.setPresentations(presentations);
        }
        return removed;
    }

    /**
     * Set the position of a presentation
     * 
     * @param id       Presentation ID
     * @param x        X coordinate
     * @param y        Y coordinate
     * @param z        Z coordinate
     * @param width    Width
     * @param height   Height
     * @param rotation Rotation
     * @return Whether the setting was successful
     */
    public boolean setPosition(String id, double x, double y, double z, double width, double height, int rotation) {
        PresentationData presentation = presentations.get(id);
        if (presentation == null) {
            return false;
        }
        presentation.setPosition(x, y, z, width, height, rotation);
        if (savedData != null) {
            savedData.setPresentations(presentations);
        }
        return true;
    }

    /**
     * Unset the position of a presentation
     * 
     * @param id Presentation ID
     * @return Whether the unsetting was successful
     */
    public boolean unsetPosition(String id) {
        PresentationData presentation = presentations.get(id);
        if (presentation == null) {
            return false;
        }
        presentation.unsetPosition();
        if (savedData != null) {
            savedData.setPresentations(presentations);
        }
        return true;
    }

    /**
     * Move to the next slide
     * 
     * @param id Presentation ID
     * @return Whether the move was successful
     */
    public boolean nextSlide(String id) {
        PresentationData presentation = presentations.get(id);
        if (presentation == null) {
            return false;
        }
        return presentation.nextSlide();
    }

    /**
     * Move to the previous slide
     * 
     * @param id Presentation ID
     * @return Whether the move was successful
     */
    public boolean prevSlide(String id) {
        PresentationData presentation = presentations.get(id);
        if (presentation == null) {
            return false;
        }
        return presentation.prevSlide();
    }

    /**
     * Jump to the first slide
     * 
     * @param id Presentation ID
     * @return Whether the jump was successful
     */
    public boolean firstSlide(String id) {
        PresentationData presentation = presentations.get(id);
        if (presentation == null) {
            return false;
        }
        return presentation.firstSlide();
    }

    /**
     * Jump to the last slide
     * 
     * @param id Presentation ID
     * @return Whether the jump was successful
     */
    public boolean lastSlide(String id) {
        PresentationData presentation = presentations.get(id);
        if (presentation == null) {
            return false;
        }
        return presentation.lastSlide();
    }

    /**
     * Jump to a specific slide
     * 
     * @param id          Presentation ID
     * @param slideNumber Slide number to jump to
     * @return Whether the jump was successful
     */
    public boolean jumpToSlide(String id, int slideNumber) {
        PresentationData presentation = presentations.get(id);
        if (presentation == null) {
            return false;
        }
        return presentation.jumpToSlide(slideNumber);
    }

    /**
     * Clear all data (mainly used when loading a world)
     */
    public void clear() {
        presentations.clear();
    }
}
