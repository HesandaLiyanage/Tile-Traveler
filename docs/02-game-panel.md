# 2. Game Panel & Game Loop

## File: `src/main/Gamepanel.java`

The `Gamepanel` is the **heart of the game**. It contains:
- Screen and world settings
- The game loop
- All game sub-systems (tile manager, player, collision checker, etc.)
- The rendering pipeline

---

## Class Declaration

```java
public class Gamepanel extends JPanel implements Runnable {
    // ...
}
```

- **Extends `JPanel`** - This is a Swing component that can be drawn on.
- **Implements `Runnable`** - This allows the class to be run as a separate thread (for the game loop).

---

## Screen Settings

```java
// Screen settings
final int originalTileSize = 16;      // Original 16x16 pixel tiles
public final int tileSize = originalTileSize * 3;  // Scaled to 48x48
public int maxScreenCol = 16;         // 16 tiles across
public int maxScreenRow = 12;         // 12 tiles down
public final int screenWidth = tileSize * maxScreenCol;   // 768 pixels
public final int screenHeight = tileSize * maxScreenRow;  // 576 pixels
```

### Why Scale Tiles?

Original game assets are often tiny (16x16 pixels). On modern monitors, this would be extremely small. Scaling by 3x makes each tile 48x48 pixels, which is visible and clear.

### Screen Calculation

```
screenWidth  = 48 × 16 = 768 pixels
screenHeight = 48 × 12 = 576 pixels
```

This creates a 4:3 aspect ratio game window.

---

## World Settings

```java
// World settings
public final int maxWorldCol = 74;
public final int maxWorldRow = 74;
public final int worldWidth = tileSize * maxWorldCol;   // 3552 pixels
public final int worldHeight = tileSize * maxWorldRow;  // 3552 pixels
```

The **world is much larger than the screen**. The screen only shows a portion of the world at any time (the camera view).

```
┌─────────────────────────────────────────────┐
│                   WORLD                     │
│         (74 × 74 tiles = 3552px)            │
│                                             │
│     ┌───────────────────┐                   │
│     │      SCREEN       │                   │
│     │  (16 × 12 tiles)  │                   │
│     │   Player is in    │                   │
│     │     the center    │                   │
│     └───────────────────┘                   │
│                                             │
└─────────────────────────────────────────────┘
```

---

## Game Sub-systems

```java
TileManager tileM = new TileManager(this);
KeyHandler keyH = new KeyHandler();
public Player player = new Player(this, keyH);
Thread gameThread;  // This is the game clock
public SuperObject obj[] = new SuperObject[10];  // Up to 10 objects
public CollisionChecker cChecker = new CollisionChecker(this);
public AssetSetter aSetter = new AssetSetter(this);
```

| Sub-system | Purpose |
|------------|---------|
| `TileManager` | Loads and renders the tile-based world |
| `KeyHandler` | Captures WASD keyboard input |
| `Player` | The player entity with movement and rendering |
| `gameThread` | The separate thread running the game loop |
| `obj[]` | Array of interactive objects (max 10) |
| `CollisionChecker` | Detects collisions between entities and tiles/objects |
| `AssetSetter` | Places objects in the world at startup |

---

## Constructor

```java
public Gamepanel() {
    this.setPreferredSize(new Dimension(screenWidth, screenHeight));
    this.setBackground(Color.black);
    this.setDoubleBuffered(true);
    this.addKeyListener(keyH);
    this.setFocusable(true);
}
```

| Line | Purpose |
|------|---------|
| `setPreferredSize()` | Sets the panel size to 768×576 pixels |
| `setBackground(Color.black)` | Default background is black |
| `setDoubleBuffered(true)` | Prevents flickering during rendering |
| `addKeyListener(keyH)` | Registers the keyboard handler |
| `setFocusable(true)` | Allows the panel to receive keyboard focus |

### Double Buffering

Without double buffering, you'd see the screen being drawn piece by piece, causing flicker. Double buffering draws to an offscreen buffer first, then copies the complete image to the screen.

---

## Setup Game Method

```java
public void setupGame() {
    aSetter.setObject();
}
```

Called from `Main.java` before the game loop starts. This uses `AssetSetter` to place objects (keys, doors, chests) in the world.

---

## Starting the Game Thread

```java
public void startGameThread() {
    gameThread = new Thread(this);
    gameThread.start();
}
```

- Creates a new `Thread` with `this` (Gamepanel) as the `Runnable`.
- `start()` calls the `run()` method in a separate thread.
- This keeps the game running without blocking the UI thread.

---

## The Game Loop

The game loop is the **core of any game**. It runs continuously, updating game state and rendering frames.

### Current Implementation (Delta/Accumulator Method)

```java
public void run() {
    double drawInterval = 1000000000/fps;  // Time per frame in nanoseconds
    double delta = 0;
    double lastTime = System.nanoTime();
    double currentTime;
    
    // FPS tracking
    long timer = 0;
    long drawCount = 0;

    while (gameThread != null) {
        currentTime = System.nanoTime();
        delta += (currentTime - lastTime)/drawInterval;
        timer += currentTime - lastTime;
        lastTime = currentTime;
        
        if (delta >= 1) {
            update();
            repaint();
            delta--;
            drawCount++;
        }

        // FPS display (every second)
        if(timer >= 1000000000) {
            System.out.println("FPS: " + drawCount);
            drawCount = 0;
            timer = 0;
        }
    }
}
```

### How the Delta Method Works

```
┌─────────────────────────────────────────────────────────────┐
│                    GAME LOOP TIMELINE                       │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  Time:  |──16.67ms──|──16.67ms──|──16.67ms──|──16.67ms──|   │
│         ↑           ↑           ↑           ↑               │
│      Frame 1     Frame 2     Frame 3     Frame 4            │
│                                                             │
│  Delta accumulates time since last frame                    │
│  When delta >= 1, enough time has passed for one frame      │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

1. `drawInterval` = 16,666,666 nanoseconds (1 second ÷ 60 FPS)
2. Each loop iteration calculates elapsed time
3. `delta` accumulates fractional frames
4. When `delta >= 1`, a full frame's worth of time has passed
5. Update and draw, then subtract 1 from delta

This ensures the game runs at a consistent 60 FPS regardless of processing speed.

---

## The OLD Game Loop (Commented Out)

The code contains a commented-out alternative game loop method. Let's understand why it was replaced:

```java
// public void run() {
//     double drawInterval = 1000000000/fps;
//     double nextDrawTIme = System.nanoTime() + drawInterval;
//     
//     while (gameThread != null) {
//         update();
//         repaint();
//         
//         try {
//             double remainingTime = nextDrawTIme - System.nanoTime();
//             remainingTime = remainingTime/1000000; // Convert to milliseconds
//             
//             if (remainingTime < 0) {
//                 remainingTime = 0;
//             }
//             
//             Thread.sleep((long) remainingTime);
//             nextDrawTIme += drawInterval;
//         } catch (InterruptedException e) {
//             throw new RuntimeException(e);
//         }
//     }
// }
```

### Sleep Method vs Delta Method

| Aspect | Sleep Method (Old) | Delta Method (Current) |
|--------|-------------------|------------------------|
| **Accuracy** | Less accurate due to `Thread.sleep()` imprecision | More accurate, doesn't rely on sleep |
| **CPU Usage** | Lower CPU (sleeps between frames) | Higher CPU (busy loop) |
| **Frame Skipping** | No frame skip handling | Can handle frame skips |
| **Consistency** | Can have timing issues | More consistent frame timing |

The **Delta method** was chosen because it provides more consistent timing, which is important for smooth gameplay.

---

## Update Method

```java
public void update() {
    player.update();
}
```

Called every frame. Currently only updates the player. In the future, this would also update:
- NPCs
- Enemies
- Projectiles
- Animations
- Game state

---

## Paint Component (Rendering)

```java
public void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g;
    
    // Draw tiles FIRST (background layer)
    tileM.draw(g2);
    
    // Draw objects (middle layer)
    for (int i = 0; i < obj.length; i++) {
        if(obj[i] != null) {
            obj[i].draw(g2, this);
        }
    }
    
    // Draw player LAST (top layer)
    player.draw(g2);
    
    // Dispose graphics context (good practice)
    g2.dispose();
}
```

### Why Graphics2D?

`Graphics2D` extends `Graphics` with more advanced features:
- Better control over shapes and strokes
- Transformation support (rotation, scaling)
- Improved text rendering
- Anti-aliasing control

### Rendering Order (Layers)

```
┌─────────────────────────────────────┐
│          SCREEN (top)               │
├─────────────────────────────────────┤
│  Layer 3: Player (drawn last)       │
├─────────────────────────────────────┤
│  Layer 2: Objects (keys, chests)    │
├─────────────────────────────────────┤
│  Layer 1: Tiles (drawn first)       │
└─────────────────────────────────────┘
```

Things drawn **later** appear **on top** of things drawn earlier. This is why:
1. Tiles are drawn first (background)
2. Objects are drawn on top of tiles
3. Player is drawn last (always visible)

### g2.dispose()

```java
g2.dispose();
```

Releases system resources associated with the graphics context. This is good practice to prevent memory leaks.

---

## Visualizing the Game Loop

```
┌──────────────────────────────────────────────────────────────┐
│                      GAME LOOP                               │
│                  (runs continuously)                         │
└──────────────────────────────────────────────────────────────┘
                            │
                            ▼
              ┌─────────────────────────┐
              │  Calculate delta time   │
              └─────────────────────────┘
                            │
                            ▼
              ┌─────────────────────────┐
              │    delta >= 1 ?         │
              │   (time for a frame?)   │
              └─────────────────────────┘
                     │          │
                    YES         NO
                     │          │
                     ▼          └────┐
        ┌───────────────────────┐    │
        │       update()        │    │
        │  • Update player pos  │    │
        │  • Check collisions   │    │
        │  • Update animations  │    │
        └───────────────────────┘    │
                     │               │
                     ▼               │
        ┌───────────────────────┐    │
        │      repaint()        │    │
        │  (triggers           │    │
        │   paintComponent)    │    │
        └───────────────────────┘    │
                     │               │
                     ▼               │
        ┌───────────────────────┐    │
        │   paintComponent()    │    │
        │  • Draw tiles         │    │
        │  • Draw objects       │    │
        │  • Draw player        │    │
        └───────────────────────┘    │
                     │               │
                     ▼               │
              ┌─────────────────────────┐
              │        Loop back        │◄──┘
              └─────────────────────────┘
```

---

## Next: [Input Handling](./03-input-handling.md)
