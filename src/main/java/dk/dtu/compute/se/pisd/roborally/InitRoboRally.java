/*
package dk.dtu.compute.se.pisd.roborally;

import com.g16.roborallyclient.ClientConsume;
import com.g16.roborallyclient.Connection;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.json.JSONObject;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class InitRoboRally extends Application {

    private static final int MIN_APP_WIDTH = 600;

    public void init() throws Exception {
        super.init();
    }

    public void start(Stage s) throws Exception {

        // create the primary scene with a menu bar and a pane for
        // the board view (which initially is empty); it will be filled
        // when the user creates a new game or loads a game
        s.setTitle("InitRoboRally");
        Button S = new Button("Singleplayer");
        Button M = new Button("Multiplayer");
        TilePane r = new TilePane();
        Label l = new Label("Choose a gamemode");

        EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e)
            {
                l.setText("No friends mode selected");
                new RoboRally(new String[]{"offline"}, s);
            }
        };
        EventHandler<ActionEvent> event1 = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e)
            {
                l.setText("Imaginary friend mode selected");
                //startMultiplayer();
            }
        };

        S.setOnAction(event);
        M.setOnAction(event1);

        r.getChildren().add(S);
        r.getChildren().add(M);
        r.getChildren().add(l);

        VBox vbox = new VBox(r);
        vbox.setMinWidth(MIN_APP_WIDTH);
        Scene primaryScene = new Scene(vbox);
        s.setScene(primaryScene);
        s.setResizable(false);
        s.sizeToScene();
        s.show();
    }


    public static void main(String[] args) {
        launch(args);
        //localOrOnline(clientConsume);
    }



    private static void startMultiplayer() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("J join or H host");
        String input = scanner.nextLine();

        if (input.equals("J") || input.equals("j")) {
            join(scanner);
        } else if (input.equals("H") || input.equals("h")) {
            host(scanner);
        } else {
            System.out.println("Not a command");
            startMultiplayer();
        }
    }
    private static void host(Scanner scanner) {
        System.out.println("Input game ID");
        String gameID = scanner.nextLine();
        String map = "testbeepboop";
        try {
            ClientConsume.hostGame(gameID);

            List<String> maps = ClientConsume.getMap();
            System.out.println("Choose map");
            for (String str: maps) {
                System.out.println(str);
            }
            map = chooseMap(scanner, maps);
        } catch (Exception e){
            System.out.println(e);
            if (e instanceof ResourceAccessException){
                System.out.println("The server is down");
            }
            if (e instanceof RestClientException){
                System.out.println("This lobby already exist");
            }
            startMultiplayer();
        }



        startGame(scanner, gameID, map);

    }

    private static void startGame(Scanner scanner, String gameID, String map) {
        System.out.println("press s to start");
        String keyPress = scanner.nextLine();
        if (keyPress.equals("s") || keyPress.equals("S")) {
            System.out.println(ClientConsume.startGame(gameID, map));
            if (ClientConsume.startGame(gameID, map).equals("100")){

                GameController gm = ClientConsume.updateBoard(gameID, ClientConsume.conn.userID);

                ClientConsume.conn.gameSession.setController(gm);
                String playerToken = ClientConsume.getPlayerToken(gameID, ClientConsume.conn.userID);
                Connection.setPlayerToken(playerToken);
                RoboRally.main(new String[]{"online"});
            } else if (ClientConsume.startGame(gameID, map).equals("200")){
                System.out.println("You are not authenticated!");
                startGame(scanner, gameID, map);
            } else if (ClientConsume.startGame(gameID, map).equals("300")){
                System.out.println("You need to be at least 2 players!");
                startGame(scanner, gameID, map);
            }
        }
    }

    private static void join(Scanner scanner) {
        System.out.println("Active lobbies");
        JSONObject jsonObject = new JSONObject(ClientConsume.getLobbies().trim());
        try {
            jsonObject = new JSONObject(ClientConsume.getLobbies().trim());
        } catch (ResourceAccessException e) {
            System.out.println("The server is down");
        }

        Iterator<String> keys = jsonObject.keys();

        if (!keys.hasNext()){
            System.out.println("No active lobbies");
        } else {
            while(keys.hasNext()) {
                String key = keys.next();
                System.out.print("Lobby ID: " + key + ": Player count: ");
                System.out.println(jsonObject.get(key));
            }
        }

        System.out.println("Input game ID");


        String gameID = scanner.nextLine();
        try {
            if ((int)jsonObject.get(gameID) >= 6 ){
                System.out.println("The lobby is full");
                startMultiplayer();
            }
            if (ClientConsume.isStarted(gameID)) {
                System.out.println("You can't join this because the game has started");
                startMultiplayer();
            } else {
                ClientConsume.joinGame(gameID);
            }
        } catch (RestClientException e) {
            System.out.println("Lobby does not exist");
            startMultiplayer();
        }
        while (!ClientConsume.isStarted(gameID)){
            System.out.println("Waiting for game to start");
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e){
                System.out.println("Sleep was interrupted");
            }
        }
        GameController gm = ClientConsume.updateBoard(gameID, ClientConsume.conn.userID);

        ClientConsume.conn.gameSession.setController(gm);
        String playerToken = ClientConsume.getPlayerToken(gameID, ClientConsume.conn.userID);
        Connection.setPlayerToken(playerToken);
        RoboRally.main(new String[]{"online"});


    }

    private static String chooseMap(Scanner scanner, List<String> maps) {
        String chosenMap = scanner.nextLine();
        String finalMap = null;
        Optional<String> result = maps.stream().filter(map -> map.equals(chosenMap)).findFirst();
        if (result.isPresent()){
            finalMap = result.get();
        } else {
            System.out.println("This map does not exist, try again");
            return chooseMap(scanner, maps);
        }
        return finalMap;
    }


}

 */
