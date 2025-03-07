package com.mateusz.roguelike;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class Room {
    private List<Rectangle> walls;

    public Room(){
        walls = new ArrayList<>();

        walls.add(new Rectangle(0, 0, 800,20));
        walls.add(new Rectangle(0, 580, 800,20));
        walls.add(new Rectangle(0, 0, 20,600));
        walls.add(new Rectangle(780, 0, 20,600));
    }

    public void draw(ShapeRenderer shapeRenderer){
        shapeRenderer.setColor(Color.BROWN);
        for(Rectangle wall:walls){
            shapeRenderer.rect(wall.x, wall.y, wall.width, wall.height);
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
}
