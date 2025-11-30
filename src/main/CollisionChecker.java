package main;

import entity.Entity;

public class CollisionChecker {

    Gamepanel gp;

    public CollisionChecker(Gamepanel gp ) {
        this.gp = gp;
    }

    public void checkTile(Entity entity) {

        int entityLeftWorldX = entity.worldX + entity.solidarea.x;
        int entityRightWorldX = entity.worldX + entity.solidarea.x +  entity.solidarea.width;
        int entityTopWorldY = entity.worldY + entity.solidarea.y;
        int entityBottomWorldY = entity.worldY + entity.solidarea.y + entity.solidarea.height;

        int entityLeftCol = entityLeftWorldX/gp.tileSize;
        int entityRightCol = entityRightWorldX/gp.tileSize;
        int entityTopRow = entityTopWorldY/gp.tileSize;
        int entityBottomRow = entityBottomWorldY/gp.tileSize;

        int tileNum1, tileNum2;

        switch(entity.direction) {
            case "up":
                break;
                case "down":
                    break;
                    case "left":
                        tileNum1 = entityLeftWorldX/gp.tileSize;
                        tileNum2 = entityRightWorldX/gp.tileSize;
                        break;
                        case "right":
                            tileNum1 = entityRightWorldX/gp.tileSize;
                            tileNum2 = entityTopWorldY/gp.tileSize;
                            break;
        }
    }
}
