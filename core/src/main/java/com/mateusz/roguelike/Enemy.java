package com.mateusz.roguelike;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Enemy {
    private Circle bounds;
    private Color color;
    private float speed;
    private Vector2 direction;
    private float movementTimer;
    private float directionChangeTime;

    public Enemy(float x, float y, float radius){
        this.bounds = new Circle(x,y,radius);
        this.color = Color.RED;
        this.speed = 50f + (float)Math.random()*50f;
        this.direction = new Vector2(1,0).setToRandomDirection();
        this.directionChangeTime = 2f + (float)Math.random()*3f;
        this.movementTimer = 0;
    }
    public void update(float delta, Room room) {
        movementTimer += delta;

        // Zmiana kierunku po upływie czasu lub kolizji
        if (movementTimer >= directionChangeTime || checkWallCollision(room)) {
            direction.setToRandomDirection();
            movementTimer = 0;
        }

        // Poruszanie się
        Vector2 movement = direction.cpy().scl(speed * delta);
        bounds.x += movement.x;
        bounds.y += movement.y;

        // Ograniczenie do obszaru pokoju
        bounds.x = Math.max(bounds.radius, Math.min(bounds.x, room.getWidth() - bounds.radius));
        bounds.y = Math.max(bounds.radius, Math.min(bounds.y, room.getHeight() - bounds.radius));
    }

    private boolean checkWallCollision(Room room) {
        // Sprawdzanie kolizji ze ścianami
        Rectangle enemyRect = new Rectangle(
            bounds.x - bounds.radius,
            bounds.y - bounds.radius,
            bounds.radius * 2,
            bounds.radius * 2
        );
        return room.collidesWithWalls(enemyRect);
    }

    public void draw(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(color);
        shapeRenderer.circle(bounds.x, bounds.y, bounds.radius);
    }

    public Circle getBounds() {
        return bounds;
    }
}
