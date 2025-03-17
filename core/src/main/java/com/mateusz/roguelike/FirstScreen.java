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
    private VirtualJoystick joystick;
    private RoomManager roomManager;
    private FOVRenderer fovRenderer;

    @Override
    public void show(){
        shapeRenderer = new ShapeRenderer();
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();


        // ustawianie pozycji poczatkowej i rozmiaru postaci
        player = new Player(screenWidth / 2 - 25, screenHeight / 2 - 25, 50, 50);
        joystick = new VirtualJoystick(100,100,75,40);
        roomManager = new RoomManager(screenWidth, screenHeight);
        fovRenderer = new FOVRenderer(60, 300);
        Gdx.input.setInputProcessor(this);
    }
    @Override
    public void render(float delta){
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Room currentRoom = roomManager.getCurrentRoom();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        currentRoom.draw(shapeRenderer);
        player.draw(shapeRenderer);
        joystick.draw(shapeRenderer);
        shapeRenderer.end();

        // pole widzenia
        fovRenderer.render(player.getPosition(), player.getRotation(), currentRoom);

        if(joystick.isTouched()){
            Vector2 direction = joystick.getDirection();
            float speed = 5;
            float newX = player.getBounds().x + direction.x * speed;
            float newY = player.getBounds().y + direction.y * speed;
            player.setPosition(newX, newY, currentRoom);

            // obracanie postaci w kierunku ruchu
            player.setRotation(direction.angleDeg());
        }

        if (player.isAtExit(currentRoom)){
            roomManager.changeRoom(0);
            player.setPosition(Gdx.graphics.getWidth() / 2 - 25, Gdx.graphics.getHeight() / 2 - 25, roomManager.getCurrentRoom());
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    /*@Override
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
        }*/
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
        joystick.setTouched(true);
        joystick.update(screenX,Gdx.graphics.getHeight() - screenY);
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button){
        joystick.setTouched(false);
        return true;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer){
        if(joystick.isTouched()){
            joystick.update(screenX, Gdx.graphics.getHeight() - screenY);
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
        fovRenderer.dispose();
    }

}
