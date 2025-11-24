package info.taiju.minecraft.presentation;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(Presentation.MODID)
public class Presentation {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "presentation";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod
    // is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and
    // pass them in automatically.
    public Presentation(IEventBus modEventBus, ModContainer modContainer) {
        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class
        // (Presentation) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in
        // this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        // Register items
        PresentationItems.ITEMS.register(modEventBus);
    }

    // Register commands
    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        PresentationCommand.register(event.getDispatcher());
        LOGGER.info("Presentation commands registered");
    }

    // Load presentation data when server starts
    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        // Load presentation data from the Overworld
        // The Overworld is used because it's never fully unloaded
        PresentationManager.getInstance().loadFromWorld(event.getServer().overworld());
        LOGGER.info("Presentation data loaded from world");
    }
}
