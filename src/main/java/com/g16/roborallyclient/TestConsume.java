package com.g16.roborallyclient;

import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import org.springframework.web.client.RestTemplate;


public class TestConsume {
    String uri = "http://10.209.211.242:8080/game";
    public Connection joinGame() {
        uri = uri + "/join/1";
        RestTemplate restTemplate = new RestTemplate();
        Connection connection = restTemplate.getForObject(uri, Connection.class);
        GameSession gs = connection.gameSession;
        System.out.println("user ID is " + connection.userID + " game id is " + gs.gameID);
        return connection;
    }

    /*
    public Connection hostGame(){
        uri = uri + "/host/10";

        return Connection;
    }
     */


}
