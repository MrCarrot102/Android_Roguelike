package com.mateusz.roguelike;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class FOVRenderer{
    private ShapeRenderer shapeRenderer;
    private float fovAngle;
    private float viewDistance;
    private Player player;
    private Color fovColor;


    public FOVRenderer(ShapeRenderer shapeRenderer, float fovAngle, float viewDistance, Player player) {
        this.shapeRenderer = shapeRenderer;
        this.fovAngle = fovAngle;
        this.viewDistance = viewDistance;
        this.player = player;
        this.fovColor = new Color(1, 1, 1, 0.2f); // Bardziej przezroczysty
    }

    public void render(Vector2 playerPosition, float playerRotation, Room room) {
        if (player == null) return;

        // Włącz blending dla przezroczystości
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);


        shapeRenderer.setColor(1, 1, 1, 0.2f); // Biały z 20% przezroczystości

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
        for (Enemy enemy : room.getEnemies()) {
            if (isInFOV(playerPosition, playerRotation, enemy.getBounds())) {
                player.shoot(); // Automatyczne strzelanie do widocznych przeciwników
            }
        }

        shapeRenderer.end();

        // Włącz mieszanie kolorów dla przezroczystości
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }
    private boolean isInFOV(Vector2 playerPos, float playerRot, Circle enemyBounds) {
        Vector2 toEnemy = new Vector2(enemyBounds.x - playerPos.x, enemyBounds.y - playerPos.y);
        float angleToEnemy = toEnemy.angleDeg();
        float angleDiff = Math.abs(((angleToEnemy - playerRot) + 180 + 360) % 360 - 180);

        return angleDiff <= fovAngle / 2 && toEnemy.len() <= viewDistance;
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
    public boolean isPlayerVisibleTo(Enemy enemy, Player player) {
        float dx = player.getX() - enemy.getX();
        float dy = player.getY() - enemy.getY();
        float angleToPlayer = (float) Math.atan2(dy, dx);

        float enemyAngle = (float) Math.atan2(enemy.getDirection().y, enemy.getDirection().x);

        float deltaAngle = angleDifference(enemyAngle, angleToPlayer);
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        return Math.abs(deltaAngle) < enemy.getFovAngle() / 2f && distance < enemy.getViewDistance();
    }

    private float angleDifference(float a, float b) {
        float diff = a - b;
        while (diff < -Math.PI) diff += 2 * Math.PI;
        while (diff > Math.PI) diff -= 2 * Math.PI;
        return diff;
    }

    public void dispose(){}
}
