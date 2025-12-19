# 7. Collision Detection

## File: `src/main/CollisionChecker.java`

The collision system handles detecting when entities bump into tiles or objects.

---

## Class Structure

```java
package main;

import entity.Entity;

public class CollisionChecker {
    Gamepanel gp;

    public CollisionChecker(Gamepanel gp) {
        this.gp = gp;
    }

    public void checkTile(Entity entity) { /* ... */ }
    public int checkObject(Entity entity, boolean player) { /* ... */ }
}
```

---

## How Collision Works - The Concept

Collision detection checks if two rectangles overlap. Each entity has a `solidarea` (collision box) that's smaller than its sprite.

```
┌─────────────────────────────────────────────────────────────┐
│                COLLISION BOX CONCEPT                        │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  Player Sprite (48x48)         Actual Collision (32x32)     │
│  ┌────────────────────┐        ┌────────────────────┐       │
│  │                    │        │    ┌──────────┐    │       │
│  │       Head         │        │    │          │    │       │
│  │      .-'''-.       │   →    │    │ Collision│    │       │
│  │     /       \      │        │    │   Box    │    │       │
│  │    |  O   O  |     │        │    │          │    │       │
│  │     \  ___  /      │        │    └──────────┘    │       │
│  │      Body          │        │                    │       │
│  └────────────────────┘        └────────────────────┘       │
│                                                             │
│  The head can "overlap" walls visually, but collision       │
│  only happens with the smaller body hitbox.                 │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## checkTile() - Tile Collision Detection

```java
public void checkTile(Entity entity) {
    // Calculate entity collision box edges in world coordinates
    int entityLeftWorldX = entity.worldX + entity.solidarea.x;
    int entityRightWorldX = entity.worldX + entity.solidarea.x + entity.solidarea.width;
    int entityTopWorldY = entity.worldY + entity.solidarea.y;
    int entityBottomWorldY = entity.worldY + entity.solidarea.y + entity.solidarea.height;

    // Convert to tile coordinates
    int entityLeftCol = entityLeftWorldX / gp.tileSize;
    int entityRightCol = entityRightWorldX / gp.tileSize;
    int entityTopRow = entityTopWorldY / gp.tileSize;
    int entityBottomRow = entityBottomWorldY / gp.tileSize;

    int tileNum1, tileNum2;

    switch(entity.direction) {
        case "up":
            // Check tile at predicted future position
            entityTopRow = (entityTopWorldY - entity.speed) / gp.tileSize;
            tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityTopRow];
            tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityTopRow];
            if(gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) {
                entity.collisionOn = true;
            }
            break;
        // ... similar for down, left, right
    }
}
```

### Step-by-Step Breakdown

#### Step 1: Calculate Collision Box World Position

```java
int entityLeftWorldX = entity.worldX + entity.solidarea.x;
// Example: 1104 + 8 = 1112 pixels
```

```
┌─────────────────────────────────────────────────────────────┐
│              COLLISION BOX POSITION                         │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  entity.worldX = 1104 (sprite top-left)                     │
│                                                             │
│  worldX = 1104                                              │
│  ↓                                                          │
│  ┌────────────────────────────────────┐                     │
│  │        Sprite                      │                     │
│  │   ┌────────────────────────┐       │                     │
│  │   │    Collision Box       │       │                     │
│  │   │                        │       │                     │
│  │   └────────────────────────┘       │                     │
│  │                                    │                     │
│  └────────────────────────────────────┘                     │
│       ↑                                                     │
│       entityLeftWorldX = 1104 + 8 = 1112                    │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

#### Step 2: Convert to Tile Coordinates

```java
int entityLeftCol = entityLeftWorldX / gp.tileSize;
// Example: 1112 / 48 = 23 (tile column 23)
```

This tells us which tile the entity is in.

#### Step 3: Predict Future Position

```java
case "up":
    entityTopRow = (entityTopWorldY - entity.speed) / gp.tileSize;
```

If moving up, check where the TOP edge will be after moving by `speed` pixels.

#### Step 4: Check Two Tiles

```java
tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityTopRow];
tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityTopRow];
```

Why two tiles? The entity might span two tiles:

```
┌─────────────────────────────────────────────────────────────┐
│                TWO-TILE CHECK                               │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  Tiles:    │  Tile A   │  Tile B   │                        │
│            │           │           │                        │
│            │     ┌─────┼─────┐     │                        │
│            │     │  Entity  │     │                        │
│            │     │  spans   │     │                        │
│            │     │  both    │     │                        │
│            │     └─────┼─────┘     │                        │
│            │           │           │                        │
│                                                             │
│  Must check BOTH tiles for collision!                       │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

#### Step 5: Set Collision Flag

```java
if (gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) {
    entity.collisionOn = true;
}
```

If either tile has collision enabled, set the flag.

---

## All Four Directions

```
┌─────────────────────────────────────────────────────────────┐
│              DIRECTION-BASED TILE CHECKS                    │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│           UP: Check top-left and top-right tiles            │
│                      ┌───┬───┐                              │
│                      │ 1 │ 2 │ ← Check these                │
│                      ├───┴───┤                              │
│                      │ Entity│                              │
│                      └───────┘                              │
│                                                             │
│         DOWN: Check bottom-left and bottom-right tiles      │
│                      ┌───────┐                              │
│                      │ Entity│                              │
│                      ├───┬───┤                              │
│                      │ 1 │ 2 │ ← Check these                │
│                      └───┴───┘                              │
│                                                             │
│         LEFT: Check top-left and bottom-left tiles          │
│              ┌───┬───────┐                                  │
│   Check →    │ 1 │       │                                  │
│   these      ├───┤Entity │                                  │
│              │ 2 │       │                                  │
│              └───┴───────┘                                  │
│                                                             │
│        RIGHT: Check top-right and bottom-right tiles        │
│              ┌───────┬───┐                                  │
│              │       │ 1 │    ← Check these                 │
│              │Entity ├───┤                                  │
│              │       │ 2 │                                  │
│              └───────┴───┘                                  │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## checkObject() - Object Collision Detection

```java
public int checkObject(Entity entity, boolean player) {
    int index = 999;  // 999 = no collision

    for (int i = 0; i < gp.obj.length; i++) {
        if (gp.obj[i] != null) {
            // Get entity's solid area position
            entity.solidarea.x = entity.worldX + entity.solidarea.x;
            entity.solidarea.y = entity.worldY + entity.solidarea.y;

            gp.obj[i].solidArea.x = gp.obj[i].worldX + gp.obj[i].solidArea.x;
            gp.obj[i].solidArea.y = gp.obj[i].worldY + gp.obj[i].solidArea.y;

            switch(entity.direction) {
                case "up":
                    entity.solidarea.y -= entity.speed;
                    if (entity.solidarea.intersects(gp.obj[i].solidArea)) {
                        if (gp.obj[i].collision == true) {
                            entity.collisionOn = true;
                        }
                        if (player == true) {
                            index = i;
                        }
                        index = i;
                    }
                    break;
                // ... other directions
            }
            
            // Reset solid areas to defaults
            entity.solidarea.x = entity.solidAreaDefaultX;
            entity.solidarea.y = entity.solidAreaDefaultY;
            gp.obj[i].solidArea.x = gp.obj[i].solidAreaDefaultX;
            gp.obj[i].solidArea.y = gp.obj[i].solidAreaDefaultY;
        }
    }
    return index;
}
```

### How It Works

1. **Loop through all objects**
2. **Convert collision boxes to world coordinates**
3. **Move entity's collision box in the movement direction**
4. **Use `Rectangle.intersects()` to check overlap**
5. **If collision:**
   - If object has collision, block movement
   - Return the object's index (for pickup/interaction)
6. **Reset collision boxes to defaults**

### Why Reset at the End?

The solid area rectangles are modified to world coordinates during checking. They must be reset for the next collision check cycle.

```java
entity.solidarea.x = entity.solidAreaDefaultX;  // Reset to 8
entity.solidarea.y = entity.solidAreaDefaultY;  // Reset to 16
```

---

## The Return Value

```java
int objIndex = gp.cChecker.checkObject(this, true);
```

- Returns `999` if no collision
- Returns object index (0-9) if collision occurred

This is used in `Player.pickUpObject()` to know which object to remove.

---

## Developer Note in Code

```java
// ISSUE - character is floating somehow - FIXES
```

This comment indicates there was (or might still be) a visual issue where the character appears to float above tiles. This is typically caused by collision box alignment issues.

---

## Collision Flow Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                  COLLISION DETECTION FLOW                   │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  Player presses movement key                                │
│              │                                              │
│              ▼                                              │
│  ┌───────────────────────────┐                              │
│  │  collisionOn = false      │  ← Reset flag                │
│  └───────────────────────────┘                              │
│              │                                              │
│              ▼                                              │
│  ┌───────────────────────────┐                              │
│  │  checkTile(player)        │                              │
│  │  • Calculate future pos   │                              │
│  │  • Check 2 tiles          │                              │
│  │  • Set collisionOn = true │  (if blocked)                │
│  └───────────────────────────┘                              │
│              │                                              │
│              ▼                                              │
│  ┌───────────────────────────┐                              │
│  │  checkObject(player)      │                              │
│  │  • Loop all objects       │                              │
│  │  • Check intersection     │                              │
│  │  • Return index or 999    │                              │
│  └───────────────────────────┘                              │
│              │                                              │
│              ▼                                              │
│  ┌───────────────────────────┐                              │
│  │  collisionOn == false?    │                              │
│  └───────────────────────────┘                              │
│         │            │                                      │
│        YES          NO                                      │
│         │            │                                      │
│         ▼            ▼                                      │
│  ┌─────────────┐  ┌─────────────┐                           │
│  │    MOVE     │  │ DON'T MOVE  │                           │
│  │  player by  │  │  (blocked)  │                           │
│  │   speed     │  │             │                           │
│  └─────────────┘  └─────────────┘                           │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## Potential Issues in Current Code

### Issue 1: Duplicate Index Assignment

```java
if (player == true) {
    index = i;
}
index = i;  // This always runs, making the if-check pointless
```

The second `index = i;` should probably be inside an `else` block, or the first assignment is redundant.

### Issue 2: Direct Modification of solidarea

```java
entity.solidarea.x = entity.worldX + entity.solidarea.x;
```

This modifies `solidarea.x` by adding `worldX` to it. But `solidarea.x` is supposed to be a **relative offset**, not an absolute position. This works because it's reset at the end, but it's confusing.

A cleaner approach would use temporary variables:
```java
int entitySolidX = entity.worldX + entity.solidarea.x;
// Use entitySolidX for calculations without modifying original
```

---

## Next: [Suggestions & Improvements](./08-suggestions.md)
