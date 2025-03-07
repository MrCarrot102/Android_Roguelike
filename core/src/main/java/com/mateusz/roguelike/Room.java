package com.mateusz.roguelike;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class Room {
    private List<Rectangle> walls;
    private List<Rectangle> exits;
    private float screenWidth, screenHeight;

    public Room(float screenWidth, float screenHeight){
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        walls = new ArrayList<>();
        exits = new ArrayList<>();

        walls.add(new Rectangle(0, 0, screenWidth, 20));
        walls.add(new Rectangle(0, screenHeight - 20, screenWidth, 20));

        walls.add(new Rectangle(0, 0, 20, screenHeight));
        walls.add(new Rectangle(screenWidth - 20, 0, 20, screenHeight));


        exits.add(new Rectangle(screenWidth / 2 -50, 0, 100, 20));
        exits.add(new Rectangle(0, screenHeight / 2 - 50, 20, 100));
        exits.add(new Rectangle(screenWidth - 20, screenHeight / 2 - 50, 20, 100));



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
