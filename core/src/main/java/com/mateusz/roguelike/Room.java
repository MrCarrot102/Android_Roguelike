package com.mateusz.roguelike;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Room {
    private List<Rectangle> walls;
    private List<Rectangle> exits;
    private float screenWidth, screenHeight;
    private Random random;
    private RoomType type;
    private List<Rectangle> obstacles;
    private List<Rectangle> treasures;

    public enum RoomType{
        EMPTY,
        PILLARS,
        MAZE,
        TREASURE,
        ENEMY_CAMP
    }

    public Room(float screenWidth, float screenHeight){
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.random = new Random();
        walls = new ArrayList<>();
        exits = new ArrayList<>();

        walls.add(new Rectangle(0, 0, screenWidth, 20));
        walls.add(new Rectangle(0, screenHeight - 20, screenWidth, 20));
        walls.add(new Rectangle(0, 0, 20, screenHeight));
        walls.add(new Rectangle(screenWidth - 20, 0, 20, screenHeight));

        generateExits();
    }

    private void generateExits(){
        int exitCount = 1 + random.nextInt(3); // od 1 do 3 wyjsc na pokoj

        List<String> possibleExits = new ArrayList<>(Arrays.asList("bottom", "top", "left", "right"));
        Collections.shuffle(possibleExits);

        for(int i = 0; i < Math.min(exitCount, possibleExits.size()); i++){
            String exitPos = possibleExits.get(i);
            switch(exitPos){
                case "bottom":
                    exits.add(new Rectangle(screenWidth/2-50,0,100,20));
                    break;
                case "top":
                    exits.add(new Rectangle(screenWidth/2-50,screenHeight-20,100,20));
                    break;
                case "left":
                    exits.add(new Rectangle(0,screenHeight/2-50,20,100));
                    break;
                case "right":
                    exits.add(new Rectangle(screenWidth-20, screenHeight/2-50,20,100));
                    break;
            }
        }
    }

    public void draw(ShapeRenderer shapeRenderer){
        shapeRenderer.setColor(Color.BROWN);
        for(Rectangle wall:walls){
            shapeRenderer.rect(wall.x, wall.y, wall.width, wall.height);
        }

        shapeRenderer.setColor(Color.GREEN);
        for(Rectangle exit : exits){
            shapeRenderer.rect(exit.x, exit.y, exit.width, exit.height);
        }
    }

    public boolean collidesWith(Rectangle bounds){
        for(Rectangle wall : walls){
            if (wall.overlaps(bounds)){
                return true;
            }
        }
        return false;
    }

    public List<Rectangle> getExits(){
        return exits;
    }
}
