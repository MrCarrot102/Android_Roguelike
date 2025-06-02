package com.mateusz.roguelike;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;

public class Medkit {
    private Circle bounds;
    private boolean collected = false;

    public Medkit(float x, float y) {
        bounds = new Circle(x, y, 10);
    }

    public void draw(ShapeRenderer renderer) {
        if (collected) return;

        renderer.setColor(Color.PINK);
        renderer.circle(bounds.x, bounds.y, bounds.radius);
    }

    public boolean checkCollected(Player player) {
        if (collected) return false;

        if (Intersector.overlaps(bounds, player.getBounds())) {
            collected = true;
            player.heal(20);
            return true;
        }

        return false;
    }

    public boolean isCollected() {
        return collected;
    }
}
