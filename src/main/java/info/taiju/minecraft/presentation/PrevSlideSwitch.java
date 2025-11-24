package info.taiju.minecraft.presentation;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

/**
 * Item that goes back to the previous slide in a presentation.
 */
public class PrevSlideSwitch extends Item {
    private static final String PRESENTATION_ID_KEY = "PresentationId";

    public PrevSlideSwitch(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        ItemStack stack = player.getItemInHand(hand);
        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = customData.copyTag();

        if (!tag.contains(PRESENTATION_ID_KEY)) {
            Presentation.LOGGER.warn("Previous slide item used without presentation ID");
            return InteractionResult.FAIL;
        }

        String presentationId = tag.getStringOr(PRESENTATION_ID_KEY, "");
        if (presentationId.isEmpty()) {
            Presentation.LOGGER.warn("Previous slide item has empty presentation ID");
            return InteractionResult.FAIL;
        }

        PresentationManager manager = PresentationManager.getInstance();
        PresentationData presentation = manager.getPresentation(presentationId);

        if (presentation == null) {
            Presentation.LOGGER.warn("Presentation '{}' not found", presentationId);
            return InteractionResult.FAIL;
        }

        if (manager.prevSlide(presentationId)) {
            int currentIndex = presentation.getCurrentSlideIndex();
            int totalSlides = presentation.getSlides().size();
            Presentation.LOGGER.info("Presentation '{}': Slide {} / {}", presentationId, currentIndex + 1, totalSlides);
        } else {
            Presentation.LOGGER.info("Presentation '{}': Already at the first slide", presentationId);
        }

        return InteractionResult.SUCCESS;
    }
}
