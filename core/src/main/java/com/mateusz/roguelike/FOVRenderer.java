package com.mateusz.roguelike;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class FOVRenderer{
    private ShapeRenderer shapeRenderer;
    private float fovAngle;
    private float viewDistance;

    public FOVRenderer(ShapeRenderer shapeRenderer, float fovAngle, float viewDistance){
        this.shapeRenderer = new ShapeRenderer();
        this.fovAngle = fovAngle;
        this.viewDistance =  viewDistance;
    }

    public void render(Vector2 playerPosition, float playerRotation, Room room){
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(1, 1, 1, 0.5f));

        Vector2 direction = new Vector2(1, 0).setAngleDeg(playerRotation);
        Vector2 leftRay = direction.cpy().rotateDeg(-fovAngle / 2);
        Vector2 rightRay = direction.cpy().rotateDeg(fovAngle / 2);

        Vector2 leftEnd = new Vector2(playerPosition).add(leftRay.scl(viewDistance));
        Vector2 rightEnd = new Vector2(playerPosition).add(rightRay.scl(viewDistance));

        // pole widzenia
        shapeRenderer.triangle(
            playerPosition.x, playerPosition.y,
            leftEnd.x, leftEnd.y,
            rightEnd.x, rightEnd.y
        );
        shapeRenderer.end();
    }
    public void dispose(){
        shapeRenderer.dispose();
    }
}
