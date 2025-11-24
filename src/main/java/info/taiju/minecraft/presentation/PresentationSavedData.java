package info.taiju.minecraft.presentation;

import java.util.HashMap;
import java.util.Map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

/**
 * SavedData implementation for storing presentation data to world
 */
public class PresentationSavedData extends SavedData {
    public static final SavedDataType<PresentationSavedData> TYPE = new SavedDataType<>(
            // The identifier of the saved data
            // Used as the path within the level's `data` folder
            "presentations",
            // The initial constructor
            PresentationSavedData::new,
            // The codec used to serialize the data
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.unboundedMap(Codec.STRING, PresentationData.CODEC)
                            .fieldOf("presentations")
                            .forGetter(data -> data.presentations))
                    .apply(instance, PresentationSavedData::new)));

    private final Map<String, PresentationData> presentations;

    // Default constructor (for new data)
    public PresentationSavedData() {
        this.presentations = new HashMap<>();
    }

    // Data constructor (for deserialization)
    public PresentationSavedData(Map<String, PresentationData> presentations) {
        this.presentations = new HashMap<>(presentations);
    }

    public Map<String, PresentationData> getPresentations() {
        return presentations;
    }

    public void setPresentations(Map<String, PresentationData> presentations) {
        this.presentations.clear();
        this.presentations.putAll(presentations);
        this.setDirty();
    }
}
