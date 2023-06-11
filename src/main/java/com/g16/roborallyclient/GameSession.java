package com.g16.roborallyclient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;

public class GameSession {
    public String gameID;

    @JsonIgnore
    public GameController getController() {
        return controller;
    }

    public void setController(GameController controller) {
        this.controller = controller;
    }

    private GameController controller;
    public GameSession(){
    }
}
