package info.taiju.minecraft.presentation;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registry for presentation items.
 */
public class PresentationItems {
        public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Presentation.MODID);

        // Next slide controller item
        public static final DeferredItem<Item> NEXT_SLIDE_ITEM = ITEMS.registerItem(
                        "next_slide_switch",
                        NextSlideSwitch::new,
                        new Item.Properties().stacksTo(1));

        // Previous slide controller item
        public static final DeferredItem<Item> PREV_SLIDE_ITEM = ITEMS.registerItem(
                        "prev_slide_switch",
                        PrevSlideSwitch::new,
                        new Item.Properties().stacksTo(1));

        // Last slide controller item
        public static final DeferredItem<Item> LAST_SLIDE_ITEM = ITEMS.registerItem(
                        "last_slide_switch",
                        LastSlideSwitch::new,
                        new Item.Properties().stacksTo(1));

        // First slide controller item
        public static final DeferredItem<Item> FIRST_SLIDE_ITEM = ITEMS.registerItem(
                        "first_slide_switch",
                        FirstSlideSwitch::new,
                        new Item.Properties().stacksTo(1));
}
