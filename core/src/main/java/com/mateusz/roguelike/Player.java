package com.mateusz.roguelike;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;


public class Player {
    private Rectangle bounds;
    private Color color;

    public Player(float x, float y, float width, float height){
        bounds = new Rectangle(x, y, width, height);
        color = Color.GREEN;
    }

    public void draw(ShapeRenderer shapeRenderer){
        shapeRenderer.setColor(color);
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public Rectangle getBounds(){
        return bounds;
    }

    public void setPosition(float x, float y, Room room){
        Rectangle newBounds = new Rectangle(x, y, bounds.width, bounds.height);
        if (!room.collidesWith(newBounds)){
            bounds.setPosition(x, y);
        }
    }

    public boolean isAtExit(Room room){
        for (Rectangle exit : room.getExits()){
            if(bounds.overlaps(exit)){
                return true;
            }
        }
        return false;
    }
}
