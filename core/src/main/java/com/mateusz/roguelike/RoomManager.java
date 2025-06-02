package com.mateusz.roguelike;

import com.badlogic.gdx.Gdx;

import java.util.ArrayList;
import java.util.List;

public class RoomManager {
    private List<Room> rooms;
    private int currentRoomIndex;
    private float screenWidth, screenHeight;
    private String lastExitType;
    private int currentLevel = 1;

    public void setLastExitType(String exitType) { this.lastExitType = exitType;}

    public RoomManager(float screenWidth, float screenHeight){
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        rooms = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            rooms.add( new Room(screenWidth, screenHeight, currentLevel));
        }
        currentRoomIndex = 0;
    }

    public void goToNextRoom(String lastExitType) {
        currentRoomIndex = (currentRoomIndex + 1) % rooms.size();
        currentLevel++;
        if (currentRoomIndex == 0) {
            Room newRoom =  new Room(screenWidth, screenHeight, currentLevel);
            // Zapewnij, że nowy pokój ma wyjście odpowiadające kierunkowi wejścia
            newRoom.forceExit(lastExitType);
            rooms.add(newRoom);
        }
    }



    public Room getCurrentRoom(){
        return rooms.get(currentRoomIndex);
    }

    public void changeRoom(int exitIndex){
        currentRoomIndex = (currentRoomIndex + 1) % rooms.size();
    }

}
