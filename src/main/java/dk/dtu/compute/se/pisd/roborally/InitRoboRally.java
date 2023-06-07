package dk.dtu.compute.se.pisd.roborally;

import com.g16.roborallyclient.ClientConsume;
import com.g16.roborallyclient.Connection;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import javafx.application.Application;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
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

    private static final int MIN_APP_WIDTH = 300;

    public void init() throws Exception {
        super.init();
    }

    static String saveGame = null;

    public void start(Stage s) throws Exception {

        // create the primary scene with a menu bar and a pane for
        // the board view (which initially is empty); it will be filled
        // when the user creates a new game or loads a game
        s.setTitle("InitRoboRally");
        Button S = new Button("Singleplayer");

        Button M = new Button("Multiplayer");

        Label l = new Label("Choose a gamemode");

        TilePane r = new TilePane(Orientation.VERTICAL);
        r.setAlignment(Pos.CENTER);
        r.setVgap(10);
        EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent ev)
            {
                new RoboRally(new String[]{"offline"}, s);
            }
        };
        EventHandler<ActionEvent> event1 = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent ev)
            {
                startMultiplayer(s);
            }
        };

        S.setOnAction(event);
        M.setOnAction(event1);

        r.getChildren().addAll(l, S, M);

        vbox(s, r);
    }

    public static void vbox(Stage s, TilePane r){
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
    private static void startMultiplayer(Stage s) {
        s.setTitle("ServerBrowser");
        Button J = new Button("Join game");
        Button H = new Button("Host game");
        Label l = new Label("Do you want to Join or Host a game?");

        TilePane r = new TilePane(Orientation.VERTICAL);
        r.setAlignment(Pos.CENTER);
        r.setVgap(10);

        EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent ev)
            {
                join(s);
            }
        };
        EventHandler<ActionEvent> event1 = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent ev)
            {
                host(s);
            }
        };

        J.setOnAction(event);
        H.setOnAction(event1);

        r.getChildren().add(l);
        r.getChildren().add(J);
        r.getChildren().add(H);


        vbox(s, r);
    }
    private static void host(Stage s) {
        s.setTitle("Host Server");
        Button H = new Button("Host");
        Label l = new Label("Input game ID");
        TilePane r = new TilePane(Orientation.VERTICAL);
        r.setAlignment(Pos.CENTER);
        r.setVgap(10);
        final TextField gameID = new TextField();

        EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent ev)
            {
                newGameOrLoad(gameID.getText(), s);
            }
        };

        H.setOnAction(event);

        r.getChildren().addAll(l, gameID, H);

        vbox(s, r);
    }


    public static void newGameOrLoad(String gameID, Stage s){
        s.setTitle("New game or continue?");
        Button a = new Button("New game");
        Button b = new Button("Continue from save");
        TilePane r = new TilePane(Orientation.VERTICAL);
        r.setAlignment(Pos.CENTER);
        r.setVgap(10);
        Label l = new Label("Do you want to start a new game or continue an existing game?");
        saveGame = null;
        EventHandler<ActionEvent> newGame = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent ev)
            {
                chooseMap(gameID,  s);
            }
        };
        EventHandler<ActionEvent> continueGame = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent ev)
            {
                loadGame(gameID, s);
            }
        };


        a.setOnAction(newGame);
        b.setOnAction(continueGame);

        r.getChildren().addAll(l, a, b);

        vbox(s, r);
    }


    private static void loadGame(String gameID, Stage s){
        TextField saveInput = new TextField("savegame_01");
        Button b = new Button("Continue");
        TilePane r = new TilePane(Orientation.VERTICAL);
        r.setAlignment(Pos.CENTER);
        r.setVgap(10);

        EventHandler<ActionEvent> continueGame = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent ev)
            {
                saveGame = saveInput.getText();
                startGame(gameID, null, s);
            }
        };

        b.setOnAction(continueGame);

        Label l = new Label("Enter savegame name");
        r.getChildren().addAll(l, saveInput, b);
        vbox(s,r);


    }


    public static void chooseMap(String gameID, Stage s){
        s.setTitle("Maps");
        Button a = new Button("Dizzy Highway");
        Button b = new Button("High Octane");
        Button t = new Button("Test Map");
        TilePane r = new TilePane(Orientation.VERTICAL);
        r.setAlignment(Pos.CENTER);
        r.setVgap(10);
        Label l = new Label("Choose a Map");

        EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent ev)
            {
                startGame(gameID, "DizzyHighway", s);
            }
        };
        EventHandler<ActionEvent> event1 = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent ev)
            {
                startGame(gameID, "High Octane", s);
            }
        };
        EventHandler<ActionEvent> event2 = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent ev)
            {

                startGame(gameID, "testMap", s);
            }
        };

        a.setOnAction(event);
        b.setOnAction(event1);
        t.setOnAction(event2);

        r.getChildren().addAll(l, a, b, t);

        vbox(s, r);
    }

    private static void startGame(String gameID, String map, Stage s) {
        try {
            ClientConsume.hostGame(gameID);

        } catch (Exception e){
            Alert notEnoughAlert = new Alert(Alert.AlertType.WARNING, "You need to be at least 2 players!");
            notEnoughAlert.showAndWait();
            ClientConsume.hostGame(gameID);
            return;
        }

        s.setTitle("Start");
        Button start = new Button("Start");
        TilePane r = new TilePane(Orientation.VERTICAL);
        r.setAlignment(Pos.CENTER);
        r.setVgap(10);
        Label l = new Label("Press start, when you are ready to begin the game");

        EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent ev)
            {
                String response;
                if(saveGame == null)
               {
                response = ClientConsume.startGame(gameID, map);
                } else {
                    response = ClientConsume.startGameFromSave(gameID, saveGame);
                }
                switch (response) {
                    case "100" -> {
                        GameController gm = ClientConsume.updateBoard(gameID, ClientConsume.conn.userID);
                        ClientConsume.conn.gameSession.setController(gm);
                        String playerToken = ClientConsume.getPlayerToken(gameID, ClientConsume.conn.userID);
                        Connection.setPlayerToken(playerToken);
                        new RoboRally(new String[]{"online"}, s);
                    }
                    case "200" -> {
                        System.out.println("You are not authenticated!");
                        startGame(gameID, map, s);
                    }
                    case "300" -> {
                        System.out.println("You need to be at least 2 players!");
                        startGame(gameID, map, s);
                    }
                }
            }
        };

        start.setOnAction(event);

        r.getChildren().addAll(l, start);

        vbox(s, r);
    }

    private static void join(Stage s) {
        s.setTitle("Join Server");
        Button J = new Button("Join");
        TilePane r = new TilePane(Orientation.VERTICAL);
        r.setAlignment(Pos.CENTER);
        r.setVgap(10);
        Label l = new Label("Active lobbies");
        Label l1 = new Label("Input game ID");
        Label l2 = new Label("");

        final TextField gameID = new TextField();

        JSONObject jsonObject = new JSONObject(ClientConsume.getLobbies().trim());
        try {
            jsonObject = new JSONObject(ClientConsume.getLobbies().trim());
        } catch (ResourceAccessException e) {
            l2.setText("The server is down");
            startMultiplayer(s);
            return;
        }

        Iterator<String> keys = jsonObject.keys();

        if (!keys.hasNext()){
            l2.setText("No active lobbies");
        } else {
            while(keys.hasNext()) {
                String key = keys.next();
            }
        }

        EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent ev)
            {
                JSONObject jsonObject;


                try {
                    jsonObject = new JSONObject(ClientConsume.getLobbies().trim());
                } catch (Exception e) {
                    l2.setText("The server is down");
                    startMultiplayer(s);
                    return;
                }

                Iterator<String> keys = jsonObject.keys();

                if (!keys.hasNext()){
                    l2.setText("No active lobbies");
                } else {
                    while(keys.hasNext()) {
                        String key = keys.next();
                        //System.out.print("Lobby ID: " + key + ": Player count: ");
                        //System.out.println(jsonObject.get(key));
                    }
                }

                try {
                    if ((int)jsonObject.get(gameID.getText()) >= 6 ){
                        l2.setText("The lobby is full");
                        startMultiplayer(s);
                    }
                    if (ClientConsume.isStarted(gameID.getText())) {
                        l2.setText("You can't join this lobby because the game has started");
                        vbox(s, r);
                        startMultiplayer(s);
                    } else {
                        ClientConsume.joinGame(gameID.getText());
                    }
                } catch (Exception e) {
                    l2.setText("Lobby does not exist");
                    vbox(s, r);
                    startMultiplayer(s);
                    return;
                }
                l2.setText("Waiting for game to start");
                vbox(s, r);
                while (!ClientConsume.isStarted(gameID.getText())){

                    try {
                        TimeUnit.SECONDS.sleep(2);
                    } catch (InterruptedException e){
                        l2.setText("Sleep was interrupted");
                        vbox(s, r);
                    }
                }
                try {
                    GameController gm = ClientConsume.updateBoard(gameID.getText(), ClientConsume.conn.userID);
                    ClientConsume.conn.gameSession.setController(gm);
                    String playerToken = ClientConsume.getPlayerToken(gameID.getText(), ClientConsume.conn.userID);
                    Connection.setPlayerToken(playerToken);
                    new RoboRally(new String[]{"online"}, s);
                } catch (Exception e){
                    startMultiplayer(s);
                    return;
                }



            }
        };

        J.setOnAction(event);

        r.getChildren().addAll(l, l1, gameID, J, l2);

        vbox(s, r);

    }
}
