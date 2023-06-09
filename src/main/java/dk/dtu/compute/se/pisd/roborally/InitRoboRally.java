package dk.dtu.compute.se.pisd.roborally;

import com.fasterxml.jackson.core.JsonParseException;
import com.g16.roborallyclient.ClientConsume;
import com.g16.roborallyclient.Connection;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import javafx.application.Application;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.json.JSONObject;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class InitRoboRally extends Application {

    private static final int MIN_APP_WIDTH = 300;
    private static final int MIN_APP_HEIGHT = 400;

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

        EventHandler<ActionEvent> event = ev -> new RoboRally(new String[]{"offline"}, s);
        EventHandler<ActionEvent> event1 = ev -> startMultiplayer(s);


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
        s.setWidth(MIN_APP_WIDTH);
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

        EventHandler<ActionEvent> event = ev -> join(s);
        EventHandler<ActionEvent> event1 = ev -> host(s);

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

        EventHandler<ActionEvent> event = ev -> newGameOrLoad(gameID.getText(), s);

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

        EventHandler<ActionEvent> newGame = ev -> chooseMap(gameID,  s);
        EventHandler<ActionEvent> continueGame = ev -> loadGame(gameID, s);

        a.setOnAction(newGame);
        b.setOnAction(continueGame);

        r.getChildren().addAll(l, a, b);

        vbox(s, r);
    }


    private static void loadGame(String gameID, Stage s){
        ListView savesList = new ListView();
        savesList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        Button b = new Button("Continue");

        List<String> saves;
        try {
            saves = ClientConsume.getServerSaves();
        } catch (Exception e ){
            Alert ccServerAlert = new Alert(Alert.AlertType.WARNING, "Cannot load saves from server!");
            ccServerAlert.showAndWait();
            startMultiplayer(s);
            return;
        }

        for(String save : saves){
            savesList.getItems().add(save);
        }




        EventHandler<ActionEvent> continueGame = ev -> {
            saveGame = savesList.getSelectionModel().getSelectedItem().toString();
            startGame(gameID, null, s);
        };

        b.setOnAction(continueGame);

        Label l = new Label("Select a save game");

        VBox vbox = new VBox(l, savesList, b);
        vbox.setAlignment(Pos.CENTER);
        Scene primaryScene = new Scene(vbox);
        s.setScene(primaryScene);
        s.setResizable(false);
        s.sizeToScene();
        s.setWidth(MIN_APP_WIDTH);
        s.show();


    }


    public static void chooseMap(String gameID, Stage s){
        s.setTitle("Maps");
        TilePane tilePane = new TilePane(Orientation.VERTICAL);
        tilePane.setAlignment(Pos.CENTER);
        tilePane.setVgap(10);

        Button C = new Button("Choose Map");

        ListView listView = new ListView();
        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        try {
            List<String> maps = ClientConsume.getMap();

            for (String map:maps){
                listView.getItems().add(map);
            }
        } catch (ResourceAccessException e) {
            Alert ccServerAlert = new Alert(Alert.AlertType.WARNING, "Cannot connect to server!");
            ccServerAlert.showAndWait();
            startMultiplayer(s);
            return;
        }

        EventHandler<ActionEvent> event = ev -> {
            String selected = listView.getSelectionModel().getSelectedItems().toString();
            selected = selected.replace("[", "");
            selected = selected.replace("]", "");
            System.out.print(selected);
            startGame(gameID, selected, s);
        };

        C.setOnAction(event);

        tilePane.getChildren().addAll(C);

        VBox vbox = new VBox(listView, tilePane);
        vbox.setMinWidth(MIN_APP_WIDTH);
        Scene primaryScene = new Scene(vbox);
        s.setScene(primaryScene);
        s.setResizable(false);
        s.sizeToScene();
        s.show();
    }

    private static void startGame(String gameID, String map, Stage s) {
        try {
            ClientConsume.hostGame(gameID);
        } catch (ResourceAccessException e){
            Alert ccServerAlert = new Alert(Alert.AlertType.WARNING, "Cannot connect to server!");
            ccServerAlert.showAndWait();
            startMultiplayer(s);
            return;
        } catch (HttpMessageNotReadableException | RestClientException e){
            Alert ccServerAlert = new Alert(Alert.AlertType.WARNING, "Cannot create lobby. Lobby with the same name might already exist.");
            ccServerAlert.showAndWait();
            startMultiplayer(s);
            return;
        }

        s.setTitle("Start");
        Button start = new Button("Start");
        TilePane tilePane = new TilePane(Orientation.VERTICAL);
        tilePane.setAlignment(Pos.CENTER);
        tilePane.setVgap(10);
        Label l = new Label("Lobby name: " + gameID);
        Label l1 = new Label("Press start, when you are ready to begin the game");

        EventHandler<ActionEvent> event = ev -> {
            String response;
            if(saveGame == null)
           {
            response = ClientConsume.startGame(gameID, map);
            } else {
                response = ClientConsume.startGameFromSave(gameID, saveGame);
            }
            switch (response) {
                case "100" ->
                    gameLaunch(gameID, s);
                case "200" -> {
                    System.out.println("You are not authenticated!");
                    Alert authenticationAlert = new Alert(Alert.AlertType.WARNING, "You are not authenticated!");
                    authenticationAlert.showAndWait();
                    startGame(gameID, map, s);
                }
                case "300" -> {
                    System.out.println("You need to be at least 2 players!");
                    Alert notEnoughAlert = new Alert(Alert.AlertType.WARNING, "You need to be at least 2 players!");
                    notEnoughAlert.showAndWait();
                    startGame(gameID, map, s);
                }
            }
        };

        start.setOnAction(event);

        tilePane.getChildren().addAll(l, l1, start);

        vbox(s, tilePane);
    }

    private static void gameLaunch(String gameID, Stage s) {
        GameController gm = ClientConsume.updateBoard(gameID, ClientConsume.conn.userID);
        ClientConsume.conn.gameSession.setController(gm);
        String playerToken = ClientConsume.getPlayerToken(gameID, ClientConsume.conn.userID);
        Connection.setPlayerToken(playerToken);
        new RoboRally(new String[]{"online"}, s);
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
        ListView playerListView = new ListView();
        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        jsonObject(s, l, listView,playerListView);


        EventHandler<ActionEvent> eventJ = ev -> {
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
                    startMultiplayer(s);
                    return;
                }
            }
            try {
                gameLaunch(selected, s);
            } catch (Exception e){
                System.out.println("Error");
                startMultiplayer(s);
            }
        };

        EventHandler<ActionEvent> eventR = ev -> {
            listView.getItems().clear();
            playerListView.getItems().clear();
            jsonObject(s, l, listView, playerListView);
        };

        J.setOnAction(eventJ);
        R.setOnAction(eventR);

        tilePane1.getChildren().add(l);
        tilePane2.getChildren().addAll(l1, J, R);
        tilePane3.getChildren().add(l2);

        HBox inlineLists = new HBox(listView, playerListView);
        inlineLists.setAlignment(Pos.CENTER);
        playerListView.setFocusTraversable(false);

        listView.setMaxWidth(MIN_APP_WIDTH+50);
        playerListView.setMaxWidth(50);

        VBox vbox = new VBox(tilePane1, inlineLists, tilePane2);
        vbox.setMinWidth(MIN_APP_WIDTH);
        Scene primaryScene = new Scene(vbox);
        s.setScene(primaryScene);
        s.setResizable(false);
        s.sizeToScene();
        s.show();
    }

    private static void jsonObject(Stage s, Label l, ListView listView, ListView playerListView) {
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
                playerListView.getItems().add(jsonObject.get(key)+"/6");
            }
        }
    }
}
