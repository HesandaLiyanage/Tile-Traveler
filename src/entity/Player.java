package entity;

import main.Gamepanel;
import main.KeyHandler;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class Player extends Entity {
    Gamepanel gp;
    KeyHandler keyH;

    public Player(Gamepanel gp, KeyHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;
        setDefaultValues();
        getPlayerImage();
    }

    public void setDefaultValues(){
        x = 100;
        y = 100;
        speed = 4;
        direction = "down";
    }

    public void getPlayerImage() {
        try{
            up1 = ImageIO.read(getClass().getClassLoader().getResourceAsStream("/player/boy_up_1.png"));
            up2 = ImageIO.read(getClass().getClassLoader().getResourceAsStream("/player/boy_up_2.png"));
            down1 = ImageIO.read(getClass().getClassLoader().getResourceAsStream("/player/boy_down_1.png"));
            down2 = ImageIO.read(getClass().getClassLoader().getResourceAsStream("/player/boy_down_2.png"));
            left1 = ImageIO.read(getClass().getClassLoader().getResourceAsStream("/player/boy_left_1.png"));
            left2 = ImageIO.read(getClass().getClassLoader().getResourceAsStream("/player/boy_left_2.png"));
            right1 = ImageIO.read(getClass().getClassLoader().getResourceAsStream("/player/boy_right_1.png"));
            right2 = ImageIO.read(getClass().getClassLoader().getResourceAsStream("/player/boy_right_2.png"));


        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void update(){

        if (keyH.upPressed) {
            direction = "up";
            y -= speed;
        }
        else if (keyH.downPressed) {
            direction = "down";
            y += speed;
        }
        else if (keyH.leftPressed) {
            direction = "left";
            x -= speed;
        }
        else if (keyH.rightPressed) {
            direction = "right";
            x += speed;
        }
    }

    public void draw(Graphics g2){
//        g2.setColor(Color.white);
//        g2.fillRect(x,y, gp.tileSize, gp.tileSize);
        //there is no rect in here
        //we need to draw the image
        BufferedImage image = null;

        switch (direction) {
            case "up":
                image = up1;
                break;
            case "down" :
                image = down1;
                break;
            case "left" :
                image = left1;
                break;
            case "right":
                image = right1;
                break;
        }

        g2.drawImage(image,x,y,gp.tileSize,gp.tileSize,null);
        //this null is called image observer
    }

}
