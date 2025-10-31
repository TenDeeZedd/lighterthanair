package net.deezedd.lighterthanair.client;

public class ClientWindData {
    private static int clientWindDirection = 0; // Výchozí směr

    public static void setWindDirection(int direction) {
        clientWindDirection = direction;
    }

    public static int getWindDirection() {
        return clientWindDirection;
    }
}