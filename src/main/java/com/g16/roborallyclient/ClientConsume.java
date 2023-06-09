package com.g16.roborallyclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Command;
import dk.dtu.compute.se.pisd.roborally.model.CommandCard;
import dk.dtu.compute.se.pisd.roborally.model.CommandCardField;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import dk.dtu.compute.se.pisd.roborally.controller.SaveLoadController;
import javax.swing.text.GapContent;
import java.util.*;


public class ClientConsume {
   //static String uri = "http://10.209.211.242:8081";
   //static String uri = "http://10.209.211.220:8081";
    static String uri = "http://localhost:8081";


   public static Connection conn;

    public static String getLobbies() throws ResourceAccessException {
        String endPoint = uri + "/lobby";
       // ResponseEntity<List<GameSession>> response = getListResponseEntity(endPoint);
        RestTemplate restTemplate = new RestTemplate();
        String lobbies = restTemplate.getForObject(endPoint, String.class);

        return lobbies;
    }

    @NotNull
    private static ResponseEntity<List<Interactive>> getListResponseEntity(String endPoint) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("accept", "application/json");
        HttpEntity httpEntity = new HttpEntity<>(null, headers);
        ResponseEntity<List<Interactive>> response = restTemplate.exchange(endPoint, HttpMethod.GET, httpEntity,
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

        return restTemplate.getForObject(endPoint, String.class);
    }

    public static String startGameFromSave(String gameID, String saveName){
        String endPoint = uri + "/storage/load/" + saveName + "?uuid=" + conn.userID + "&gameID=" + gameID;
        RestTemplate restTemplate = new RestTemplate();

        return restTemplate.getForObject(endPoint, String.class);
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

    public static void sendInteractiveCommand(String gameID, String uuid, String comm){
        String endPoint = uri + "/game/interactive/" + gameID + "?uuid=" +uuid;
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.postForObject(endPoint,comm,String.class);
    }

    public static List<Interactive> getInteractive(String gameID, String uuid) throws JsonProcessingException {
        String endPoint = uri + "/game/interactive/" + gameID + "?uuid=" +uuid;
        /*
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(endPoint, String.class);
        System.out.println(response);
        ObjectMapper mapper = new ObjectMapper();

         */

        List<Interactive> interactives = getListResponseEntity(endPoint).getBody();


       return interactives;
    }

    public static void saveGame(String saveName, String json) throws ResourceAccessException {
        String endPoint = uri + "/storage/save/"+saveName + "?gameID="+ClientConsume.conn.gameSession.gameID;
        RestTemplate restTemplate = new RestTemplate();
        String resp = restTemplate.postForObject(endPoint, json, String.class);


        if(Objects.equals(resp, "100")){
            System.out.println("Saved game to server");
        } else {
            System.out.println("Couldn't save to server");
        }

    }


    public static void saveCardState(List<Player> players){

        Optional<Player> playerReq = players.stream().filter(p -> p.getName().equals(Connection.getPlayerToken())).findFirst();
        if(playerReq.isEmpty()){
            System.out.println("Error cannot save to server");
            return;
        }
        Player player = playerReq.get();
        String endPoint = uri + "/storage/saveHand/"+conn.gameSession.gameID+"?uuid="+conn.userID;

        CommandCardField[] cardHand = player.getCards();
        CommandCardField[] program = player.getProgram();

        String[] simpleCardHand = new String[cardHand.length];
        String[] simpleProgram = new String[program.length];

        for(int i = 0; i < cardHand.length; i++){
            if(cardHand[i].getCard() == null){
                simpleCardHand[i] = "null";
            } else {
                simpleCardHand[i] = cardHand[i].getCard().command.displayName;
            }
        }

        for(int i = 0; i < program.length; i++){
            if(program[i].getCard() == null){
                simpleProgram[i] = "null";
            } else {
                simpleProgram[i] = program[i].getCard().command.displayName;
            }
        }

        RestTemplate restTemplate = new RestTemplate();
        String resp = restTemplate.postForObject(endPoint, new String[][]{simpleCardHand,simpleProgram}, String.class);

        if(Objects.equals(resp, "100")){
            System.out.println("Saved card state to server");
        } else {
            System.out.println("Couldn't save card state to server");
        }
    }
}
