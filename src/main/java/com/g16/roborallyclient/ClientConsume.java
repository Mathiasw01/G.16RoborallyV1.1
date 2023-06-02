package com.g16.roborallyclient;

import com.fasterxml.jackson.core.type.TypeReference;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;


public class ClientConsume {
   //String uri = "http://10.209.211.242:8081";
   String uri = "http://localhost:8081";

   Connection conn;

    public List<GameSession> getLobbies(){
        String endPoint = uri + "/lobby";
        ResponseEntity<List<GameSession>> response = getListResponseEntity(endPoint);

        return response.getBody();
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
        return restTemplate.getForObject(endPoint, String.class);
    }

}
