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
