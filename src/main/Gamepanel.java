package main;

import entity.Player;

import javax.swing.*;
import java.awt.*;

public class Gamepanel extends JPanel implements Runnable {
    //screen settings
    final int originalTileSize = 16; //16x16 tiles
    public final int tileSize = originalTileSize * 3; //48x48 tiles
    final int maxScreenCol = 16;
    final int maxScreenRow = 12;
    final int screenWidth = tileSize * maxScreenCol; //768 pixels
    final int screenHeight = tileSize * maxScreenRow; //576 pixels

    //fps
    int fps = 60;
    KeyHandler keyH = new KeyHandler();
    Player player = new Player(this, keyH);
    Thread gameThread; //this is the game clock

    //set players default position
    int playerX = 100;
    int playerY = 100;
    int playerSpeed = 4;

    public Gamepanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
    }

    public void startGameThread() {

        gameThread = new Thread(this);
        gameThread.start();
    }
//    public void run() {
//
//        double drawInterval = 1000000000/fps; //0.01666 seconds
//        double nextDrawTIme = System.nanoTime() + drawInterval;
//        while (gameThread != null) {
//
//            //what will be updated each loop
//            //update information such as character positions
//            update();
//            //draw the screen with the updated information
//            repaint();
//            try {
//                double remainingTime = nextDrawTIme - System.nanoTime();
//                remainingTime = remainingTime/1000000; //converted to milli cus sleep only accepts millis
//                if (remainingTime < 0){
//                    remainingTime = 0;
//                }//btw this wont happen in this little game but just in case hehe
//
//                Thread.sleep((long) remainingTime);
//                nextDrawTIme += drawInterval;
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//
//        }
//    }
    //this have the thread normal game loop method

    public void run() {
        double drawInterval = 1000000000/fps;
        double delta = 0;
        double lastTime = System.nanoTime();
        double currentTime;
        //to check fps
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

            if( timer >= 1000000000) {
                System.out.println("FPS: " + drawCount);
                drawCount = 0;
                timer = 0;
            }


        }
    }

        public void update(){
            player.update();
    }

        public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        //graphics2d has bit more functions
            //we can use this to draw , thats why we converqt to graphics2d

            player.draw(g2);
            //for now we using tilesize but then we can use this make the character as well
            g2.dispose();
            //this will dispose the graphics2d object so that we can use it again
            //its a good practice btw
        }

}

