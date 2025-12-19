# Tile Traveler - Project Documentation

## Overview

**Tile Traveler** is a 2D top-down adventure game built using **Java Swing**. The game features a tile-based world system, player movement with WASD controls, collision detection, and interactable objects (keys, doors, chests).

This documentation provides a comprehensive breakdown of the entire codebase, following the natural flow of game development patterns.

---

## Project Status

🚧 **This is a work in progress.** The game is still being actively developed.

### What's Implemented ✅

| Feature | Status |
|---------|--------|
| Game window and panel setup | ✅ Complete |
| Game loop (Delta/Accumulator method) | ✅ Complete |
| Player movement (WASD) | ✅ Complete |
| Player sprite animation | ✅ Complete |
| Tile-based world rendering | ✅ Complete |
| World map loading from text files | ✅ Complete |
| Camera system (world scrolling) | ✅ Complete |
| Tile collision detection | ✅ Complete |
| Object collision detection | ✅ Complete |
| Interactable objects (Key, Door, Chest) | ✅ Complete |
| Culling (only render visible tiles) | ✅ Complete |

### What's Pending 🔜

| Feature | Status |
|---------|--------|
| Inventory system | ⏳ Not started |
| Key usage for doors | ⏳ Not started |
| NPC/Enemy entities | ⏳ Not started |
| Sound effects & music | ⏳ Not started |
| UI (health bar, inventory display) | ⏳ Not started |
| Game states (menu, pause, game over) | ⏳ Not started |
| Save/Load system | ⏳ Not started |

---

## Project Structure

```
Tile-Traveler/
├── src/
│   ├── main/                    # Core game classes
│   │   ├── Main.java            # Entry point
│   │   ├── Gamepanel.java       # Game panel & loop
│   │   ├── KeyHandler.java      # Input handling
│   │   ├── CollisionChecker.java# Collision detection
│   │   └── AssetSetter.java     # Object placement
│   │
│   ├── entity/                  # Entity classes
│   │   ├── Entity.java          # Base entity class
│   │   └── Player.java          # Player class
│   │
│   ├── tile/                    # Tile system
│   │   ├── Tile.java            # Individual tile
│   │   └── TileManager.java     # Tile loading & rendering
│   │
│   └── object/                  # Game objects
│       ├── SuperObject.java     # Base object class
│       ├── OBJ_Key.java         # Key item
│       ├── OBJ_Door.java        # Door object
│       └── OBJ_Chest.java       # Chest object
│
├── res/                         # Resources
│   ├── maps/                    # World map text files
│   │   ├── map01.txt            # Small test map
│   │   ├── map02.txt            # 50x50 map
│   │   ├── map03.txt            # Larger map
│   │   └── map04.txt            # 74x74 current map
│   │
│   ├── tiles/                   # Tile sprites (16x16)
│   │   ├── grass.png            # Tile 0 - Walkable
│   │   ├── wall.png             # Tile 1 - Collision
│   │   ├── water.png            # Tile 2 - Collision
│   │   ├── earth.png            # Tile 3 - Walkable
│   │   ├── tree.png             # Tile 4 - Collision
│   │   └── sand.png             # Tile 5 - Walkable
│   │
│   ├── player/                  # Player sprites
│   │   ├── boy_up_1.png
│   │   ├── boy_up_2.png
│   │   ├── boy_down_1.png
│   │   ├── boy_down_2.png
│   │   ├── boy_left_1.png
│   │   ├── boy_left_2.png
│   │   ├── boy_right_1.png
│   │   └── boy_right_2.png
│   │
│   └── objects/                 # Object sprites
│       ├── key.png
│       ├── door.png
│       ├── chest.png
│       └── ... (more available)
│
└── docs/                        # This documentation
```

---

## Documentation Index

1. [**Overview**](./00-overview.md) ← You are here
2. [**Main Entry Point**](./01-main-entry.md) - How the game starts
3. [**Game Panel & Loop**](./02-game-panel.md) - Core rendering and update loop
4. [**Input Handling**](./03-input-handling.md) - WASD keyboard controls
5. [**Entity System**](./04-entity-system.md) - Base entity and player
6. [**Tile System**](./05-tile-system.md) - Tiles, maps, and evolution
7. [**Object System**](./06-object-system.md) - Interactable game objects
8. [**Collision Detection**](./07-collision.md) - Tile and object collisions
9. [**Suggestions & Improvements**](./08-suggestions.md) - Future improvements

---

## How to Read This Documentation

Follow the numbered files in order for the best understanding. Each file builds upon the previous one, mimicking the natural progression of game development:

1. **Start with Main** - The entry point that creates everything
2. **Understand the Game Panel** - Where all rendering and updating happens
3. **Learn how input works** - How player presses are captured
4. **Entity system** - The foundation for all moving things
5. **Tile system** - How the world is built and rendered
6. **Objects** - Items the player can interact with
7. **Collision** - How things bump into each other
8. **Improvements** - Ideas for the future

---

## Technical Details

| Setting | Value |
|---------|-------|
| Original Tile Size | 16x16 pixels |
| Scaled Tile Size | 48x48 pixels (3x scale) |
| Screen Columns | 16 tiles |
| Screen Rows | 12 tiles |
| Screen Resolution | 768 x 576 pixels |
| World Size | 74 x 74 tiles |
| Target FPS | 60 frames per second |
| Player Speed | 4 pixels per frame |
