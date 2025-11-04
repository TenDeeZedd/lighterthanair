package net.deezedd.lighterthanair.client;

public class ClientWindData {
    private static int currentDirection = 0;
    private static int currentStrength = 0;

    public static int getCurrentDirection() {
        return currentDirection;
    }

    public static void setCurrentDirection(int direction) {
        ClientWindData.currentDirection = direction;
    }

    public static int getCurrentStrength() {
        return currentStrength;
    }

    public static void setCurrentStrength(int strength) {
        ClientWindData.currentStrength = strength;
    }
}