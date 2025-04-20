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
    private String lastExitType;

    // Zmienne do przejść między pokojami
    private boolean isChangingRoom = false;
    private float roomChangeTimer = 0;
    private final float ROOM_CHANGE_TIME = 0.5f;

    

    @Override
    public void show() {
        shapeRenderer = new ShapeRenderer();
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        player = new Player(screenWidth/2, screenHeight/2, 30, 30);
        joystick = new VirtualJoystick(150, 150, 80, 45);
        roomManager = new RoomManager(screenWidth, screenHeight);
        fovRenderer = new FOVRenderer(shapeRenderer, 90, 300);

        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render(float delta) {
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

        // 1. Rysowanie pokoju (ścian i wyjść)
        currentRoom.draw(shapeRenderer);

        // 2. Rysowanie wyjść na wierzchu
        shapeRenderer.setColor(0, 1, 0, 1); // Zielone wyjścia
        for(Rectangle exit : currentRoom.getExits()) {
            shapeRenderer.rect(exit.x, exit.y, exit.width, exit.height);
        }

        // 3. Rysowanie gracza
        player.draw(shapeRenderer);
        shapeRenderer.end();

        // 4. Rysowanie FOV
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        //fovRenderer.render(player.getPosition(), player.getRotation());
        shapeRenderer.end();

        // Sprawdzanie wyjść
        checkRoomExits(currentRoom);
    }

    private void handleMovement(float delta, Room currentRoom) {
        if(joystick.isTouched()){
            Vector2 direction = joystick.getDirection();
            float speed = 150 * delta;

            float newX = player.getBounds().x + direction.x * speed;
            float newY = player.getBounds().y + direction.y * speed;

            Rectangle tempBounds = new Rectangle(newX, newY,
                player.getBounds().width, player.getBounds().height);

            if(!currentRoom.collidesWithWalls(tempBounds)){
                player.setPosition(newX, newY);
            }

            player.setRotation(direction.angleDeg());
        }
    }

    private void checkRoomExits(Room currentRoom) {
        Rectangle playerBounds = player.getBounds();

        for (Rectangle exit : currentRoom.getExits()){
            if (exit.overlaps(playerBounds)){
                String exitType = getExitType(exit, currentRoom);
                handleRoomTransition(exitType);
                return;
            }
        }
    }

    private String getExitType(Rectangle exit, Room room){
        if (exit.y == 0) return "bottom";
        if (exit.y == room.getHeight() - exit.height) return "top";
        if (exit.x == 0) return "left";
        return "right";
    }

    private void handleRoomTransition(String exitType){
        isChangingRoom = true;
        roomChangeTimer = 0;

        this.lastExitType = exitType;
    }

    private void startRoomTransition() {
        isChangingRoom = true;
        roomChangeTimer = 0;
    }

    private void renderRoomTransition(float delta) {
        roomChangeTimer += delta;
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

     float margin = 30f;
     switch(lastExitType){
         case "bottom":
             player.setPosition(screenWidth/2, screenHeight-margin);
             break;
         case "top":
             player.setPosition(screenWidth/2, margin);
             break;
         case "left":
             player.setPosition(screenWidth-margin, screenHeight/2);
             break;
         case "right":
             player.setPosition(margin, screenHeight/2);
             break;
     }

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
        player.setPosition(width/2, height/2);
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
