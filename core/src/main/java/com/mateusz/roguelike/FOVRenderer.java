package com.mateusz.roguelike;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class FOVRenderer{
    private ShapeRenderer shapeRenderer;
    private float fovAngle;
    private float viewDistance;
    private Player player;

    public FOVRenderer(ShapeRenderer shapeRenderer, float fovAngle, float viewDistance, Player player){
        this.shapeRenderer = shapeRenderer;
        this.fovAngle = fovAngle;
        this.viewDistance = viewDistance;
        this.player = player;
    }

    public void render(Vector2 playerPosition, float playerRotation, Room room) {
        if(player == null) return;
        shapeRenderer.setColor(new Color(1, 1, 1, 0.5f));

        // Rysowanie FOV
        Vector2 direction = new Vector2(1, 0).setAngleDeg(playerRotation);
        Vector2 leftRay = direction.cpy().rotateDeg(-fovAngle / 2);
        Vector2 rightRay = direction.cpy().rotateDeg(fovAngle / 2);

        Vector2 leftEnd = new Vector2(playerPosition).add(leftRay.cpy().scl(viewDistance));
        Vector2 rightEnd = new Vector2(playerPosition).add(rightRay.cpy().scl(viewDistance));

        shapeRenderer.triangle(
            playerPosition.x, playerPosition.y,
            leftEnd.x, leftEnd.y,
            rightEnd.x, rightEnd.y
        );

        // Sprawdzanie przeciwników w FOV
        for (Enemy enemy : room.getEnemies()) {
            if (isInFOV(playerPosition, playerRotation, enemy.getBounds(), room)) {
                player.shoot(); // Strzelaj tylko do widocznych przeciwników
            }
        }
    }
    private boolean isInFOV(Vector2 playerPos, float playerRot, Circle enemyBounds, Room room) {
        // 1. Sprawdź odległość i kąt
        Vector2 toEnemy = new Vector2(enemyBounds.x - playerPos.x, enemyBounds.y - playerPos.y);
        float distance = toEnemy.len();
        float angleToEnemy = toEnemy.angleDeg();
        float angleDiff = Math.abs(((angleToEnemy - playerRot) + 180 + 360) % 360 - 180);

        if (angleDiff > fovAngle / 2 || distance > viewDistance) {
            return false;
        }

        // 2. Sprawdź linie widzenia (raycasting)
        Vector2 rayEnd = new Vector2(enemyBounds.x, enemyBounds.y);
        return !hasObstacleBetween(playerPos, rayEnd, room);
    }

    private boolean hasObstacleBetween(Vector2 start, Vector2 end, Room room) {
        // Prosta implementacja raycastingu
        int steps = 20;
        Vector2 step = new Vector2(end).sub(start).scl(1f/steps);

        for (int i = 0; i < steps; i++) {
            Vector2 point = new Vector2(start).add(step.cpy().scl(i));
            Rectangle testArea = new Rectangle(point.x - 5, point.y - 5, 10, 10);
            if (room.collidesWithWalls(testArea)) {
                return true;
            }
        }
        return false;
    }

    public void dispose(){
        // No need to dispose shapeRenderer here, it's managed by FirstScreen
    }
}
