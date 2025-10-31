package main;

import javax.swing.*;
import java.awt.*;

public class Gamepanel extends JPanel implements Runnable {
    //screen settings
    final int originalTileSize = 16; //16x16 tiles
    final int tileSize = originalTileSize * 3; //48x48 tiles
    final int maxScreenCol = 16;
    final int maxScreenRow = 12;
    final int screenWidth = tileSize * maxScreenCol; //768 pixels
    final int screenHeight = tileSize * maxScreenRow; //576 pixels

    Thread gameThread; //this is the game clock

    public Gamepanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
    }

    public void startGameThread() {

        gameThread = new Thread(this);
        gameThread.start();
    }
    public void run() {
        while (gameThread != null) {
            System.out.println("Game loop is running");
            //what will be updated each loop
            //update information such as character positions
            //draw the screen with the updated information
        }
    }

        public void update(){}

        public void paintComponent(Graphics g){
        super.paintComponent(g);
        }

}

