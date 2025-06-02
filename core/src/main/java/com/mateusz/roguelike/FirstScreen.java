package com.mateusz.roguelike;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Matrix4;
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
        shapeRenderer.setProjectionMatrix(new Matrix4()
            .setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        player = new Player(screenWidth/2, screenHeight/2, 30, 30);
        joystick = new VirtualJoystick(150, 150, 80, 45);
        roomManager = new RoomManager(screenWidth, screenHeight);
        fovRenderer = new FOVRenderer(shapeRenderer, 90, 300, player);

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

        // 1. Rysowanie pokoju i przeciwników
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        currentRoom.draw(shapeRenderer);

        // Rysowanie wyjść
        shapeRenderer.setColor(0, 1, 0, 1);
        for(Rectangle exit : currentRoom.getExits()) {
            shapeRenderer.rect(exit.x, exit.y, exit.width, exit.height);
        }

        // Rysowanie przeciwników
        currentRoom.drawEnemies(shapeRenderer);
        shapeRenderer.end();

        // 2. Rysowanie FOV (półprzezroczysty)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        fovRenderer.render(player.getPosition(), player.getRotation(), currentRoom);
        shapeRenderer.end();

        // 3. Rysowanie gracza i UI (na wierzchu)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        player.draw(shapeRenderer);
        player.drawBullets(shapeRenderer);
        joystick.draw(shapeRenderer);
        shapeRenderer.end();

        // Aktualizacje i kolizje
        currentRoom.updateEnemies(delta);
        player.update(delta, currentRoom);
        handleMovement(delta, currentRoom);
        checkRoomExits(currentRoom);

        if (player.checkEnemyCollision(currentRoom.getEnemies())) {
            Gdx.app.log("Collision", "Player hit by enemy!");
        }
    }
    private void handleMovement(float delta, Room currentRoom) {
        if(joystick.isTouched()){
            Vector2 direction = joystick.getDirection();
            float speed = 150 * delta;

            // Pobieramy aktualną pozycję środka gracza
            Vector2 playerPos = player.getPosition();

            // Obliczamy nową pozycję
            float newX = playerPos.x + direction.x * speed;
            float newY = playerPos.y + direction.y * speed;

            // Sprawdzamy kolizję używając metody wouldCollide
            if(!player.wouldCollide(newX, newY, currentRoom)){
                player.setPosition(newX, newY);
            }

            player.setRotation(direction.angleDeg());
        }
    }

    private void checkRoomExits(Room currentRoom) {
        Vector2 playerPos = player.getPosition();
        float playerSize = Math.max(player.getBounds().width, player.getBounds().height);

        // Sprawdzamy czy gracz dotyka krawędzi ekranu
        if (playerPos.x <= 0) {
            handleRoomTransition("left");
        } else if (playerPos.x >= screenWidth) {
            handleRoomTransition("right");
        } else if (playerPos.y <= 0) {
            handleRoomTransition("bottom");
        } else if (playerPos.y >= screenHeight) {
            handleRoomTransition("top");
        }
    }


    private void handleRoomTransition(String exitType){
        isChangingRoom = true;
        roomChangeTimer = 0;
        this.lastExitType = exitType;
        roomManager.setLastExitType(exitType);
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
        roomManager.goToNextRoom(lastExitType);
        Room currentRoom = roomManager.getCurrentRoom();
        float spawnMargin = 30f;
        float baseX = 0, baseY = 0;

        switch (lastExitType) {
            case "left":
                baseX = screenWidth - spawnMargin;
                baseY = screenHeight / 2;
                break;
            case "right":
                baseX = spawnMargin;
                baseY = screenHeight / 2;
                break;
            case "bottom":
                baseX = screenWidth / 2;
                baseY = screenHeight - spawnMargin;
                break;
            case "top":
                baseX = screenWidth / 2;
                baseY = spawnMargin;
                break;
        }
        if (!player.wouldCollide(baseX, baseY, currentRoom)) {
            player.setPosition(baseX, baseY);
        } else {
            boolean placed = false;
            for (float dx = -20; dx <= 20 && !placed; dx += 5) {
                for (float dy = -20; dy <= 20 && !placed; dy += 5) {
                    float tryX = baseX + dx;
                    float tryY = baseY + dy;
                    if (!player.wouldCollide(tryX, tryY, currentRoom)) {
                        player.setPosition(tryX, tryY);
                        placed = true;
                    }
                }
            }

            if (!placed) {
                // Ostateczność — ustaw mimo wszystko (lepiej być w ścianie niż zniknąć)
                player.setPosition(baseX, baseY);
            }
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
        joystick.update(screenX, Gdx.graphics.getHeight() - screenY);
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
