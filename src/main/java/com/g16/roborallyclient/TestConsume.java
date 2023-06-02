package com.g16.roborallyclient;

import dk.dtu.compute.se.pisd.roborally.RoboRally;
import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import org.springframework.web.client.RestTemplate;


public class TestConsume {
   String uri = "http://10.209.211.242:8080/game";
   //String uri = "http://localhost:8081/game";
    public Connection joinGame(String gameID) {
        uri = uri + "/join/" + gameID;
        System.out.println(uri);
        RestTemplate restTemplate = new RestTemplate();
        Connection connection = restTemplate.getForObject(uri, Connection.class);
        GameSession gs = connection.gameSession;

        System.out.println("user ID is " + connection.userID + " game id is " + gs.gameID);
        return connection;
    }

}
