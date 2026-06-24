package com.smartcampus.library.soap;

public class RoomAlreadyBookedFault extends Exception {

    public RoomAlreadyBookedFault(String message) {
        super(message);
    }
}
