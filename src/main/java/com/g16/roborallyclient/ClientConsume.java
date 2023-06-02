package com.g16.roborallyclient;

import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;


public class ClientConsume {
   String uri = "http://10.209.211.242:8081";
   //String uri = "http://localhost:8081/game";
    public Connection joinGame(String gameID) {
        String endPoint = uri + "/game/join/" + gameID;
        RestTemplate restTemplate = new RestTemplate();
        Connection connection = restTemplate.getForObject(endPoint, Connection.class);
        GameSession gs = connection.gameSession;

        return connection;
    }

    public Connection hostGame(String gameID) throws RestClientException{
        String endPoint = uri + "/game/host/" + gameID;
        RestTemplate restTemplate = new RestTemplate();
        Connection connection = restTemplate.getForObject(endPoint, Connection.class);
        GameSession gs = connection.gameSession;

        return connection;

    }

    public List<String> getMap (){
        String endPoint = uri + "/map/";
        RestTemplate restTemplate = new RestTemplate();

        List<String> maps = restTemplate.getForObject(endPoint, List.class);
        return maps;
    }

}
