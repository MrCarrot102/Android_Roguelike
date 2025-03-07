package com.mateusz.roguelike;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class VirtualJoystick{
    private Vector2 position;
    private float radius;
    private Vector2 knobPosition;
    private float knobRadius;
    private boolean isTouched;

    public VirtualJoystick(float x, float y, float radius, float knobRadius){
        this.position = new Vector2(x, y);
        this.radius = radius;
        this.knobPosition = new Vector2(x, y);
        this.knobRadius = knobRadius;
        this.isTouched = false;
    }

    public void draw(ShapeRenderer shapeRenderer){
        // podstawa
        shapeRenderer.setColor(Color.GRAY);
        shapeRenderer.circle(position.x, position.y, radius);
        // gałka
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.circle(knobPosition.x, knobPosition.y, knobRadius);
    }

    public void update(float touchX, float touchY){
        Vector2 touchPos = new Vector2(touchX, touchY);
        Vector2 direction = touchPos.sub(position);
        if(direction.len() > radius) {
            direction.nor().scl(radius);
        }
        knobPosition.set(position).add(direction);
    }

    public Vector2 getDirection(){
        return new Vector2(knobPosition.x - position.x , knobPosition.y - position.y).nor();
    }

    public void setTouched(boolean isTouched){
        this.isTouched = isTouched;
        if(!isTouched){
            // jeśli joystick nie jest dotykany jest na środku
            knobPosition.set(position);
        }
    }

    public boolean isTouched(){
        return isTouched;
    }
}

