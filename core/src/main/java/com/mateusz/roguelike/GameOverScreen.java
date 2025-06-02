package com.mateusz.roguelike;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameOverScreen implements Screen {
    private SpriteBatch batch;
    private BitmapFont font;
    private int finalScore;
    private float screenWidth, screenHeight;
    private GlyphLayout layout;

    public GameOverScreen(int score) {
        this.finalScore = score;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(3f);  // większy font
        layout = new GlyphLayout();

        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        String title = "Game Over";
        String scoreText = "Your Score: " + finalScore;
        String prompt = "Tap to restart";

        layout.setText(font, title);
        font.draw(batch, title,
            (screenWidth - layout.width) / 2,
            screenHeight / 2 + 80);

        layout.setText(font, scoreText);
        font.draw(batch, scoreText,
            (screenWidth - layout.width) / 2,
            screenHeight / 2);

        layout.setText(font, prompt);
        font.draw(batch, prompt,
            (screenWidth - layout.width) / 2,
            screenHeight / 2 - 80);

        batch.end();

        if (Gdx.input.justTouched()) {
            ((com.badlogic.gdx.Game) Gdx.app.getApplicationListener()).setScreen(new FirstScreen());
        }
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}
