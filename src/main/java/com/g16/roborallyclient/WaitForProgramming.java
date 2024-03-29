package com.g16.roborallyclient;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class WaitForProgramming implements Runnable {
    private volatile String[] cards;
    private final GameController gc;


    public WaitForProgramming(GameController gc, String[] cards){
        this.cards = cards;
        this.gc = gc;
    }

    @Override
    public void run() {
        do {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (ClientConsume.conn == null){
                break;
            }
            cards = ClientConsume.executeProgrammedCards(ClientConsume.conn.gameSession.gameID, ClientConsume.conn.userID);
        } while (cards[0].equals("500"));
        gc.executeCommandsFromServer(cards);
    }
}
