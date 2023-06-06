/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020,2021: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package dk.dtu.compute.se.pisd.roborally;

import com.g16.roborallyclient.ClientConsume;
import com.g16.roborallyclient.Connection;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import org.json.JSONObject;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * This is a class for starting up the RoboRally application. This is a
 * workaround for a strange quirk in the Open JavaFX project launcher,
 * which prevents starting a JavaFX application in IntelliJ directly:
 *
 *   https://stackoverflow.com/questions/52569724/javafx-11-create-a-jar-file-with-gradle/52571719#52571719
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class StartRoboRally {

    private static RoboRally roboRally = new RoboRally();

    public static void main(String[] args) {
        ClientConsume clientConsume = new ClientConsume();
        localOrOnline(clientConsume);
        /*
        System.out.println(System.getProperty("user.dir"));
        RoboRally.main(args);

         */
    }

    private static void localOrOnline(ClientConsume clientConsume) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("S singleplayer or M multiplayer");
        String in = scanner.nextLine();

        if (in.equals("S") || in.equals("s")) {
            RoboRally.main(new String[]{"offline"});
        } else if (in.equals("M") || in.equals("m")) {
            startMultiplayer(clientConsume);
        } else {
            System.out.println("Not a command");
            localOrOnline(clientConsume);
        }
    }

    private static void startMultiplayer(ClientConsume clientConsume) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("J join or H host");
        String input = scanner.nextLine();

        if (input.equals("J") || input.equals("j")) {
            join(clientConsume, scanner);
        } else if (input.equals("H") || input.equals("h")) {
            host(clientConsume, scanner);
        } else {
            System.out.println("Not a command");
            startMultiplayer(clientConsume);
        }
    }

    private static void host(ClientConsume clientConsume, Scanner scanner) {
        System.out.println("Input game ID");
        String gameID = scanner.nextLine();
        String map = "testbeepboop";
        try {
            clientConsume.hostGame(gameID);

            List<String> maps = clientConsume.getMap();
            System.out.println("Choose map");
            for (String str: maps) {
                System.out.println(str);
            }
            map = chooseMap(scanner, maps);
        } catch (Exception e){
            if (e instanceof ResourceAccessException){
                System.out.println("The server is down");
            }
            if (e instanceof RestClientException){
                System.out.println("This lobby already exist");
            }
            startMultiplayer(clientConsume);
        }



        startGame(clientConsume, scanner, gameID, map);

    }

    private static void startGame(ClientConsume clientConsume, Scanner scanner, String gameID, String map) {
        System.out.println("press s to start");
        String keyPress = scanner.nextLine();
        if (keyPress.equals("s") || keyPress.equals("S")) {
            System.out.println(clientConsume.startGame(gameID, map));
            if (clientConsume.startGame(gameID, map).equals("100")){

                GameController gm = clientConsume.updateBoard(gameID, ClientConsume.conn.userID);

                ClientConsume.conn.gameSession.setController(gm);
                String playerToken = clientConsume.getPlayerToken(gameID, ClientConsume.conn.userID);
                Connection.setPlayerToken(playerToken);
                RoboRally.main(new String[]{"online"});
            } else if (clientConsume.startGame(gameID, map).equals("200")){
                System.out.println("You are not authenticated!");
                startGame(clientConsume, scanner, gameID, map);
            } else if (clientConsume.startGame(gameID, map).equals("300")){
                System.out.println("You need to be at least 2 players!");
                startGame(clientConsume, scanner, gameID, map);
            }
        }
    }

    private static void join(ClientConsume clientConsume, Scanner scanner){
        System.out.println("Active lobbies");
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(clientConsume.getLobbies().trim());
        } catch (ResourceAccessException e) {
            System.out.println("The server is down");
            localOrOnline(clientConsume);
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
            if ((int)jsonObject.get(gameID) >= 6 ) {
                System.out.println("The lobby is full");
                startMultiplayer(clientConsume);
            }
            if (clientConsume.isStarted(gameID, ClientConsume.conn.userID)) {
                System.out.println("You can't join this because the game has started");
            } else {
                clientConsume.joinGame(gameID);
            }
        } catch (RestClientException e) {
            System.out.println("Lobby does not exist");
            startMultiplayer(clientConsume);
        }
        //System.out.println("Press any key to start");
        //scanner.nextLine();
        while (!clientConsume.isStarted(gameID, ClientConsume.conn.userID)){
            System.out.println("Waiting for game to start");
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e){
                System.out.println("Sleep was interrupted");
            }
        }
        GameController gm = clientConsume.updateBoard(gameID, ClientConsume.conn.userID);

        ClientConsume.conn.gameSession.setController(gm);
        String playerToken = clientConsume.getPlayerToken(gameID, ClientConsume.conn.userID);
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
