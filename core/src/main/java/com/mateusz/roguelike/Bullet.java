package com.mateusz.roguelike;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;

public class Bullet {
    private Circle bounds;
    private Vector2 direction;
    private float speed;
    private boolean active;
    private Color color;

    public Bullet() {
        this.bounds = new Circle(0, 0, 5);
        this.direction = new Vector2();
        this.speed = 300f;
        this.active = false;
        this.color = Color.YELLOW;
    }

    public void activate(float x, float y, float angleDeg) {
        bounds.x = x;
        bounds.y = y;
        direction.set(1, 0).setAngleDeg(angleDeg);
        active = true;
    }

    public void update(float delta) {
        if (!active) return;

        bounds.x += direction.x * speed * delta;
        bounds.y += direction.y * speed * delta;
    }

    public void draw(ShapeRenderer shapeRenderer) {
        if (!active) return;

        shapeRenderer.setColor(color);
        shapeRenderer.circle(bounds.x, bounds.y, bounds.radius);
    }

    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        active = false;
    }

    public Circle getBounds() {
        return bounds;
    }
}
