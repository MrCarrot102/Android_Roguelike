package com.mateusz.roguelike;

import com.badlogic.gdx.Gdx;

import java.util.ArrayList;
import java.util.List;

public class RoomManager {
    private List<Room> rooms;
    private int currentRoomIndex;
    private float screenWidth, screenHeight;

    public RoomManager(float screenWidth, float screenHeight){
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        rooms = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            rooms.add(new Room(screenWidth, screenHeight));
        }
        currentRoomIndex = 0;
    }

    public void goToNextRoom(){
        currentRoomIndex = (currentRoomIndex + 1) % rooms.size();
        if(currentRoomIndex == 0){
            rooms.add(new Room(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        }
    }

    public Room getCurrentRoom(){
        return rooms.get(currentRoomIndex);
    }

    public void changeRoom(int exitIndex){
        currentRoomIndex = (currentRoomIndex + 1) % rooms.size();
    }

}
