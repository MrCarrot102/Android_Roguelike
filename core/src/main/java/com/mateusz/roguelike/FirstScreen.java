package com.mateusz.roguelike;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class FirstScreen implements Screen, InputProcessor {
    private ShapeRenderer shapeRenderer;
    private Player player;
    private VirtualJoystick joystick;
    private RoomManager roomManager;
    private FOVRenderer fovRenderer;
    private float screenWidth, screenHeight;

    // Zmienne do przejść między pokojami
    private boolean isChangingRoom = false;
    private float roomChangeTimer = 0;
    private final float ROOM_CHANGE_TIME = 0.5f; // Czas trwania animacji przejścia

    @Override
    public void show() {
        shapeRenderer = new ShapeRenderer();
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        player = new Player(screenWidth/2, screenHeight/2, 30, 30);
        joystick = new VirtualJoystick(150, 150, 80, 45);
        roomManager = new RoomManager(screenWidth, screenHeight);
        fovRenderer = new FOVRenderer(shapeRenderer, 90, 300); // 90 stopni FOV, zasięg 300px

        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render(float delta) {
        // Czyszczenie ekranu
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Room currentRoom = roomManager.getCurrentRoom();

        if (isChangingRoom) {
            renderRoomTransition(delta);
            return;
        }

        // Sterowanie
        handleMovement(delta, currentRoom);

        // Rysowanie
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // 1. Najpierw rysujemy ściany pokoju
        currentRoom.draw(shapeRenderer);

        // 2. Potem gracza
        player.draw(shapeRenderer);

        shapeRenderer.end();

        // 3. Na końcu FOV (oddzielny begin/end dla przezroczystości)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        //fovRenderer.render(player.getPosition(), player.getRotation());
        shapeRenderer.end();

        // Sprawdzanie wyjść
        checkRoomExits(currentRoom);
    }

    private void handleMovement(float delta, Room currentRoom) {
        if (joystick.isTouched()) {
            Vector2 direction = joystick.getDirection();
            float speed = 150 * delta; // Prędkość zależna od czasu

            float newX = player.getBounds().x + direction.x * speed;
            float newY = player.getBounds().y + direction.y * speed;

            player.setPosition(newX, newY, currentRoom);
            player.setRotation(direction.angleDeg());
        }
    }

    private void checkRoomExits(Room currentRoom) {
        for (Rectangle exit : currentRoom.getExits()) {
            if (player.getBounds().overlaps(exit)) {
                startRoomTransition();
                break;
            }
        }
    }

    private void startRoomTransition() {
        isChangingRoom = true;
        roomChangeTimer = 0;
    }

    private void renderRoomTransition(float delta) {
        roomChangeTimer += delta;

        // Efekt zaciemnienia
        float alpha = Math.min(roomChangeTimer / ROOM_CHANGE_TIME, 1);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, alpha);
        shapeRenderer.rect(0, 0, screenWidth, screenHeight);
        shapeRenderer.end();

        if (roomChangeTimer >= ROOM_CHANGE_TIME) {
            completeRoomTransition();
        }
    }

    private void completeRoomTransition() {
        roomManager.goToNextRoom();

        // Ustaw gracza w środku nowego pokoju
        player.setPosition(screenWidth/2, screenHeight/2, roomManager.getCurrentRoom());
        player.setRotation(0); // Resetuj kierunek patrzenia

        isChangingRoom = false;
    }



    @Override
    public boolean keyDown(int keycode) {
        return false;
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
    public void resize(int width, int height) {
        screenWidth = width;
        screenHeight = height;
        player.setPosition(width/2, height/2, roomManager.getCurrentRoom());
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
