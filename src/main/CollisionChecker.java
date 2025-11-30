package main;

import entity.Entity;

public class CollisionChecker {

    Gamepanel gp;

    public CollisionChecker(Gamepanel gp ) {
        this.gp = gp;
    }

    public void checkTile(Entity entity) {

        int entityLeftWorldX = entity.worldX + entity.solidarea.x;
        int entityRightWorldX = entity.worldX + entity.solidarea.x;
        int entityTopWorldY = entity.worldY + entity.solidarea.y;
    }
}
