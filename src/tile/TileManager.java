package tile;

import main.Gamepanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.GenericArrayType;

public class TileManager{
    Gamepanel gp;
    Tile[] tile;
    int mapTileNum[][];
    public TileManager(Gamepanel gp){
        this.gp = gp;
        tile = new Tile[10];
        mapTileNum = new int[gp.maxWorldCol][gp.maxWorldCol];
        getTileImage();
        loadMap("maps/map02.txt");
    }

    public void getTileImage() {
        try {
            tile[0] = new Tile();
            tile[0].image = ImageIO.read(getClass().getClassLoader().getResourceAsStream("tiles/grass.png"));
            tile[1] = new Tile();
            tile[1].image = ImageIO.read(getClass().getClassLoader().getResourceAsStream("tiles/wall.png"));
            tile[2] = new Tile();
            tile[2].image = ImageIO.read(getClass().getClassLoader().getResourceAsStream("tiles/water.png"));
            tile[3] = new Tile();
            tile[3].image = ImageIO.read(getClass().getClassLoader().getResourceAsStream("tiles/earth.png"));
            tile[4] = new Tile();
            tile[4].image = ImageIO.read(getClass().getClassLoader().getResourceAsStream("tiles/tree.png"));
            tile[5] = new Tile();
            tile[5].image = ImageIO.read(getClass().getClassLoader().getResourceAsStream("tiles/sand.png"));

        }catch (IOException e) {
        e.printStackTrace();
    }
    }

    public void loadMap(String mapFile) {
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream(mapFile);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            int col = 0;
            int row = 0;
            while (col < gp.maxWorldCol && row < gp.maxWorldCol) {
                String line = br.readLine();
                //readline reads a line of text

                while (col < gp.maxWorldCol) {
                    String numbers[] = line.split(" ");
                    int num = Integer.parseInt(numbers[col]);
                    mapTileNum[col][row] = num;
                    col++;
                }
                if(col == gp.maxWorldCol){
                    col = 0;
                    row++;
                }
            }
            br.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void draw(Graphics g2) {
//        g2.drawImage(tile[1].image,0,0,gp.tileSize,gp.tileSize,null);
//        g2.drawImage(tile[1].image,48,0,gp.tileSize,gp.tileSize,null);
//        g2.drawImage(tile[1].image,96,0,gp.tileSize,gp.tileSize,null);
//        g2.drawImage(tile[1].image,144,0,gp.tileSize,gp.tileSize,null);
//        g2.drawImage(tile[1].image,192,0,gp.tileSize,gp.tileSize,null);
//
//        g2.drawImage(tile[1].image,0,48,gp.tileSize,gp.tileSize,null);
//        g2.drawImage(tile[0].image,48,48,gp.tileSize,gp.tileSize,null);
//        g2.drawImage(tile[0].image,96,48,gp.tileSize,gp.tileSize,null);
//        g2.drawImage(tile[0].image,144,48,gp.tileSize,gp.tileSize,null);
//        g2.drawImage(tile[1].image,192,48,gp.tileSize,gp.tileSize,null);
//
//        g2.drawImage(tile[1].image,0,96,gp.tileSize,gp.tileSize,null);
//        g2.drawImage(tile[0].image,48,96,gp.tileSize,gp.tileSize,null);
//        g2.drawImage(tile[0].image,96,96,gp.tileSize,gp.tileSize,null);
//        g2.drawImage(tile[0].image,144,96,gp.tileSize,gp.tileSize,null);
//        g2.drawImage(tile[0].image,192,96,gp.tileSize,gp.tileSize,null);
//
//        g2.drawImage(tile[1].image,0,144,gp.tileSize,gp.tileSize,null);
//        g2.drawImage(tile[0].image,48,144,gp.tileSize,gp.tileSize,null);
//        g2.drawImage(tile[0].image,96,144,gp.tileSize,gp.tileSize,null);
//        g2.drawImage(tile[0].image,144,144,gp.tileSize,gp.tileSize,null);
//        g2.drawImage(tile[1].image,192,144,gp.tileSize,gp.tileSize,null);
//
//        g2.drawImage(tile[1].image,0,192,gp.tileSize,gp.tileSize,null);
//        g2.drawImage(tile[2].image,48,192,gp.tileSize,gp.tileSize,null);
//        g2.drawImage(tile[2].image,96,192,gp.tileSize,gp.tileSize,null);
//        g2.drawImage(tile[2].image,144,192,gp.tileSize,gp.tileSize,null);
//        g2.drawImage(tile[0].image,192,192,gp.tileSize,gp.tileSize,null);
            int WorldCol = 0;
            int WorldRow = 0;
//            int x = 0;
//            int y = 0;
            while (WorldCol < gp.maxWorldCol && WorldRow < gp.maxWorldRow) {

                int tileNum = mapTileNum[WorldCol][WorldRow];

                int worldX = WorldCol * gp.tileSize;
                int worldY = WorldRow * gp.tileSize;

                int screenX = worldX - gp.player.worldX + gp.player.screenX;
                int screenY = worldY - gp.player.worldY + gp.player.screenY;

                if(worldX > gp.player.worldX - gp.player.screenX && worldX < gp.player.worldX - gp.player.screenX &&
                worldY > gp.player.worldY - gp.player.screenY && worldY < gp.player.worldY - gp.player.screenY ) {}
                g2.drawImage(tile[tileNum].image, screenX, screenY, gp.tileSize, gp.tileSize, null);
                WorldCol++;
//                x += gp.tileSize;
                if (WorldCol == gp.maxWorldCol) {
                    WorldCol = 0;
                    WorldRow++;
//                    x = 0;
//                    y += gp.tileSize;
                }
                //16 0s and 12 lines

            }

    }
}
