# 6. Object System

## Overview

The object system handles interactable items in the game world: keys, doors, chests, and other collectibles.

---

## Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    OBJECT HIERARCHY                         │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│                     SuperObject                             │
│                    (base class)                             │
│                         │                                   │
│          ┌──────────────┼──────────────┐                    │
│          │              │              │                    │
│          ▼              ▼              ▼                    │
│      OBJ_Key       OBJ_Door       OBJ_Chest                 │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## File: `src/object/SuperObject.java`

The base class for all objects:

```java
package object;

import main.Gamepanel;
import java.awt.*;
import java.awt.image.BufferedImage;

public class SuperObject {
    public BufferedImage image;
    public String name;
    public boolean collision = false;
    public int worldX;
    public int worldY;
    public Rectangle solidArea = new Rectangle(0, 0, 48, 48);
    public int solidAreaDefaultX = 0;
    public int solidAreaDefaultY = 0;

    public void draw(Graphics g2, Gamepanel gp) {
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        // Culling - only draw if visible
        if (worldX + gp.tileSize > gp.player.worldX - gp.player.screenX &&
            worldX - gp.tileSize < gp.player.worldX + gp.player.screenX &&
            worldY + gp.tileSize > gp.player.worldY - gp.player.screenY &&
            worldY - gp.tileSize < gp.player.worldY + gp.player.screenY) {
            
            g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
        }
    }
}
```

### Properties Breakdown

| Property | Type | Purpose |
|----------|------|---------|
| `image` | BufferedImage | The sprite to display |
| `name` | String | Identifier (e.g., "Key", "Door") |
| `collision` | boolean | If true, blocks player movement |
| `worldX`, `worldY` | int | Position in world coordinates |
| `solidArea` | Rectangle | Collision box (default: full tile 48x48) |
| `solidAreaDefaultX/Y` | int | For resetting collision box position |

### Draw Method

The draw method uses the same camera math as TileManager:
1. Convert world position to screen position
2. Check if visible (culling)
3. Draw if on screen

---

## File: `src/object/OBJ_Key.java`

```java
package object;

import javax.imageio.ImageIO;
import java.io.IOException;

public class OBJ_Key extends SuperObject {

    public OBJ_Key() {
        name = "Key";
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/objects/key.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

### Key Features:
- **No collision** - Player can walk through it
- **Collectible** - Gets picked up on contact
- Currently just disappears (no inventory system yet)

---

## File: `src/object/OBJ_Door.java`

```java
package object;

import javax.imageio.ImageIO;
import java.io.IOException;

public class OBJ_Door extends SuperObject {

    public OBJ_Door() {
        name = "Door";
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/objects/door.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        collision = true;  // Doors block movement!
    }
}
```

### Door Features:
- **Has collision** - Player cannot walk through
- Currently cannot be opened (requires key logic to be implemented)

---

## File: `src/object/OBJ_Chest.java`

```java
package object;

import javax.imageio.ImageIO;
import java.io.IOException;

public class OBJ_Chest extends SuperObject {

    public OBJ_Chest() {
        name = "Chest";
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/objects/chest.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

### Chest Features:
- **No collision by default** - Can be walked over
- Currently just disappears when touched
- Has `chest_opened.png` sprite available but not used yet

---

## File: `src/main/AssetSetter.java`

This class places objects in the world at startup:

```java
package main;

import object.OBJ_Chest;
import object.OBJ_Door;
import object.OBJ_Key;

public class AssetSetter {
    Gamepanel gp;
    
    public AssetSetter(Gamepanel gp) {
        this.gp = gp;
    }

    public void setObject() {
        // 1. A Chest
        gp.obj[0] = new OBJ_Chest();
        gp.obj[0].worldX = 1 * gp.tileSize;
        gp.obj[0].worldY = 3 * gp.tileSize;

        // 2. The Central Key (Key for Door 1)
        gp.obj[1] = new OBJ_Key();
        gp.obj[1].worldX = 12 * gp.tileSize;
        gp.obj[1].worldY = 36 * gp.tileSize;

        // 3. Hidden Key 1 (A second Key in the upper left)
        gp.obj[2] = new OBJ_Key();
        gp.obj[2].worldX = 10 * gp.tileSize;
        gp.obj[2].worldY = 10 * gp.tileSize;

        // 4. Hidden Key 2 (A third Key on the right side)
        gp.obj[3] = new OBJ_Key();
        gp.obj[3].worldX = 65 * gp.tileSize;
        gp.obj[3].worldY = 30 * gp.tileSize;

        // 5. The Exit Door (Bottom right corner)
        gp.obj[4] = new OBJ_Door();
        gp.obj[4].worldX = 68 * gp.tileSize;
        gp.obj[4].worldY = 68 * gp.tileSize;
    }
}
```

### Object Placement Visualization

```
┌─────────────────────────────────────────────────────────────┐
│                    WORLD MAP (74x74)                        │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  (1,3)                         (10,10)                      │
│    📦                            🔑                         │
│   Chest                      Hidden Key 1                   │
│                                                             │
│                                                             │
│           (12,36)                                           │
│              🔑                             (65,30)         │
│          Central Key                           🔑           │
│                                            Hidden Key 2     │
│                                                             │
│                                                             │
│                                              (68,68)        │
│                                                 🚪          │
│                                             Exit Door       │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### Position Calculation

```java
gp.obj[0].worldX = 1 * gp.tileSize;  // 1 * 48 = 48 pixels
gp.obj[0].worldY = 3 * gp.tileSize;  // 3 * 48 = 144 pixels
```

Objects are placed using tile coordinates multiplied by tile size for pixel position.

---

## How Objects Are Rendered

In `Gamepanel.paintComponent()`:

```java
// Draw objects (middle layer)
for (int i = 0; i < obj.length; i++) {
    if (obj[i] != null) {
        obj[i].draw(g2, this);
    }
}
```

- Loop through all object slots (max 10)
- Skip null slots (empty or picked up)
- Draw each existing object

---

## Object Interaction (Current)

In `Player.pickUpObject()`:

```java
public void pickUpObject(int i) {
    if (i != 999) {
        gp.obj[i] = null;
    }
}
```

This is **very basic**:
- If collision detected (index != 999)
- Delete the object from the array

**No differentiation** between object types yet. Everything just disappears.

---

## Available Object Sprites (Not All Used)

The `res/objects/` folder contains many sprites for future use:

| File | Used? | Purpose |
|------|-------|---------|
| key.png | ✅ Yes | Collectible key |
| door.png | ✅ Yes | Blocking door |
| chest.png | ✅ Yes | Collectible chest |
| chest_opened.png | ❌ No | Opened chest state |
| door_iron.png | ❌ No | Alternative door |
| axe.png | ❌ No | Weapon? Tool? |
| boots.png | ❌ No | Speed upgrade? |
| coin_bronze.png | ❌ No | Currency |
| heart_full.png | ❌ No | Health display |
| heart_half.png | ❌ No | Health display |
| heart_blank.png | ❌ No | Health display |
| potion_red.png | ❌ No | Health restore |
| sword_normal.png | ❌ No | Weapon |
| shield_wood.png | ❌ No | Defense |
| shield_blue.png | ❌ No | Defense |
| lantern.png | ❌ No | Light source? |
| tent.png | ❌ No | Save point? |
| pickaxe.png | ❌ No | Tool |
| manacrystal_full.png | ❌ No | Magic? |
| manacrystal_blank.png | ❌ No | Magic display |
| blueheart.png | ❌ No | Special item? |

---

## Object Flow Summary

```
┌─────────────────────────────────────────────────────────────┐
│                    OBJECT LIFECYCLE                         │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  1. CREATION (setupGame → AssetSetter.setObject)            │
│     ┌──────────────────────────────────────────────┐        │
│     │ gp.obj[0] = new OBJ_Chest()                  │        │
│     │ gp.obj[0].worldX = 1 * 48                    │        │
│     │ gp.obj[0].worldY = 3 * 48                    │        │
│     └──────────────────────────────────────────────┘        │
│                         │                                   │
│                         ▼                                   │
│  2. RENDERING (paintComponent → obj[i].draw)                │
│     ┌──────────────────────────────────────────────┐        │
│     │ for each obj:                                │        │
│     │   if visible: draw at screenX, screenY      │        │
│     └──────────────────────────────────────────────┘        │
│                         │                                   │
│                         ▼                                   │
│  3. COLLISION (update → checkObject)                        │
│     ┌──────────────────────────────────────────────┐        │
│     │ if player overlaps object:                   │        │
│     │   return object index                        │        │
│     └──────────────────────────────────────────────┘        │
│                         │                                   │
│                         ▼                                   │
│  4. PICKUP (update → pickUpObject)                          │
│     ┌──────────────────────────────────────────────┐        │
│     │ gp.obj[index] = null  // Object removed      │        │
│     └──────────────────────────────────────────────┘        │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## Next: [Collision Detection](./07-collision.md)
