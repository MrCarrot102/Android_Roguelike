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

    public Room(float screenWidth, float screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.random = new Random();

        this.walls = new ArrayList<>();
        this.exits = new ArrayList<>();
        this.obstacles = new ArrayList<>();
        this.treasures = new ArrayList<>();  // To było brakujące!

        this.type = RoomType.values()[new Random().nextInt(RoomType.values().length)];
        generateRoom(screenWidth, screenHeight);

        generateExits();
    }
    private void generateRoom(float width, float height){
        walls = new ArrayList<>();
        exits = new ArrayList<>();

        walls.add(new Rectangle(0, 0, screenWidth, 20));
        walls.add(new Rectangle(0, screenHeight - 20, screenWidth, 20));
        walls.add(new Rectangle(0, 0, 20, screenHeight));
        walls.add(new Rectangle(screenWidth - 20, 0, 20, screenHeight));

        switch (type){
            case PILLARS:
                generatePillars(width, height);
                break;
            case MAZE:
                generateMaze(width, height);
                break;
            case TREASURE:
                generateTresures(width, height);
                break;
            case ENEMY_CAMP:
                generateEnemyCamp(width, height);
                break;
        }
    }
    private void generatePillars(float width, float height){
        int pillarCount = 3 + new Random().nextInt(3);
        for(int i = 0; i < pillarCount; i++){
            float x = 100 + new Random().nextFloat() * (width - 200);
            float y = 100 + new Random().nextFloat() * (height - 200);
            obstacles.add(new Rectangle(x,y,40,40));
        }
    }

    private void generateMaze(float width, float height){
        for(int x = 0; x < width; x+=100){
            for(int y = 0; y < height; y+=100){
                if(new Random().nextFloat() > 0.7f){
                    obstacles.add(new Rectangle(x,y,80,20));
                    obstacles.add(new Rectangle(x,y,20,80));
                }
            }
        }
    }

    private void generateTresures(float width, float height){
        int pillarCount = 3 + new Random().nextInt(3);
        for(int i = 0; i < pillarCount; i++){
            float x = 100 + new Random().nextFloat() * (width - 200);
            float y = 100 + new Random().nextFloat() * (height - 200);
            obstacles.add(new Rectangle(x,y,40,40));
        }
    }
    private void generateEnemyCamp(float width, float height){
        int pillarCount = 3 + new Random().nextInt(3);
        for(int i = 0; i < pillarCount; i++){
            float x = 100 + new Random().nextFloat() * (width - 200);
            float y = 100 + new Random().nextFloat() * (height - 200);
            obstacles.add(new Rectangle(x,y,40,40));
        }
    }


    private void generateExits() {
        exits.clear(); // Czyścimy stare wyjścia

        // Zawsze generujemy przynajmniej 1 wyjście
        String[] possibleExits = {"bottom", "top", "left", "right"};
        List<String> exitsToGenerate = new ArrayList<>(Arrays.asList(possibleExits));
        Collections.shuffle(exitsToGenerate);

        int exitCount = 1 + random.nextInt(3); // 1-3 wyjścia

        for(int i = 0; i < exitCount && !exitsToGenerate.isEmpty(); i++) {
            String exitPos = exitsToGenerate.remove(0);

            switch(exitPos) {
                case "bottom":
                    exits.add(new Rectangle(screenWidth/2-50, 0, 100, 20));
                    break;
                case "top":
                    exits.add(new Rectangle(screenWidth/2-50, screenHeight-20, 100, 20));
                    break;
                case "left":
                    exits.add(new Rectangle(0, screenHeight/2-50, 20, 100));
                    break;
                case "right":
                    exits.add(new Rectangle(screenWidth-20, screenHeight/2-50, 20, 100));
                    break;
            }
        }
    }

    public void draw(ShapeRenderer shapeRenderer){
        switch(type){
            case EMPTY:
                shapeRenderer.setColor(0.1f, 0.1f,0.1f,1);
                break;
            case PILLARS:
                shapeRenderer.setColor(0.2f,0.15f,0.1f,1);
                break;
            case TREASURE:
                shapeRenderer.setColor(0.1f,0.1f,0.3f,1);
                break;
            case ENEMY_CAMP:
                shapeRenderer.setColor(0.3f,0.1f,0.1f,1);
                break;
        }

        shapeRenderer.rect(0,0,screenWidth, screenHeight);

        // rysowanie scian
        shapeRenderer.setColor(0.5f,0.5f,0.5f,1);
        for(Rectangle wall : walls){
            shapeRenderer.rect(wall.x, wall.y, wall.width, wall.height);
        }

        // rysowanie obiektow specyficznych dla pokoju
        shapeRenderer.setColor(0.8f,0.8f,0.8f,1);
        for(Rectangle obstacle : obstacles){
            shapeRenderer.rect(obstacle.x, obstacle.y, obstacle.width, obstacle.height);
        }

        // skarby
        shapeRenderer.setColor(1,0.8f, 0, 1);
        for(Rectangle treasure : treasures){
            shapeRenderer.rect(treasure.x, treasure.y, treasure.width, treasure.height);
        }
    }

    public boolean collidesWith(Rectangle bounds){
        for(Rectangle wall : walls){
            if (wall.overlaps(bounds)){
                return true;
            }
        }
        for(Rectangle obstacle : obstacles){
            if(obstacle.overlaps(bounds)) return true;
        }
        return false;
    }

    public boolean collidesWithWalls(Rectangle bounds){
        for(Rectangle wall : walls){
            if (wall.overlaps (bounds)) return true;
        }
        for(Rectangle obstacle : obstacles){
            if (obstacle.overlaps(bounds)) return true;
        }
        return false;
    }

    public List<Rectangle> getExits(){
        return exits;
    }

    public int getHeight(){
        return (int) screenHeight;
    }
}

