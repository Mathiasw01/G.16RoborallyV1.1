package dk.dtu.compute.se.pisd.roborally;

import com.g16.roborallyclient.ClientConsume;
import com.g16.roborallyclient.Connection;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
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
        } catch (ResourceAccessException e){
            Alert ccServerAlert = new Alert(Alert.AlertType.WARNING, "Cannot connect to server!");
            ccServerAlert.showAndWait();
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
                        Alert authenAlert = new Alert(Alert.AlertType.WARNING, "You are not authenticated!");
                        authenAlert.showAndWait();
                        startGame(gameID, map, s);
                    }
                    case "300" -> {
                        System.out.println("You need to be at least 2 players!");
                        Alert notEnoughAlert = new Alert(Alert.AlertType.WARNING, "You need to be at least 2 players!");
                        notEnoughAlert.showAndWait();
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

        TilePane tilePane1 = new TilePane(Orientation.HORIZONTAL);
        tilePane1.setAlignment(Pos.CENTER);
        TilePane tilePane2 = new TilePane(Orientation.VERTICAL);
        tilePane2.setAlignment(Pos.CENTER);
        tilePane2.setVgap(10);
        TilePane tilePane3 = new TilePane(Orientation.VERTICAL);
        tilePane3.setAlignment(Pos.CENTER);
        tilePane3.setVgap(10);
        Button J = new Button("Join");
        Button R = new Button("Refresh");
        Label l = new Label("Active lobbies");
        Label l1 = new Label("Choose a server");
        Label l2 = new Label("Waiting for game to start");

        ListView listView = new ListView();

        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(ClientConsume.getLobbies().trim());
        } catch (ResourceAccessException e) {
            Alert ccServerAlert = new Alert(Alert.AlertType.WARNING, "Cannot connect to server!");
            ccServerAlert.showAndWait();
            startMultiplayer(s);
            return;
        }

        Iterator<String> keys = jsonObject.keys();

        if (!keys.hasNext()){
            l.setText("No active lobbies");
        } else {
            while(keys.hasNext()) {
                String key = keys.next();
                listView.getItems().add(key);
                System.out.print("Lobby ID: " + key + ": Player count: ");
                System.out.println(jsonObject.get(key));
            }
        }

        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        EventHandler<ActionEvent> eventJ = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent ev)
            {
                JSONObject jsonObject;

                try {
                    jsonObject = new JSONObject(ClientConsume.getLobbies().trim());
                } catch (Exception e) {
                    Alert ccServerAlert = new Alert(Alert.AlertType.WARNING, "Cannot connect to server!");
                    ccServerAlert.showAndWait();
                    startMultiplayer(s);
                    return;
                }

                String selected = listView.getSelectionModel().getSelectedItems().toString();
                selected = selected.replace("[", "");
                selected = selected.replace("]", "");
                System.out.print(selected);
                try {
                    if ((int)jsonObject.get(selected) >= 6 ){
                        l1.setText("The lobby is full");
                        System.out.println("The lobby is full");
                        return;
                    }
                    if (ClientConsume.isStarted(selected)) {
                        l1.setText("You can't join this lobby because the game has started");
                        System.out.println("You can't join this lobby because the game has started");
                        return;
                    } else {
                        VBox vbox = new VBox(tilePane3);
                        vbox.setMinWidth(MIN_APP_WIDTH);
                        Scene primaryScene = new Scene(vbox);
                        s.setScene(primaryScene);
                        s.setResizable(false);
                        s.sizeToScene();
                        s.show();
                        ClientConsume.joinGame(selected);
                    }
                } catch (Exception e) {
                    l1.setText("Lobby does not exist");
                    return;
                }

                while (!ClientConsume.isStarted(selected)){
                    try {
                        TimeUnit.SECONDS.sleep(2);
                    } catch (InterruptedException e){
                        l2.setText("Sleep was interrupted");
                        startMultiplayer(s);
                        return;
                    }
                }
                try {
                    GameController gm = ClientConsume.updateBoard(selected, ClientConsume.conn.userID);
                    ClientConsume.conn.gameSession.setController(gm);
                    String playerToken = ClientConsume.getPlayerToken(selected, ClientConsume.conn.userID);
                    Connection.setPlayerToken(playerToken);
                    new RoboRally(new String[]{"online"}, s);
                } catch (Exception e){
                    startMultiplayer(s);
                    System.out.println("Error");
                    return;
                }
            }
        };

        EventHandler<ActionEvent> eventR = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent ev)
            {
                listView.getItems().clear();
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(ClientConsume.getLobbies().trim());
                } catch (ResourceAccessException e) {
                    Alert ccServerAlert = new Alert(Alert.AlertType.WARNING, "Cannot connect to server!");
                    ccServerAlert.showAndWait();
                    startMultiplayer(s);
                    return;
                }

                Iterator<String> keys = jsonObject.keys();

                if (!keys.hasNext()){
                    l.setText("No active lobbies");
                } else {
                    while(keys.hasNext()) {
                        String key = keys.next();
                        listView.getItems().add(key);
                        System.out.print("Lobby ID: " + key + ": Player count: ");
                        System.out.println(jsonObject.get(key));
                    }
                }
            }
        };

        J.setOnAction(eventJ);
        R.setOnAction(eventR);

        tilePane1.getChildren().add(l);
        tilePane2.getChildren().addAll(l1, J, R);
        tilePane3.getChildren().add(l2);

        VBox vbox = new VBox(tilePane1, listView, tilePane2);
        vbox.setMinWidth(MIN_APP_WIDTH);
        Scene primaryScene = new Scene(vbox);
        s.setScene(primaryScene);
        s.setResizable(false);
        s.sizeToScene();
        s.show();
    }
}
