package com.mateusz.roguelike;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;


public class Player {
    private Rectangle bounds;
    private Color color;
    private float rotation;

    public Player(float x, float y, float width, float height){
        bounds = new Rectangle(x, y, width, height);
        color = Color.GREEN;
        rotation = 0;
    }

    public void draw(ShapeRenderer shapeRenderer){
        shapeRenderer.setColor(color);
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public Rectangle getBounds(){
        return bounds;
    }

    public void setPosition(float x, float y) {
        bounds.setPosition(x, y);
    }
    public boolean wouldCollide(float x, float y, Room room){
        Rectangle temp = new Rectangle(x, y, bounds.width, bounds.height);
        return room.collidesWithWalls(temp);
    }


    public boolean isAtExit(Room room){
        for (Rectangle exit : room.getExits()){
            if(bounds.overlaps(exit)){
                return true;
            }
        }
        return false;
    }

    public float getRotation() {
        return rotation;
    }
    public void setRotation(float rotation){
        this.rotation = rotation;
    }
    public Vector2 getPosition(){
        return new Vector2(bounds.x + bounds.width / 2, bounds.y + bounds.height / 2);
    }
}
