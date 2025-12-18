package entity;

import java.awt.*;
import java.awt.image.BufferedImage;

public class  Entity {
    //this is the super class for players
    public int worldX,worldY;
    public int speed;

    //now what we gonna do is , we move the map instead of the player


    public BufferedImage up1,up2,down1,down2,left1,left2,right1,right2;
    public String direction;


    public int spriteCounter = 0;
    public int spriteNum = 1;

    public Rectangle solidarea;
    public int solidAreaDefaultX, solidAreaDefaultY;
    public boolean collisionOn = false;

    //again coordinates and movement speed defined
}
