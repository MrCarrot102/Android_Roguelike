package com.mateusz.roguelike;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;

public class FirstScreen implements Screen, InputProcessor {
    private ShapeRenderer shapeRenderer;
    private Player player;
    private VirtualJoystick joistick;

    @Override
    public void show(){
        shapeRenderer = new ShapeRenderer();
        // ustawianie pozycji poczatkowej i rozmiaru postaci
        player = new Player(100, 50, 50, 50);
        joistick = new VirtualJoystick(100,100,75,40);
        Gdx.input.setInputProcessor(this);
    }
    @Override
    public void render(float delta){
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        player.draw(shapeRenderer);
        joistick.draw(shapeRenderer);
        shapeRenderer.end();

        if(joistick.isTouched()){
            Vector2 direction = joistick.getDirection();
            float speed = 5;
            player.setPosition(player.getBounds().x + direction.x * speed, player.getBounds().y + direction.y * speed);
        }
    }
    @Override
    public boolean keyDown(int keycode){
        // predkosc poruszania sie postaci
        float speed = 5;
        switch (keycode) {
            case Keys.LEFT:
                player.setPosition(player.getBounds().x - speed, player.getBounds().y);
                break;
            case Keys.RIGHT:
                player.setPosition(player.getBounds().x + speed, player.getBounds().y);
                break;
            case Keys.UP:
                player.setPosition(player.getBounds().x, player.getBounds().y + speed);
                break;
            case Keys.DOWN:
                player.setPosition(player.getBounds().x, player.getBounds().y - speed);
                break;
        }
        return true;
    }
    @Override
    public boolean keyUp(int keycode){
        return false;
    }
    @Override
    public boolean keyTyped(char character){
        return false;
    }
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button){
        joistick.setTouched(true);
        joistick.update(screenX,Gdx.graphics.getHeight() - screenY);
        return true;
    }
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button){
        joistick.setTouched(false);
        return true;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer){
        if(joistick.isTouched()){
            joistick.update(screenX, Gdx.graphics.getHeight() - screenY);
        }
        return true;
    }
    @Override
    public boolean mouseMoved(int screenX, int screenY){
        return false;
    }
    @Override
    public boolean scrolled(float amountX, float amountY){
        return false;
    }


    @Override
    public void resize(int width, int height){
        float playerX = 50;
        float playerY = (height / 2) - (player.getBounds().height / 2);
        player.setPosition(playerX, playerY);
    }
    @Override
    public void pause(){}
    @Override
    public void resume(){}
    @Override
    public void hide(){}
    @Override
    public void dispose(){
        shapeRenderer.dispose();
    }




}
