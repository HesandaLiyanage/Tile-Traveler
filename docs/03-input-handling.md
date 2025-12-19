# 3. Input Handling

## File: `src/main/KeyHandler.java`

The `KeyHandler` class captures keyboard input and stores the state of movement keys. It implements Java's `KeyListener` interface.

---

## The Code

```java
package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {
    // Side note: we have to always implement all the abstract methods 
    // when implementing from an interface
    
    public boolean upPressed, downPressed, leftPressed, rightPressed;

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        // This returns a number identifying the key pressed
        
        if (code == KeyEvent.VK_W) {
            upPressed = true;
        }
        if (code == KeyEvent.VK_A) {
            leftPressed = true;
        }
        if (code == KeyEvent.VK_S) {
            downPressed = true;
        }
        if (code == KeyEvent.VK_D) {
            rightPressed = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        
        if (code == KeyEvent.VK_W) {
            upPressed = false;
        }
        if (code == KeyEvent.VK_A) {
            leftPressed = false;
        }
        if (code == KeyEvent.VK_S) {
            downPressed = false;
        }
        if (code == KeyEvent.VK_D) {
            rightPressed = false;
        }
    }
}
```

---

## How KeyListener Works

When implementing `KeyListener`, you **must** implement all three methods:

| Method | When Called | Use Case |
|--------|-------------|----------|
| `keyTyped()` | When a character is typed | Text input, typing |
| `keyPressed()` | When a key is pressed down | Movement, actions |
| `keyReleased()` | When a key is released | Stop movement |

For this game, we only use `keyPressed()` and `keyReleased()`.

---

## State Variables

```java
public boolean upPressed, downPressed, leftPressed, rightPressed;
```

These four booleans track the current state of each movement key:
- `true` = key is currently being held down
- `false` = key is not pressed

### Why Booleans Instead of Direct Movement?

The `KeyHandler` doesn't move the player directly. It just records the **state** of keys. This separation is important:

```
┌──────────────────────────────────────────────────────────────┐
│                   INPUT SEPARATION                           │
├──────────────────────────────────────────────────────────────┤
│                                                              │
│   [Keyboard]                                                 │
│       │                                                      │
│       ▼                                                      │
│   ┌───────────────────────┐                                  │
│   │     KeyHandler        │  ← Records key states            │
│   │  - upPressed = true   │                                  │
│   │  - downPressed = false│                                  │
│   │  - leftPressed = false│                                  │
│   │  - rightPressed = true│                                  │
│   └───────────────────────┘                                  │
│            │                                                 │
│            ▼ (read each frame)                               │
│   ┌───────────────────────┐                                  │
│   │     Player.update()   │  ← Uses states to move           │
│   │  if(keyH.upPressed)   │                                  │
│   │    worldY -= speed;   │                                  │
│   └───────────────────────┘                                  │
│                                                              │
└──────────────────────────────────────────────────────────────┘
```

**Benefits:**
1. **Frame-independent input** - Movement happens in `update()`, not in the event handler
2. **Consistent movement** - Player moves the same amount every frame
3. **Multiple keys** - Can detect diagonal movement (W+D = up-right)
4. **Decoupling** - Input handling is separate from game logic

---

## Key Press Events

### keyPressed()

```java
@Override
public void keyPressed(KeyEvent e) {
    int code = e.getKeyCode();
    
    if (code == KeyEvent.VK_W) {
        upPressed = true;
    }
    // ... other keys
}
```

When a key is pressed:
1. `getKeyCode()` returns a unique integer for each key
2. Compare with `KeyEvent.VK_*` constants
3. Set the corresponding boolean to `true`

### keyReleased()

```java
@Override
public void keyReleased(KeyEvent e) {
    int code = e.getKeyCode();
    
    if (code == KeyEvent.VK_W) {
        upPressed = false;
    }
    // ... other keys
}
```

When a key is released, reset the boolean to `false`.

---

## Key Codes Reference

| Key | KeyEvent Constant | Value |
|-----|------------------|-------|
| W | `VK_W` | 87 |
| A | `VK_A` | 65 |
| S | `VK_S` | 83 |
| D | `VK_D` | 68 |
| Up Arrow | `VK_UP` | 38 |
| Down Arrow | `VK_DOWN` | 40 |
| Left Arrow | `VK_LEFT` | 37 |
| Right Arrow | `VK_RIGHT` | 39 |
| Space | `VK_SPACE` | 32 |
| Escape | `VK_ESCAPE` | 27 |
| Enter | `VK_ENTER` | 10 |

---

## How It's Connected

In `Gamepanel.java`:

```java
KeyHandler keyH = new KeyHandler();
// ...
this.addKeyListener(keyH);
this.setFocusable(true);
```

And passed to the Player:

```java
public Player player = new Player(this, keyH);
```

The Player then reads the key states in its `update()` method:

```java
if (keyH.upPressed == true) {
    direction = "up";
}
```

---

## Handling Diagonal Movement

The current implementation **does not** support true diagonal movement. If both W and D are pressed:

```java
// In Player.update()
if (keyH.upPressed == true) {
    direction = "up";
} else if (keyH.downPressed == true) {
    direction = "down";
} else if (keyH.leftPressed == true) {
    direction = "left";
} else if (keyH.rightPressed == true) {
    direction = "right";
}
```

Because of `else if`, only **one** direction is chosen. The priority is:
1. Up (highest)
2. Down
3. Left
4. Right (lowest)

---

## Timing Diagram

```
Time ─────────────────────────────────────────────────────▶

Key W:   ├──────PRESSED──────┤
                              └──RELEASED

upPressed: false→true─────────true→false

Frame 1: [update reads true] → Player moves up
Frame 2: [update reads true] → Player moves up
Frame 3: [update reads true] → Player moves up
Frame 4: [update reads false] → Player stops
```

---

## Common Issues and Notes

### Issue: Keys "Sticking"

If the window loses focus while a key is pressed, `keyReleased()` might not be called, causing the character to keep moving.

**Future Fix:** Reset all booleans when the window loses focus.

### Issue: Key Ghosting

Some keyboards can't detect certain 3+ key combinations due to hardware limitations. This is a keyboard issue, not a software issue.

---

## Next: [Entity System](./04-entity-system.md)
