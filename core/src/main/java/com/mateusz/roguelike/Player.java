package com.mateusz.roguelike;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;


public class Player {
    private Rectangle bounds;
    private Color color;
    private float rotation;
    private List<Bullet> bullets;
    private float shootTimer;
    private final float SHOOT_INTERVAL = 0.3f;
    


    public Player(float x, float y, float width, float height){
        bounds = new Rectangle(x - width/2, y - height/2, width, height);
        color = Color.GREEN;
        rotation = 0;
        this.bullets = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            bullets.add(new Bullet());
        }
        this.shootTimer = 0;
    }

    public void draw(ShapeRenderer shapeRenderer){
        shapeRenderer.setColor(color);
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);

        // Rysowanie wskazówki kierunku (przód gracza)
        Vector2 center = getPosition();
        Vector2 front = new Vector2(1, 0).setAngleDeg(rotation).scl(20).add(center);

        shapeRenderer.setColor(Color.RED);
        shapeRenderer.line(center.x, center.y, front.x, front.y);
    }

    public Rectangle getBounds(){
        return bounds;
    }

    public void setPosition(float x, float y) {
        bounds.setPosition(x - bounds.width/2, y - bounds.height/2);  // Poprawka: środkowanie gracza
    }

    public boolean wouldCollide(float x, float y, Room room){
        Rectangle temp = new Rectangle(x - bounds.width/2, y - bounds.height/2, bounds.width, bounds.height);  // Poprawka: środkowanie
        return room.collidesWithWalls(temp);
    }

    public boolean checkEnemyCollision(List<Enemy> enemies) {
        Vector2 playerPos = getPosition();
        float playerRadius = Math.max(bounds.width, bounds.height) / 2;

        for (Enemy enemy : enemies) {
            if (enemy.getBounds().contains(playerPos) ||
                enemy.getBounds().overlaps(new Circle(playerPos.x, playerPos.y, playerRadius))) {
                return true;
            }
        }
        return false;
    }

    public void update(float delta, Room room) {
        shootTimer -= delta;

        // Aktualizacja pocisków
        for (Bullet bullet : bullets) {
            if (bullet.isActive()) {
                bullet.update(delta);
                checkBulletCollision(bullet, room);
            }
        }
    }

    public void shoot() {
        if(bullets == null){
            bullets = new ArrayList<>();
            for(int i = 0; i < 10; i++){
                bullets.add(new Bullet());
            }
        }
        if(shootTimer <= 0){
            for(Bullet bullet : bullets){
                if(!bullet.isActive()){
                    Vector2 pos = getPosition();
                    bullet.activate(pos.x,pos.y,rotation);
                    shootTimer = SHOOT_INTERVAL;
                    break;
                }
            }
        }
    }

    private void checkBulletCollision(Bullet bullet, Room room) {
        // Kolizja ze ścianami
        Rectangle bulletRect = new Rectangle(
            bullet.getBounds().x - bullet.getBounds().radius,
            bullet.getBounds().y - bullet.getBounds().radius,
            bullet.getBounds().radius * 2,
            bullet.getBounds().radius * 2
        );

        if (room.collidesWithWalls(bulletRect)) {
            bullet.deactivate();
            return;
        }

        // Kolizja z przeciwnikami
        for (Enemy enemy : room.getEnemies()) {
            if (bullet.getBounds().overlaps(enemy.getBounds())) {
                enemy.takeDamage(1); // Dodaj metodę takeDamage() w klasie Enemy
                bullet.deactivate();
                return;
            }
        }
    }

    public void drawBullets(ShapeRenderer shapeRenderer) {
        for (Bullet bullet : bullets) {
            bullet.draw(shapeRenderer);
        }
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
