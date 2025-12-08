# Presentation Mod

A Minecraft mod that allows you to display image presentations in-game.

![Screenshot](screenhsot/2025-12-07_07.32.25.png)

> [\!WARNING]
> **Current Status: Singleplayer Only**
> This mod is currently optimized for **Singleplayer** worlds. It is intended for local recording, streaming, or screen-sharing scenarios. Multiplayer synchronization is not yet implemented.

## About

**Bring your slides directly into the Minecraft world\!**

Presentation Mod is a unique tool designed for **content creators, streamers, and educators**. It allows you to import image files and display them as projected slides within the game world.

Whether you are explaining a redstone tutorial, creating a lore video, or giving a technical talk (this mod was actually used for a presentation at [a tech conference](https://regional.rubykaigi.org/hokuriku01/)\!), this mod lets you do it without leaving the immersion of Minecraft.

## Installation

1.  Install [Minecraft](https://www.minecraft.net/).
2.  Install [NeoForge](https://neoforged.net/) (Version 21.8.51 or later).
3.  Build the mod from source:
    ```bash
    ./gradlew build
    ```
4.  The built jar file will be in the `build/libs/` folder.
5.  Place the jar file in your `mods` folder.

## Quick Start

The easiest way to create a presentation is to import it from a folder.

### 1\. Prepare Images

1.  Go to your Minecraft instance folder.
2.  Navigate to `mods/presentation/`. (Create the folder if it doesn't exist)
3.  Create a new folder with your presentation ID (e.g., `my_presentation`).
4.  Put your slide images (`.png`) inside this folder.
      * *Tip: Images are sorted alphabetically, so name them `01.png`, `02.png`, etc.*

### 2\. Import Presentation

Run the following command to import the images as a presentation:

```
/presentation import my_presentation
```

This creates a presentation with the ID `my_presentation`.

### 3\. Display Presentation

Stand where you want the presentation to appear and run:

```
/presentation set my_presentation ~ ~ ~ 16 9
```

  * `~ ~ ~`: The position (relative to you).
  * `16 9`: The width and height in blocks.
      * *Tip: Use the aspect ratio of your images (e.g., 16 9 or 4 3) for the size\!*

### 4\. Control Slides

You can control slides using commands or **Controller Items**.

**Using Controller Items:**

1.  Get the items: `/presentation controller my_presentation`
2.  You will receive 4 items:
      * **Next Slide Switch**: Right-click to go to the next slide.
      * **Previous Slide Switch**: Right-click to go to the previous slide.
      * **Last Slide Switch**: Right-click to go to the last slide.
      * **First Slide Switch**: Right-click to go to the first slide.

**Using Commands:**

  * **Next Slide**: `/presentation slide next my_presentation`
  * **Previous Slide**: `/presentation slide prev my_presentation`
  * **Last Slide**: `/presentation slide last my_presentation`
  * **First Slide**: `/presentation slide first my_presentation`

## 💡 Pro Tips for Presenters

### Performance & Immersion

Switch to **Front Third Person view** (press F5 twice) to show your avatar standing next to the screen. This allows you to "act" as a presenter within the Minecraft world, making your video much more engaging than a simple screen share.

**Want to focus on the slides?**
Instead of using commands, try drinking an **Invisibility Potion** right on stage\! The animation of drinking a potion adds a great "live performance" feel to your presentation.

### Stage Control with Command Blocks

Want to create a lecture hall with a "Next Slide" trigger on the podium? You can use Command Blocks\!

1.  Give yourself a command block: `/give @s command_block`
2.  Place it and set the command: `presentation slide next my_presentation`
3.  **Activate it with any Redstone signal** (Buttons, Pressure Plates, Levers, etc.). Now you can advance slides physically\!

## Requirements

  * Minecraft: 1.21.8
  * NeoForge: 21.8.51

## Localization

This mod supports the following languages:

  * English (en\_us)
  * Japanese (ja\_jp)

The language is automatically selected based on your Minecraft language settings.

## Command Reference

### Management

  * `/presentation list`
      * List all available presentations.
  * `/presentation remove <id>`
      * Delete a presentation.
  * `/presentation import <id>`
      * Import slides from `mods/presentation/<id>/`.
  * `/presentation controller <id>`
      * Give yourself items to control the presentation (Next/Prev switch).

### Display

  * `/presentation set <id> <x> <y> <z> <width> <height>`
      * Place the presentation in the world.
  * `/presentation unset <id>`
      * Remove the presentation from the world (the data remains).

### Slide Control

  * `/presentation slide next <id>`
      * Go to the next slide.
  * `/presentation slide prev <id>`
      * Go to the previous slide.
  * `/presentation slide last <id>`
      * Go to the last slide.
  * `/presentation slide first <id>`
      * Go to the first slide.
  * `/presentation slide jump <id> <number>`
      * Jump to a specific slide number.
  * `/presentation slide list <id>`
      * List all slides in a presentation.

### Manual Editing (Advanced)

  * `/presentation add <id>`
      * Create an empty presentation.
  * `/presentation slide add <id> [number] <filename>`
      * Add a single slide.
  * `/presentation slide remove <id> <number>`
      * Remove a single slide.
