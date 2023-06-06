package com.g16.roborallyclient;

import com.fasterxml.jackson.core.type.TypeReference;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import dk.dtu.compute.se.pisd.roborally.controller.SaveLoadController;

import javax.swing.text.GapContent;
import java.util.List;


public class ClientConsume {
   //String uri = "http://10.209.211.242:8081";
   String uri = "http://localhost:8081";

   public static Connection conn;

    public String getLobbies() throws ResourceAccessException {
        String endPoint = uri + "/lobby";
       // ResponseEntity<List<GameSession>> response = getListResponseEntity(endPoint);
        RestTemplate restTemplate = new RestTemplate();
        String lobbies = restTemplate.getForObject(endPoint, String.class);

        return lobbies;
    }

    @NotNull
    private static ResponseEntity<List<GameSession>> getListResponseEntity(String endPoint) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("accept", "application/json");
        HttpEntity httpEntity = new HttpEntity<>(null, headers);
        ResponseEntity<List<GameSession>> response = restTemplate.exchange(endPoint, HttpMethod.GET, httpEntity,
                new ParameterizedTypeReference<>() {
                });
        return response;
    }

    public Connection joinGame(String gameID) throws RestClientException {
        String endPoint = uri + "/game/join/" + gameID;
        RestTemplate restTemplate = new RestTemplate();
        Connection connection = restTemplate.getForObject(endPoint, Connection.class);
        GameSession gs = connection.gameSession;
        conn = connection;

        return connection;
    }

    public Connection hostGame(String gameID) throws RestClientException{
        String endPoint = uri + "/game/host/" + gameID;
        RestTemplate restTemplate = new RestTemplate();
        Connection connection = restTemplate.getForObject(endPoint, Connection.class);
        GameSession gs = connection.gameSession;

        conn = connection;

        return connection;

    }

    public List<String> getMap (){
        String endPoint = uri + "/map/";
        RestTemplate restTemplate = new RestTemplate();

        List<String> maps = restTemplate.getForObject(endPoint, List.class);
        return maps;
    }

    public String startGame(String gameID, String mapName){
        String endPoint = uri + "/game/start/" + gameID + "?mapName=" + mapName + "&uuid=" + conn.userID;
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(endPoint, String.class);

        return response;
    }


    public GameController updateBoard(String gameID, String userID){
        String endPoint = uri + "/game/update/" + gameID + "?uuid=" +userID;
        RestTemplate restTemplate = new RestTemplate();
        String str = restTemplate.getForObject(endPoint, String.class);
        return SaveLoadController.deserializeString(str);
    }

    public String getPlayerToken(String gameID, String userID){
        String endPoint = uri + "/game/playertoken/" + gameID + "?uuid=" +userID;
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(endPoint, String.class);
    }

    public boolean isStarted(String gameID, String userID){
        String endPoint = uri + "/isstarted/" + gameID + "?uuid=" +userID;
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(endPoint, String.class);
        return response.equals("true");
    }

}
