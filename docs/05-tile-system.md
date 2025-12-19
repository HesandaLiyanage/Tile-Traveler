# 5. Tile System

## Overview

The tile system is how the game world is built. Instead of one large image, the world is made of small, reusable tiles that are placed on a grid.

---

## File: `src/tile/Tile.java`

The simplest class in the project:

```java
package tile;

import java.awt.image.BufferedImage;

public class Tile {
    public BufferedImage image;
    public boolean collision = false;
}
```

Each tile has:
- **image** - The sprite to display
- **collision** - Whether entities can walk through it

---

## Available Tiles

| Index | Tile | Image | Collision |
|-------|------|-------|-----------|
| 0 | Grass | grass.png | ❌ No (walkable) |
| 1 | Wall | wall.png | ✅ Yes (blocked) |
| 2 | Water | water.png | ✅ Yes (blocked) |
| 3 | Earth | earth.png | ❌ No (walkable) |
| 4 | Tree | tree.png | ✅ Yes (blocked) |
| 5 | Sand | sand.png | ❌ No (walkable) |

---

## File: `src/tile/TileManager.java`

The TileManager handles:
1. Loading tile images
2. Loading world maps from text files
3. Rendering visible tiles

### Class Properties

```java
public class TileManager {
    Gamepanel gp;
    public Tile[] tile;          // Array of tile types (max 10)
    public int[][] mapTileNum;   // 2D grid of tile indices
    
    // ...
}
```

---

## Constructor

```java
public TileManager(Gamepanel gp) {
    this.gp = gp;
    tile = new Tile[10];
    mapTileNum = new int[gp.maxWorldCol][gp.maxWorldCol];  // 74x74 grid
    getTileImage();
    loadMap("maps/map04.txt");
}
```

Order of operations:
1. Create array for tile types
2. Create 2D array for map data
3. Load tile images
4. Load map from file

---

## getTileImage() - Loading Tile Sprites

```java
public void getTileImage() {
    try {
        tile[0] = new Tile();
        tile[0].image = ImageIO.read(getClass().getClassLoader()
            .getResourceAsStream("tiles/grass.png"));

        tile[1] = new Tile();
        tile[1].image = ImageIO.read(getClass().getClassLoader()
            .getResourceAsStream("tiles/wall.png"));
        tile[1].collision = true;  // Walls block movement

        tile[2] = new Tile();
        tile[2].image = ImageIO.read(getClass().getClassLoader()
            .getResourceAsStream("tiles/water.png"));
        tile[2].collision = true;  // Water blocks movement

        tile[3] = new Tile();
        tile[3].image = ImageIO.read(getClass().getClassLoader()
            .getResourceAsStream("tiles/earth.png"));

        tile[4] = new Tile();
        tile[4].image = ImageIO.read(getClass().getClassLoader()
            .getResourceAsStream("tiles/tree.png"));
        tile[4].collision = true;  // Trees block movement

        tile[5] = new Tile();
        tile[5].image = ImageIO.read(getClass().getClassLoader()
            .getResourceAsStream("tiles/sand.png"));

    } catch (IOException e) {
        e.printStackTrace();
    }
}
```

Each tile type is loaded and configured for collision.

---

## loadMap() - Reading World Data

```java
public void loadMap(String mapFile) {
    try {
        InputStream is = getClass().getClassLoader().getResourceAsStream(mapFile);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        
        int col = 0;
        int row = 0;
        
        while (col < gp.maxWorldCol && row < gp.maxWorldCol) {
            String line = br.readLine();
            
            while (col < gp.maxWorldCol) {
                String numbers[] = line.split(" ");
                int num = Integer.parseInt(numbers[col]);
                mapTileNum[col][row] = num;
                col++;
            }
            
            if (col == gp.maxWorldCol) {
                col = 0;
                row++;
            }
        }
        br.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
}
```

### Map File Format

Map files are text files with space-separated numbers:

```
0 0 0 1 1 1 0 0 0 0 0 0 0 0 0 0
0 0 0 0 0 1 0 0 0 0 0 0 0 0 0 0
1 1 1 0 0 1 0 0 0 2 2 2 2 2 0 0
...
```

Each number corresponds to a tile type:
- `0` = Grass
- `1` = Wall
- `2` = Water
- etc.

### Parsing Process

```
┌─────────────────────────────────────────────────────────────┐
│                    MAP FILE PARSING                         │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  File: "0 1 2 0 1..."                                       │
│         │                                                   │
│         ▼                                                   │
│  split(" ") → ["0", "1", "2", "0", "1", ...]               │
│                  │                                          │
│                  ▼                                          │
│  parseInt → [0, 1, 2, 0, 1, ...]                            │
│                  │                                          │
│                  ▼                                          │
│  mapTileNum[col][row] = value                               │
│                                                             │
│  Result: 2D array                                           │
│    ┌─────────────────────────┐                              │
│    │ 0 │ 1 │ 2 │ 0 │ 1 │ ... │ ← Row 0                     │
│    │ 0 │ 0 │ 0 │ 1 │ 1 │ ... │ ← Row 1                     │
│    │ 1 │ 1 │ 0 │ 0 │ 0 │ ... │ ← Row 2                     │
│    └─────────────────────────┘                              │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## The Evolution of draw() Method

This is where we can see the evolution of the code. There are **two approaches** in the comments.

### The OLD Way (Commented Out - Hardcoded)

```java
// g2.drawImage(tile[1].image,0,0,gp.tileSize,gp.tileSize,null);
// g2.drawImage(tile[1].image,48,0,gp.tileSize,gp.tileSize,null);
// g2.drawImage(tile[1].image,96,0,gp.tileSize,gp.tileSize,null);
// ... and so on for every single tile
```

**Problems with this approach:**
1. ❌ Extremely tedious (need one line per tile)
2. ❌ Hard to change the map
3. ❌ No support for large worlds
4. ❌ No camera/scrolling support

### The CURRENT Way (Loop + Camera)

The current implementation uses loops and calculates screen position:

```java
public void draw(Graphics g2) {
    int WorldCol = 0;
    int WorldRow = 0;

    while (WorldCol < gp.maxWorldCol && WorldRow < gp.maxWorldRow) {
        int tileNum = mapTileNum[WorldCol][WorldRow];

        // Calculate world position
        int worldX = WorldCol * gp.tileSize;
        int worldY = WorldRow * gp.tileSize;

        // Convert to screen position (camera offset)
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        // Only draw if visible on screen (culling)
        if (worldX + gp.tileSize > gp.player.worldX - gp.player.screenX &&
            worldX - gp.tileSize < gp.player.worldX + gp.player.screenX &&
            worldY + gp.tileSize > gp.player.worldY - gp.player.screenY &&
            worldY - gp.tileSize < gp.player.worldY + gp.player.screenY) {
            
            g2.drawImage(tile[tileNum].image, screenX, screenY, 
                         gp.tileSize, gp.tileSize, null);
        }

        WorldCol++;
        if (WorldCol == gp.maxWorldCol) {
            WorldCol = 0;
            WorldRow++;
        }
    }
}
```

---

## Understanding the Camera Math

The key formula:

```java
int screenX = worldX - gp.player.worldX + gp.player.screenX;
int screenY = worldY - gp.player.worldY + gp.player.screenY;
```

Let's break this down:

```
┌─────────────────────────────────────────────────────────────┐
│                    CAMERA CALCULATION                       │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  Goal: Convert tile's WORLD position to SCREEN position    │
│                                                             │
│  worldX = Where the tile is in the world                    │
│  player.worldX = Where the player is in the world           │
│  player.screenX = Where the player is on screen (center)    │
│                                                             │
│  Formula breakdown:                                         │
│                                                             │
│  1. (worldX - player.worldX)                                │
│     → Tile's position RELATIVE to player                    │
│     → Negative if tile is left of player                    │
│     → Positive if tile is right of player                   │
│                                                             │
│  2. + player.screenX                                        │
│     → Offset to center of screen                            │
│     → Player is always at screen center                     │
│                                                             │
│  Example:                                                   │
│  - Tile at worldX = 500                                     │
│  - Player at worldX = 600                                   │
│  - Screen center screenX = 360                              │
│                                                             │
│  screenX = 500 - 600 + 360 = 260                            │
│  → Tile renders 100 pixels left of center                   │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## Culling (Only Draw Visible Tiles)

```java
if (worldX + gp.tileSize > gp.player.worldX - gp.player.screenX &&
    worldX - gp.tileSize < gp.player.worldX + gp.player.screenX &&
    worldY + gp.tileSize > gp.player.worldY - gp.player.screenY &&
    worldY - gp.tileSize < gp.player.worldY + gp.player.screenY)
```

This checks if a tile is **within view** before drawing it.

### Why Culling Matters

Without culling:
- Draw all 74×74 = 5,476 tiles every frame
- Most are off-screen and invisible
- Wastes CPU/GPU resources

With culling:
- Only draw ~16×12 = 192 tiles (what's visible)
- ~**97% fewer draw calls!**

```
┌─────────────────────────────────────────────────────────────┐
│                        WORLD                                │
│  ┌────────────────────────────────────────────────────┐     │
│  │ ▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒ │     │
│  │ ▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒ │     │
│  │ ▒▒▒▒▒▒▒▒▒┌───────────────┐▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒ │     │
│  │ ▒▒▒▒▒▒▒▒▒│   VISIBLE     │▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒ │     │
│  │ ▒▒▒▒▒▒▒▒▒│   (drawn)     │▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒ │     │
│  │ ▒▒▒▒▒▒▒▒▒└───────────────┘▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒ │     │
│  │ ▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒ │     │
│  │ ▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒ │     │
│  └────────────────────────────────────────────────────┘     │
│                                                             │
│  ▒ = Not drawn (culled)                                     │
│  □ = Drawn (visible on screen)                              │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## The Old Commented Variables

```java
// int x = 0;
// int y = 0;
```

These were used in an earlier version that didn't have camera support. The code was changed to use `worldX`/`screenX` calculations instead.

---

## Map Files Available

The project has 4 map files showing progression:

| Map | Size | Purpose |
|-----|------|---------|
| map01.txt | Small | Initial testing |
| map02.txt | 50×50 | Larger test map |
| map03.txt | Larger | Intermediate |
| map04.txt | 74×74 | Current main map |

The current code uses `map04.txt`.

---

## How It All Fits Together

```
┌─────────────────────────────────────────────────────────────┐
│                    TILE SYSTEM OVERVIEW                     │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  1. TILE TYPES (tile[] array)                               │
│  ┌─────┬─────┬─────┬─────┬─────┬─────┐                      │
│  │  0  │  1  │  2  │  3  │  4  │  5  │                      │
│  │grass│wall │water│earth│tree │sand │                      │
│  └─────┴─────┴─────┴─────┴─────┴─────┘                      │
│                                                             │
│  2. MAP DATA (mapTileNum[][] 2D array)                      │
│  ┌─────────────────────────────────────┐                    │
│  │ 0 0 0 1 1 1 0 0 0 0 0 0 2 2 2 0 │   │ ← Row 0            │
│  │ 0 0 0 0 0 1 0 0 4 4 0 0 2 0 2 0 │   │ ← Row 1            │
│  │ 1 1 1 0 0 1 0 0 4 4 0 0 2 2 2 0 │   │ ← Row 2            │
│  │ ...                               ... │                    │
│  └─────────────────────────────────────┘                    │
│                                                             │
│  3. RENDERING                                               │
│  - Loop through map                                         │
│  - Get tile type from mapTileNum                            │
│  - Calculate screen position using camera math              │
│  - Draw if visible (culling)                                │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## Next: [Object System](./06-object-system.md)
