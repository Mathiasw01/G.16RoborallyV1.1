package com.g16.roborallyclient;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Connection {

    public String userID;

    public final GameSession gameSession = new GameSession();


    private static String playerToken;

    @JsonIgnore
    public static String getPlayerToken() {
        return playerToken;
    }

    public static void setPlayerToken(String playerToken) {
        Connection.playerToken = playerToken;
    }
    public Connection(){

    }


}
