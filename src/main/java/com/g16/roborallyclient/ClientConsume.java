package com.g16.roborallyclient;

import com.fasterxml.jackson.core.type.TypeReference;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.CommandCard;
import dk.dtu.compute.se.pisd.roborally.model.CommandCardField;
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
   static String uri = "http://10.209.211.242:8081";
   //static String uri = "http://10.209.211.220:8081";
    //String uri = "http://localhost:8081";


   public static Connection conn;

    public static String getLobbies() throws ResourceAccessException {
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

    public static Connection joinGame(String gameID) throws RestClientException {
        String endPoint = uri + "/game/join/" + gameID;
        RestTemplate restTemplate = new RestTemplate();
        Connection connection = restTemplate.getForObject(endPoint, Connection.class);
        GameSession gs = connection.gameSession;
        conn = connection;
        System.out.println("UUID: " + connection.userID);
        return connection;
    }

    public static Connection hostGame(String gameID) throws RestClientException{
        String endPoint = uri + "/game/host/" + gameID;
        RestTemplate restTemplate = new RestTemplate();
        Connection connection = restTemplate.getForObject(endPoint, Connection.class);
        GameSession gs = connection.gameSession;

        conn = connection;
        System.out.println("UUID: " + connection.userID);

        return connection;

    }

    public static List<String> getMap (){
        String endPoint = uri + "/map/";
        RestTemplate restTemplate = new RestTemplate();

        List<String> maps = restTemplate.getForObject(endPoint, List.class);
        return maps;
    }

    public static String startGame(String gameID, String mapName){
        String endPoint = uri + "/game/start/" + gameID + "?mapName=" + mapName + "&uuid=" + conn.userID;
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(endPoint, String.class);

        return response;
    }


    public static GameController updateBoard(String gameID, String userID){
        String endPoint = uri + "/game/update/" + gameID + "?uuid=" +userID;
        RestTemplate restTemplate = new RestTemplate();
        String str = restTemplate.getForObject(endPoint, String.class);
        return SaveLoadController.deserializeString(str);
    }

    public static String getPlayerToken(String gameID, String userID){
        String endPoint = uri + "/game/playertoken/" + gameID + "?uuid=" +userID;
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(endPoint, String.class);
    }

    public static boolean isStarted(String gameID){
        String endPoint = uri + "/game/isstarted/" + gameID;
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(endPoint, String.class);
        return response.equals("true");
    }

    public static void sendProgram(String gameID, String userID, CommandCardField[] cards){
        String endPoint = uri + "/game/program/" + gameID + "?uuid=" +userID;
        RestTemplate restTemplate = new RestTemplate();
        String[] program = new String[5];
        int i = 0;
        for (CommandCardField commandCardField : cards){
            if (commandCardField.getCard() != null)
                program[i] = commandCardField.getCard().command.displayName;
            else
                program[i] = "null";
            i++;
        }
        restTemplate.postForObject(endPoint, program, String.class);
    }

    public static String[] executeProgrammedCards(String gameID, String userID){
        String endPoint = uri + "/game/getCards/" + gameID + "?uuid=" +userID;
        RestTemplate restTemplate = new RestTemplate();
        String[] response = restTemplate.getForObject(endPoint, String[].class);
        return response;
    }
}
