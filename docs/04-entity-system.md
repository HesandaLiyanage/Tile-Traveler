# 4. Entity System

## Overview

The entity system defines a base class for all moving, interactable things in the game. Currently, only the Player exists, but this foundation would support NPCs, enemies, and projectiles.

---

## File: `src/entity/Entity.java` (Base Class)

```java
package entity;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Entity {
    // This is the super class for players
    
    public int worldX, worldY;
    public int speed;

    // Now what we gonna do is, we move the map instead of the player

    public BufferedImage up1, up2, down1, down2, left1, left2, right1, right2;
    public String direction;

    public int spriteCounter = 0;
    public int spriteNum = 1;

    public Rectangle solidarea;
    public int solidAreaDefaultX, solidAreaDefaultY;
    public boolean collisionOn = false;
}
```

---

## Entity Properties Explained

### Position

```java
public int worldX, worldY;
```

The entity's position **in the world**, not on screen. This is crucial for the camera system.

```
┌─────────────────────────────────────────────────────────────┐
│                         WORLD                               │
│                                                             │
│  (0,0)                                                      │
│    ┌───────────────────────────────────────────────────┐    │
│    │                                                   │    │
│    │                                                   │    │
│    │            Player at                              │    │
│    │            worldX=1104                            │    │
│    │            worldY=1008                            │    │
│    │                •                                  │    │
│    │                                                   │    │
│    │                                                   │    │
│    └───────────────────────────────────────────────────┘    │
│                                               (3552, 3552)  │
└─────────────────────────────────────────────────────────────┘
```

### Speed

```java
public int speed;
```

Pixels moved per frame. At 60 FPS with speed 4:
- 4 pixels × 60 frames = 240 pixels per second
- 240 ÷ 48 (tile size) = 5 tiles per second

### Sprite Images

```java
public BufferedImage up1, up2, down1, down2, left1, left2, right1, right2;
```

Two frames per direction for walking animation:

```
Direction UP:        Direction DOWN:      Direction LEFT:     Direction RIGHT:
  ┌─────┐ ┌─────┐      ┌─────┐ ┌─────┐      ┌─────┐ ┌─────┐      ┌─────┐ ┌─────┐
  │ up1 │ │ up2 │      │down1│ │down2│      │left1│ │left2│      │rght1│ │rght2│
  └─────┘ └─────┘      └─────┘ └─────┘      └─────┘ └─────┘      └─────┘ └─────┘
     ↔ alternate          ↔ alternate          ↔ alternate          ↔ alternate
```

### Direction

```java
public String direction;
```

Current facing direction: `"up"`, `"down"`, `"left"`, or `"right"`. Used for:
- Determining which sprite to display
- Collision detection direction checking

### Sprite Animation

```java
public int spriteCounter = 0;
public int spriteNum = 1;
```

| Variable | Purpose |
|----------|---------|
| `spriteCounter` | Counts frames since last sprite change |
| `spriteNum` | Current sprite (1 or 2) |

Animation logic (in Player):
```java
spriteCounter++;
if (spriteCounter >= 10) {
    if (spriteNum == 1) {
        spriteNum = 2;
    } else {
        spriteNum = 1;
    }
    spriteCounter = 0;
}
```

This creates a walking animation that alternates every 10 frames (6 times per second at 60 FPS).

### Collision Properties

```java
public Rectangle solidarea;
public int solidAreaDefaultX, solidAreaDefaultY;
public boolean collisionOn = false;
```

| Property | Purpose |
|----------|---------|
| `solidarea` | Rectangle defining the entity's collision box |
| `solidAreaDefaultX/Y` | Default position of collision box (for reset) |
| `collisionOn` | Flag set true when collision is detected |

---

## File: `src/entity/Player.java`

The Player class extends Entity and adds player-specific functionality.

### Class Structure

```java
public class Player extends Entity {
    Gamepanel gp;
    KeyHandler keyH;

    public final int screenX;
    public final int screenY;
    
    // ... methods
}
```

### Constructor

```java
public Player(Gamepanel gp, KeyHandler keyH) {
    this.gp = gp;
    this.keyH = keyH;

    // Player is ALWAYS at the center of the screen
    screenX = gp.screenWidth/2 - (gp.tileSize / 2);   // 768/2 - 24 = 360
    screenY = gp.screenHeight/2 - (gp.tileSize / 2);  // 576/2 - 24 = 264

    // Define collision box (smaller than sprite)
    solidarea = new Rectangle();
    solidarea.x = 8;          // 8 pixels from left of sprite
    solidarea.y = 16;         // 16 pixels from top of sprite
    solidAreaDefaultX = solidarea.x;
    solidAreaDefaultY = solidarea.y;
    solidarea.width = 32;     // 32 pixels wide
    solidarea.height = 32;    // 32 pixels tall

    setDefaultValues();
    getPlayerImage();
}
```

### The Camera System: A Key Insight

```java
public final int screenX;
public final int screenY;
```

The comment in `Entity.java` says:
> "Now what we gonna do is, we move the map instead of the player"

This is the **camera system**. The player **doesn't move on screen** - they always stay in the center. Instead, the **world moves around them**.

```
┌─────────────────────────────────────────────────────────────┐
│                    CAMERA SYSTEM                            │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  WRONG WAY (moving player):                                 │
│  ┌─────────────────────┐                                    │
│  │ Screen (fixed)      │                                    │
│  │                     │                                    │
│  │         ──────→     │  ← Player moves right              │
│  │        •            │                                    │
│  │                     │                                    │
│  └─────────────────────┘                                    │
│                                                             │
│  RIGHT WAY (moving world):                                  │
│  ┌─────────────────────┐                                    │
│  │ ←──── World moves   │                                    │
│  │                     │                                    │
│  │         •           │  ← Player stays centered           │
│  │                     │                                    │
│  │              ←────  │                                    │
│  └─────────────────────┘                                    │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### The Collision Box (Solid Area)

```java
solidarea.x = 8;
solidarea.y = 16;
solidarea.width = 32;
solidarea.height = 32;
```

The collision box is **smaller** than the sprite:

```
┌────────────────────────────────────────┐
│           48px (full sprite)           │
│  ┌──────────────────────────────────┐  │
│  │                                  │  │ 16px gap (for head above body)
│  │  ┌────────────────────────────┐  │  │
│  │  │                            │  │  │
│  │  │    32x32 collision box     │  │  │ 
│  │  │    (actual hitbox)         │  │  │ 32px height
│  │  │                            │  │  │
│  │  └────────────────────────────┘  │  │
│  │                                  │  │
│  └──────────────────────────────────┘  │
│  8px                              8px  │
└────────────────────────────────────────┘
        32px (collision width)
```

This allows the character's head to appear to be "in front of" walls without triggering collision.

---

## setDefaultValues()

```java
public void setDefaultValues() {
    worldX = gp.tileSize * 23;  // Starting X position (tile 23)
    worldY = gp.tileSize * 21;  // Starting Y position (tile 21)
    speed = 4;
    direction = "down";
}
```

Sets the player's starting position and initial direction.

---

## getPlayerImage()

```java
public void getPlayerImage() {
    try {
        up1 = ImageIO.read(getClass().getClassLoader()
            .getResourceAsStream("player/boy_up_1.png"));
        up2 = ImageIO.read(getClass().getClassLoader()
            .getResourceAsStream("player/boy_up_2.png"));
        // ... load all 8 sprites
    } catch(IOException e) {
        e.printStackTrace();
    }
}
```

Loads all 8 sprite images from the resources folder.

---

## update() Method

```java
public void update() {
    if (keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed) {
        
        // Determine direction from input
        if (keyH.upPressed == true) {
            direction = "up";
        } else if (keyH.downPressed == true) {
            direction = "down";
        } else if (keyH.leftPressed == true) {
            direction = "left";
        } else if (keyH.rightPressed == true) {
            direction = "right";
        }

        // Check for collisions
        collisionOn = false;
        gp.cChecker.checkTile(this);
        
        int objIndex = gp.cChecker.checkObject(this, true);
        pickUpObject(objIndex);

        // Move if no collision
        if (collisionOn == false) {
            switch (direction) {
                case "up":    worldY -= speed; break;
                case "down":  worldY += speed; break;
                case "left":  worldX -= speed; break;
                case "right": worldX += speed; break;
            }
        }

        // Update animation
        spriteCounter++;
        if (spriteCounter >= 10) {
            spriteNum = (spriteNum == 1) ? 2 : 1;
            spriteCounter = 0;
        }
    }
}
```

### Update Flow

```
┌──────────────────────────────────────────────────────────────┐
│                     Player.update()                          │
└──────────────────────────────────────────────────────────────┘
                            │
                            ▼
              ┌─────────────────────────┐
              │  Any key pressed?       │
              └─────────────────────────┘
                    │            │
                   YES           NO
                    │            │
                    ▼            └ Do nothing (no animation)
        ┌───────────────────┐
        │ Set direction     │
        │ based on key      │
        └───────────────────┘
                    │
                    ▼
        ┌───────────────────┐
        │ Check tile        │
        │ collision         │
        └───────────────────┘
                    │
                    ▼
        ┌───────────────────┐
        │ Check object      │
        │ collision         │
        └───────────────────┘
                    │
                    ▼
        ┌───────────────────┐
        │ collisionOn?      │
        └───────────────────┘
              │            │
            FALSE         TRUE
              │            │
              ▼            └ Don't move
        ┌───────────────────┐
        │ Move player       │
        │ by speed pixels   │
        └───────────────────┘
                    │
                    ▼
        ┌───────────────────┐
        │ Update animation  │
        │ sprite counter    │
        └───────────────────┘
```

### Developer Note in Code

There's a comment in the code:
```java
// player image changes every 10 frames.
// and this SUCKS.
// Because even when the player is not moving, character is wanenava mokada eka wage
```

This notes that the animation only plays when keys are pressed. When stationary, the character freezes on the last frame (doesn't idle animate).

---

## pickUpObject()

```java
public void pickUpObject(int i) {
    if (i != 999) {
        gp.obj[i] = null;
    }
}
```

Currently **very simple**:
- If an object was collided with (index != 999)
- Delete it from the object array

This just makes objects disappear when touched. There's no inventory, no key mechanics yet.

---

## draw() Method

```java
public void draw(Graphics g2) {
    BufferedImage image = null;

    switch (direction) {
        case "up":
            if (spriteNum == 1) { image = up1; }
            else if (spriteNum == 2) { image = up2; }
            break;
        case "down":
            // ... similar for all directions
    }

    g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
}
```

### Key Points:
1. Select image based on direction and sprite number
2. Draw at **screenX, screenY** (center of screen), NOT worldX, worldY
3. Scale to tile size (48x48)

---

## Next: [Tile System](./05-tile-system.md)
