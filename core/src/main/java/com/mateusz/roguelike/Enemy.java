package com.mateusz.roguelike;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Enemy {
    private Circle bounds;
    private Vector2 direction;
    private float speed;
    private float directionChangeTime;
    private float movementTimer;
    private int health = 3; // lub dowolna liczba punktów życia
    private boolean alive = true;
    private float fovAngle = (float) Math.toRadians(90); // kąt widzenia w radianach
    private float viewDistance = 250f;

    private float shootCooldown = 1.5f;
    private float timeSinceLastShot = 0f;

    private Array<Bullet> bullets;

    public Enemy(float x, float y, float radius) {
        this.bounds = new Circle(x, y, radius);
        this.direction = new Vector2(1, 0); // domyślnie w prawo
        this.speed = 100f;
        this.directionChangeTime = 2f;
        this.movementTimer = 0f;

        this.bullets = new Array<>();
    }

    public void update(float delta, Room room, Player player, FOVRenderer fovRenderer) {
        movementTimer += delta;
        timeSinceLastShot += delta;

        boolean playerVisible = fovRenderer != null && fovRenderer.isPlayerVisibleTo(this, player);

        if (playerVisible) {
            Vector2 toPlayer = new Vector2(player.getX() - bounds.x, player.getY() - bounds.y);
            if (toPlayer.len() > 0.1f) {
                toPlayer.nor();
                direction.set(toPlayer);
            }

            if (timeSinceLastShot >= shootCooldown) {
                shootAt(player);
                timeSinceLastShot = 0f;
            }
        } else {
            if (movementTimer >= directionChangeTime || checkWallCollision(room)) {
                direction.setToRandomDirection();
                movementTimer = 0f;
            }
        }

        Vector2 movement = direction.cpy().scl(speed * delta);
        bounds.x += movement.x;
        bounds.y += movement.y;

        bounds.x = Math.max(bounds.radius, Math.min(bounds.x, room.getWidth() - bounds.radius));
        bounds.y = Math.max(bounds.radius, Math.min(bounds.y, room.getHeight() - bounds.radius));

        // Aktualizacja pocisków
        for (int i = bullets.size - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            bullet.update(delta);
            if (!bullet.isActive()) {
                bullets.removeIndex(i);
            }
        }
    }

    private void shootAt(Player player) {
        Vector2 toPlayer = new Vector2(player.getX() - bounds.x, player.getY() - bounds.y);
        float angle = toPlayer.angleDeg();

        Bullet bullet = new Bullet();
        bullet.activate(bounds.x, bounds.y, angle);
        //bullet.setColor(Color.RED); // czerwony dla wroga

        bullets.add(bullet);
    }

    public void draw(ShapeRenderer renderer) {
        renderer.setColor(Color.RED);
        renderer.circle(bounds.x, bounds.y, bounds.radius);

        for (Bullet bullet : bullets) {
            bullet.draw(renderer);
        }
    }

    public Circle getBounds() {
        return bounds;
    }

    public float getX() {
        return bounds.x;
    }

    public float getY() {
        return bounds.y;
    }

    public Vector2 getDirection() {
        return direction;
    }

    public float getFovAngle() {
        return fovAngle;
    }

    public float getViewDistance() {
        return viewDistance;
    }

    public Array<Bullet> getBullets() {
        return bullets;
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            alive = false;
            // opcjonalnie: wyłącz wroga, animacja śmierci itd.
        }
    }

    public boolean isAlive() {
        return alive;
    }


    private boolean checkWallCollision(Room room) {
        return bounds.x - bounds.radius <= 0 || bounds.x + bounds.radius >= room.getWidth()
            || bounds.y - bounds.radius <= 0 || bounds.y + bounds.radius >= room.getHeight();
    }
}
