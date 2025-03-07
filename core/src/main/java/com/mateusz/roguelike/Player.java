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
    public void setPosition(float x, float y){
        bounds.setPosition(x, y);
    }
}
