package info.taiju.minecraft.presentation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.loading.FMLPaths;

/**
 * Implementation of presentation commands.
 */
public class PresentationCommand {

    /**
     * Register commands.
     */
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("presentation")
                .requires(source -> source.hasPermission(2)) // Requires OP permission
                .then(Commands.literal("add")
                        .then(Commands.argument("id", StringArgumentType.word())
                                .suggests((context, builder) -> {
                                    return builder.buildFuture();
                                })
                                .executes(PresentationCommand::createPresentation)))
                .then(Commands.literal("import")
                        .then(Commands.argument("id", StringArgumentType.word())
                                .suggests((context, builder) -> {
                                    return builder.buildFuture();
                                })
                                .executes(PresentationCommand::importPresentation)))
                .then(Commands.literal("list")
                        .executes(PresentationCommand::listPresentations))
                .then(Commands.literal("slide")
                        .then(Commands.literal("add")
                                .then(Commands.argument("id", StringArgumentType.word())
                                        .suggests((context, builder) -> {
                                            PresentationManager.getInstance().getAllPresentations().keySet()
                                                    .forEach(builder::suggest);
                                            return builder.buildFuture();
                                        })
                                        .then(Commands.argument("slide-number", IntegerArgumentType.integer(1))
                                                .then(Commands.argument("filename", StringArgumentType.string())
                                                        .executes(PresentationCommand::addSlide)))
                                        .then(Commands.argument("filename", StringArgumentType.string())
                                                .executes(PresentationCommand::appendSlide))))
                        .then(Commands.literal("remove")
                                .then(Commands.argument("id", StringArgumentType.word())
                                        .suggests((context, builder) -> {
                                            PresentationManager.getInstance().getAllPresentations().keySet()
                                                    .forEach(builder::suggest);
                                            return builder.buildFuture();
                                        })
                                        .then(Commands.argument("slide-number", IntegerArgumentType.integer(1))
                                                .executes(PresentationCommand::removeSlide))))
                        .then(Commands.literal("list")
                                .then(Commands.argument("id", StringArgumentType.word())
                                        .suggests((context, builder) -> {
                                            PresentationManager.getInstance().getAllPresentations().keySet()
                                                    .forEach(builder::suggest);
                                            return builder.buildFuture();
                                        })
                                        .executes(PresentationCommand::listSlides)))
                        .then(Commands.literal("next")
                                .then(Commands.argument("id", StringArgumentType.word())
                                        .suggests((context, builder) -> {
                                            PresentationManager.getInstance().getAllPresentations().keySet()
                                                    .forEach(builder::suggest);
                                            return builder.buildFuture();
                                        })
                                        .executes(PresentationCommand::nextSlide)))
                        .then(Commands.literal("prev")
                                .then(Commands.argument("id", StringArgumentType.word())
                                        .suggests((context, builder) -> {
                                            PresentationManager.getInstance().getAllPresentations().keySet()
                                                    .forEach(builder::suggest);
                                            return builder.buildFuture();
                                        })
                                        .executes(PresentationCommand::prevSlide)))
                        .then(Commands.literal("first")
                                .then(Commands.argument("id", StringArgumentType.word())
                                        .suggests((context, builder) -> {
                                            PresentationManager.getInstance().getAllPresentations().keySet()
                                                    .forEach(builder::suggest);
                                            return builder.buildFuture();
                                        })
                                        .executes(PresentationCommand::firstSlide)))
                        .then(Commands.literal("last")
                                .then(Commands.argument("id", StringArgumentType.word())
                                        .suggests((context, builder) -> {
                                            PresentationManager.getInstance().getAllPresentations().keySet()
                                                    .forEach(builder::suggest);
                                            return builder.buildFuture();
                                        })
                                        .executes(PresentationCommand::lastSlide)))
                        .then(Commands.literal("jump")
                                .then(Commands.argument("id", StringArgumentType.word())
                                        .suggests((context, builder) -> {
                                            PresentationManager.getInstance().getAllPresentations().keySet()
                                                    .forEach(builder::suggest);
                                            return builder.buildFuture();
                                        })
                                        .then(Commands.argument("slide-number", IntegerArgumentType.integer(1))
                                                .executes(PresentationCommand::jumpSlide)))))
                .then(Commands.literal("set")
                        .then(Commands.argument("id", StringArgumentType.word())
                                .suggests((context, builder) -> {
                                    PresentationManager.getInstance().getAllPresentations().keySet()
                                            .forEach(builder::suggest);
                                    return builder.buildFuture();
                                })
                                .then(Commands.argument("pos", Vec3Argument.vec3())
                                        .then(Commands.argument("width", DoubleArgumentType.doubleArg(0.1))
                                                .then(Commands.argument("height", DoubleArgumentType.doubleArg(0.1))
                                                        .executes(PresentationCommand::setPosition))))))
                .then(Commands.literal("remove")
                        .then(Commands.argument("id", StringArgumentType.word())
                                .suggests((context, builder) -> {
                                    PresentationManager.getInstance().getAllPresentations().keySet()
                                            .forEach(builder::suggest);
                                    return builder.buildFuture();
                                })
                                .executes(PresentationCommand::removePresentation)))
                .then(Commands.literal("controller")
                        .then(Commands.argument("id", StringArgumentType.word())
                                .suggests((context, builder) -> {
                                    PresentationManager.getInstance().getAllPresentations().keySet()
                                            .forEach(builder::suggest);
                                    return builder.buildFuture();
                                })
                                .executes(PresentationCommand::giveControllerItems)))
                .then(Commands.literal("unset")
                        .then(Commands.argument("id", StringArgumentType.word())
                                .suggests((context, builder) -> {
                                    PresentationManager.getInstance().getAllPresentations().keySet()
                                            .forEach(builder::suggest);
                                    return builder.buildFuture();
                                })
                                .executes(PresentationCommand::unsetPresentation))));
    }

    private static int createPresentation(CommandContext<CommandSourceStack> context) {
        String id = StringArgumentType.getString(context, "id");
        PresentationManager manager = PresentationManager.getInstance();

        if (manager.createPresentation(id)) {
            context.getSource().sendSuccess(() -> Component.translatable("presentation.command.created", id), true);
            return 1;
        } else {
            context.getSource().sendFailure(Component.translatable("presentation.command.already_exists", id));
            return 0;
        }
    }

    private static int importPresentation(CommandContext<CommandSourceStack> context) {
        String id = StringArgumentType.getString(context, "id");
        PresentationManager manager = PresentationManager.getInstance();

        if (manager.getPresentation(id) != null) {
            context.getSource().sendFailure(Component.translatable("presentation.command.already_exists", id));
            return 0;
        }

        Path baseDir = FMLPaths.GAMEDIR.get().resolve("mods").resolve(Presentation.MODID).resolve(id);

        if (!Files.exists(baseDir) || !Files.isDirectory(baseDir)) {
            context.getSource()
                    .sendFailure(Component.translatable("presentation.command.directory_not_found", baseDir));
            return 0;
        }

        try (Stream<Path> stream = Files.list(baseDir)) {
            List<String> imageFiles = stream
                    .filter(path -> !Files.isDirectory(path))
                    .map(path -> path.getFileName().toString())
                    .filter(name -> name.toLowerCase().endsWith(".png"))
                    .sorted()
                    .toList();

            if (imageFiles.isEmpty()) {
                context.getSource()
                        .sendFailure(Component.translatable("presentation.command.no_images_found", baseDir));
                return 0;
            }

            if (manager.createPresentation(id)) {
                for (String filename : imageFiles) {
                    manager.addSlide(id, filename);
                }
                int count = imageFiles.size();
                context.getSource()
                        .sendSuccess(
                                () -> Component.translatable("presentation.command.imported", id, count),
                                true);
                return count;
            } else {
                context.getSource().sendFailure(Component.translatable("presentation.command.create_failed", id));
                return 0;
            }

        } catch (IOException e) {
            context.getSource().sendFailure(Component.translatable("presentation.command.read_error", e.getMessage()));
            return 0;
        }
    }

    private static int removePresentation(CommandContext<CommandSourceStack> context) {
        String id = StringArgumentType.getString(context, "id");
        PresentationManager manager = PresentationManager.getInstance();

        if (manager.deletePresentation(id)) {
            context.getSource().sendSuccess(() -> Component.translatable("presentation.command.removed", id), true);
            return 1;
        } else {
            context.getSource().sendFailure(Component.translatable("presentation.command.not_found", id));
            return 0;
        }
    }

    private static int unsetPresentation(CommandContext<CommandSourceStack> context) {
        String id = StringArgumentType.getString(context, "id");
        PresentationManager manager = PresentationManager.getInstance();

        if (manager.unsetPosition(id)) {
            context.getSource().sendSuccess(() -> Component.translatable("presentation.command.unset", id), true);
            return 1;
        } else {
            context.getSource().sendFailure(Component.translatable("presentation.command.not_found", id));
            return 0;
        }
    }

    private static int addSlide(CommandContext<CommandSourceStack> context) {
        String id = StringArgumentType.getString(context, "id");
        int slideNumber = IntegerArgumentType.getInteger(context, "slide-number");
        String filename = StringArgumentType.getString(context, "filename");
        PresentationManager manager = PresentationManager.getInstance();

        if (!filename.toLowerCase().endsWith(".png")) {
            context.getSource().sendFailure(Component.translatable("presentation.command.only_png"));
            return 0;
        }

        if (manager.addSlide(id, slideNumber, filename)) {
            context.getSource()
                    .sendSuccess(
                            () -> Component.translatable("presentation.command.slide_added", slideNumber, filename),
                            true);
            return 1;
        } else {
            context.getSource().sendFailure(Component.translatable("presentation.command.not_found", id));
            return 0;
        }
    }

    private static int appendSlide(CommandContext<CommandSourceStack> context) {
        String id = StringArgumentType.getString(context, "id");
        String filename = StringArgumentType.getString(context, "filename");
        PresentationManager manager = PresentationManager.getInstance();

        if (!filename.toLowerCase().endsWith(".png")) {
            context.getSource().sendFailure(Component.translatable("presentation.command.only_png"));
            return 0;
        }

        if (manager.addSlide(id, filename)) {
            context.getSource()
                    .sendSuccess(() -> Component.translatable("presentation.command.slide_appended", filename), true);
            return 1;
        } else {
            context.getSource().sendFailure(Component.translatable("presentation.command.not_found", id));
            return 0;
        }
    }

    private static int removeSlide(CommandContext<CommandSourceStack> context) {
        String id = StringArgumentType.getString(context, "id");
        int slideNumber = IntegerArgumentType.getInteger(context, "slide-number");
        PresentationManager manager = PresentationManager.getInstance();

        if (manager.removeSlide(id, slideNumber)) {
            context.getSource()
                    .sendSuccess(() -> Component.translatable("presentation.command.slide_removed", slideNumber), true);
            return 1;
        } else {
            PresentationData presentation = manager.getPresentation(id);
            if (presentation == null) {
                context.getSource().sendFailure(Component.translatable("presentation.command.not_found", id));
            } else {
                context.getSource()
                        .sendFailure(Component.translatable("presentation.command.slide_not_found", slideNumber));
            }
            return 0;
        }
    }

    private static int setPosition(CommandContext<CommandSourceStack> context) {
        String id = StringArgumentType.getString(context, "id");
        Vec3 pos = Vec3Argument.getVec3(context, "pos");
        double width = DoubleArgumentType.getDouble(context, "width");
        double height = DoubleArgumentType.getDouble(context, "height");
        PresentationManager manager = PresentationManager.getInstance();

        try {
            ServerPlayer player = context.getSource().getPlayerOrException();
            Direction facing = player.getDirection();

            Vec3 targetBlockVec = pos.add(
                    facing.getStepX() * 0.1,
                    0,
                    facing.getStepZ() * 0.1);

            int bx = Mth.floor(targetBlockVec.x);
            int by = Mth.floor(pos.y);
            int bz = Mth.floor(targetBlockVec.z);
            double x = bx;
            double y = by;
            double z = bz;

            switch (facing) {
                case NORTH:
                    x = bx;
                    z = bz + 1.0;
                    break;
                case SOUTH:
                    x = bx + 1.0;
                    z = bz;
                    break;
                case WEST:
                    x = bx + 1.0;
                    z = bz + 1.0;
                    break;
                case EAST:
                    x = bx;
                    z = bz;
                    break;
                default:
                    break;
            }

            float yRot = facing.toYRot();
            int rotation = (int) ((yRot + 180.0F) % 360.0F);

            if (manager.setPosition(id, x, y, z, width, height, rotation)) {
                double finalX = x;
                double finalY = y;
                double finalZ = z;
                context.getSource().sendSuccess(() -> Component.translatable(
                        "presentation.command.set_position",
                        id, finalX, finalY, finalZ, width, height, rotation), true);
                return 1;
            } else {
                context.getSource().sendFailure(Component.translatable("presentation.command.not_found", id));
                return 0;
            }
        } catch (CommandSyntaxException e) {
            context.getSource().sendFailure(Component.translatable("presentation.command.player_only"));
            return 0;
        }
    }

    private static int nextSlide(CommandContext<CommandSourceStack> context) {
        String id = StringArgumentType.getString(context, "id");
        PresentationManager manager = PresentationManager.getInstance();
        PresentationData presentation = manager.getPresentation(id);

        if (presentation == null) {
            context.getSource().sendFailure(Component.translatable("presentation.command.not_found", id));
            return 0;
        }

        if (manager.nextSlide(id)) {
            int currentIndex = presentation.getCurrentSlideIndex();
            int totalSlides = presentation.getSlides().size();
            Presentation.LOGGER.info("Presentation '{}': Slide {} / {}", id, currentIndex + 1, totalSlides);
        } else {
            Presentation.LOGGER.info("Presentation '{}': Already at the last slide", id);
        }
        return 1;
    }

    private static int prevSlide(CommandContext<CommandSourceStack> context) {
        String id = StringArgumentType.getString(context, "id");
        PresentationManager manager = PresentationManager.getInstance();
        PresentationData presentation = manager.getPresentation(id);

        if (presentation == null) {
            context.getSource().sendFailure(Component.translatable("presentation.command.not_found", id));
            return 0;
        }

        if (manager.prevSlide(id)) {
            int currentIndex = presentation.getCurrentSlideIndex();
            int totalSlides = presentation.getSlides().size();
            Presentation.LOGGER.info("Presentation '{}': Slide {} / {}", id, currentIndex + 1, totalSlides);
        } else {
            Presentation.LOGGER.info("Presentation '{}': Already at the first slide", id);
        }
        return 1;
    }

    private static int firstSlide(CommandContext<CommandSourceStack> context) {
        String id = StringArgumentType.getString(context, "id");
        PresentationManager manager = PresentationManager.getInstance();
        PresentationData presentation = manager.getPresentation(id);

        if (presentation == null) {
            context.getSource().sendFailure(Component.translatable("presentation.command.not_found", id));
            return 0;
        }

        if (manager.firstSlide(id)) {
            int currentIndex = presentation.getCurrentSlideIndex();
            int totalSlides = presentation.getSlides().size();
            Presentation.LOGGER.info("Presentation '{}': Jumped to first slide ({} / {})", id, currentIndex + 1,
                    totalSlides);
        } else {
            Presentation.LOGGER.info("Presentation '{}': No slides available", id);
        }
        return 1;
    }

    private static int lastSlide(CommandContext<CommandSourceStack> context) {
        String id = StringArgumentType.getString(context, "id");
        PresentationManager manager = PresentationManager.getInstance();
        PresentationData presentation = manager.getPresentation(id);

        if (presentation == null) {
            context.getSource().sendFailure(Component.translatable("presentation.command.not_found", id));
            return 0;
        }

        if (manager.lastSlide(id)) {
            int currentIndex = presentation.getCurrentSlideIndex();
            int totalSlides = presentation.getSlides().size();
            Presentation.LOGGER.info("Presentation '{}': Jumped to last slide ({} / {})", id, currentIndex + 1,
                    totalSlides);
        } else {
            Presentation.LOGGER.info("Presentation '{}': No slides available", id);
        }
        return 1;
    }

    private static int jumpSlide(CommandContext<CommandSourceStack> context) {
        String id = StringArgumentType.getString(context, "id");
        int slideNumber = IntegerArgumentType.getInteger(context, "slide-number");
        PresentationManager manager = PresentationManager.getInstance();
        PresentationData presentation = manager.getPresentation(id);

        if (presentation == null) {
            context.getSource().sendFailure(Component.translatable("presentation.command.not_found", id));
            return 0;
        }

        if (manager.jumpToSlide(id, slideNumber)) {
            int currentIndex = presentation.getCurrentSlideIndex();
            int totalSlides = presentation.getSlides().size();
            context.getSource()
                    .sendSuccess(
                            () -> Component.translatable("presentation.command.jumped", currentIndex + 1, totalSlides),
                            true);
            return 1;
        } else {
            context.getSource()
                    .sendFailure(Component.translatable("presentation.command.slide_not_found", slideNumber));
            return 0;
        }
    }

    private static int listPresentations(CommandContext<CommandSourceStack> context) {
        PresentationManager manager = PresentationManager.getInstance();
        Map<String, PresentationData> presentations = manager.getAllPresentations();

        if (presentations.isEmpty()) {
            context.getSource().sendSuccess(() -> Component.translatable("presentation.command.no_presentations"),
                    false);
            return 0;
        }

        String presentationList = String.join(", ", presentations.keySet());
        context.getSource().sendSuccess(
                () -> Component.translatable("presentation.command.list_presentations", presentationList), false);
        return 1;
    }

    private static int listSlides(CommandContext<CommandSourceStack> context) {
        String id = StringArgumentType.getString(context, "id");
        PresentationManager manager = PresentationManager.getInstance();
        PresentationData presentation = manager.getPresentation(id);

        if (presentation == null) {
            context.getSource().sendFailure(Component.translatable("presentation.command.not_found", id));
            return 0;
        }

        List<SlideData> slides = presentation.getSlides();
        if (slides.isEmpty()) {
            context.getSource().sendSuccess(() -> Component.translatable("presentation.command.no_slides", id), false);
            return 0;
        }

        String slideList = slides.stream()
                .map(slide -> slide.getSlideNumber() + " (" + slide.getFilename() + ")")
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
        context.getSource().sendSuccess(() -> Component.translatable("presentation.command.list_slides", id, slideList),
                false);
        return 1;
    }

    private static int giveControllerItems(CommandContext<CommandSourceStack> context) {
        String id = StringArgumentType.getString(context, "id");
        PresentationManager manager = PresentationManager.getInstance();

        if (manager.getPresentation(id) == null) {
            context.getSource().sendFailure(Component.translatable("presentation.command.not_found", id));
            return 0;
        }

        try {
            ServerPlayer player = context.getSource().getPlayerOrException();

            // Create NBT tag with presentation ID
            net.minecraft.nbt.CompoundTag tag = new net.minecraft.nbt.CompoundTag();
            tag.putString("PresentationId", id);

            // Create first slide item
            net.minecraft.world.item.ItemStack firstItem = new net.minecraft.world.item.ItemStack(
                    PresentationItems.FIRST_SLIDE_ITEM.get());
            firstItem.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA,
                    net.minecraft.world.item.component.CustomData.of(tag));

            // Create previous slide item
            net.minecraft.world.item.ItemStack prevItem = new net.minecraft.world.item.ItemStack(
                    PresentationItems.PREV_SLIDE_ITEM.get());
            prevItem.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA,
                    net.minecraft.world.item.component.CustomData.of(tag));

            // Create next slide item
            net.minecraft.world.item.ItemStack nextItem = new net.minecraft.world.item.ItemStack(
                    PresentationItems.NEXT_SLIDE_ITEM.get());
            nextItem.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA,
                    net.minecraft.world.item.component.CustomData.of(tag));

            // Create last slide item
            net.minecraft.world.item.ItemStack lastItem = new net.minecraft.world.item.ItemStack(
                    PresentationItems.LAST_SLIDE_ITEM.get());
            lastItem.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA,
                    net.minecraft.world.item.component.CustomData.of(tag));

            // Give items to player in order: first, prev, next, last
            player.addItem(firstItem);
            player.addItem(prevItem);
            player.addItem(nextItem);
            player.addItem(lastItem);

            context.getSource().sendSuccess(() -> Component.literal("Controller items given for " + id), true);
            return 1;
        } catch (CommandSyntaxException e) {
            context.getSource().sendFailure(Component.translatable("presentation.command.player_only"));
            return 0;
        }
    }
}
