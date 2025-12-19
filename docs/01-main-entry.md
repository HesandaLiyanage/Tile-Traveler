# 1. Main Entry Point

## File: `src/main/Main.java`

This is where everything begins. The `Main` class contains the entry point (`main` method) for the entire game.

---

## The Code

```java
package main;
import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        Gamepanel gamePanel = new Gamepanel();
        window.add(gamePanel);

        window.pack();

        window.setLocationRelativeTo(null);
        window.setVisible(true);

        gamePanel.setupGame(); //these are the objects
        gamePanel.startGameThread();
    }
}
```

---

## Line-by-Line Breakdown

### 1. Creating the Window

```java
JFrame window = new JFrame();
```

- **JFrame** is the Java Swing class that creates a window (frame) for the application.
- This is the top-level container that holds all UI components.

### 2. Setting Close Operation

```java
window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
```

- When the user clicks the ❌ button, the application will terminate completely.
- `EXIT_ON_CLOSE` calls `System.exit(0)` when the window is closed.

### 3. Disabling Resize

```java
window.setResizable(false);
```

- The window cannot be resized by the user.
- This is important for pixel-perfect games where you want consistent rendering.

### 4. Creating the Game Panel

```java
Gamepanel gamePanel = new Gamepanel();
window.add(gamePanel);
```

- A new `Gamepanel` instance is created.
- The `Gamepanel` extends `JPanel` and contains all game logic.
- It's added to the window as the main content.

### 5. Packing the Window

```java
window.pack();
```

- `pack()` causes the window to resize to fit the preferred size of its subcomponents.
- Since `Gamepanel` sets its preferred size to **768 x 576 pixels**, the window will be that size.

### 6. Centering the Window

```java
window.setLocationRelativeTo(null);
```

- Passing `null` centers the window on the screen.
- Without this, the window would appear at the top-left corner.

### 7. Making the Window Visible

```java
window.setVisible(true);
```

- Shows the window on screen.
- Until this is called, the window exists in memory but isn't rendered.

### 8. Setting Up the Game

```java
gamePanel.setupGame();
```

- Initializes game objects (keys, doors, chests).
- Uses `AssetSetter` to place objects in the world.
- This is called **before** starting the game thread.

### 9. Starting the Game Loop

```java
gamePanel.startGameThread();
```

- Creates and starts a new `Thread` that runs the game loop.
- The game loop continuously updates and repaints the game at 60 FPS.

---

## Visual Flow

```
┌──────────────────────────────────────────────────────────────┐
│                    main(String[] args)                       │
└──────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌──────────────────────────────────────────────────────────────┐
│              Create JFrame (window container)                │
└──────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌──────────────────────────────────────────────────────────────┐
│                    Create Gamepanel                          │
│    ┌─────────────────────────────────────────────────┐       │
│    │  - Sets preferred size (768 x 576)              │       │
│    │  - Sets background to black                     │       │
│    │  - Enables double buffering                     │       │
│    │  - Adds KeyListener                             │       │
│    │  - Creates TileManager                          │       │
│    │  - Creates Player                               │       │
│    │  - Creates CollisionChecker                     │       │
│    │  - Creates AssetSetter                          │       │
│    └─────────────────────────────────────────────────┘       │
└──────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌──────────────────────────────────────────────────────────────┐
│           Configure & display window                         │
│    • pack() → fit to content                                 │
│    • center on screen                                        │
│    • make visible                                            │
└──────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌──────────────────────────────────────────────────────────────┐
│                   setupGame()                                │
│    • Place objects in the world (keys, doors, chests)        │
└──────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌──────────────────────────────────────────────────────────────┐
│               startGameThread()                              │
│    • Create new Thread                                       │
│    • Start game loop (60 FPS)                                │
│    • Game is now running!                                    │
└──────────────────────────────────────────────────────────────┘
```

---

## Why This Order Matters

1. **Window must exist first** - You need a container before adding content.
2. **Gamepanel before pack()** - The window sizes itself to the panel's preferred size.
3. **Visible before game loop** - Ensures rendering has a target.
4. **setupGame() before loop** - Objects must exist before being drawn.
5. **Game loop last** - Everything must be ready before updates begin.

---

## Next: [Game Panel & Loop](./02-game-panel.md)
