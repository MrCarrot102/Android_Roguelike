package com.mateusz.roguelike;

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
        rooms.add(new Room(screenWidth, screenHeight));
        rooms.add(new Room(screenWidth, screenHeight));
        rooms.add(new Room(screenWidth, screenHeight));

        currentRoomIndex = 0;
    }

    public Room getCurrentRoom(){
        return rooms.get(currentRoomIndex);
    }

    public void changeRoom(int exitIndex){
        currentRoomIndex = (currentRoomIndex + 1) % rooms.size();
    }

}
