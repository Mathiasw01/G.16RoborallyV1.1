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
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

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

    public static void main(String[] args) {
        ClientConsume clientConsume = new ClientConsume();
        startMultiplayer(clientConsume);

        System.out.println(System.getProperty("user.dir"));
        RoboRally.main(args);
    }

    private static void startMultiplayer(ClientConsume clientConsume) {
        System.out.println("J join or H host");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();

        if (input.equals("J") || input.equals("j")) {
            System.out.println("Input game ID");
            String gameID = scanner.nextLine();
            clientConsume.joinGame(gameID);

        } else if (input.equals("H") || input.equals("h")) {
            System.out.println("Input game ID");
            String gameID = scanner.nextLine();
            try {
                clientConsume.hostGame(gameID);
                List<String> maps = clientConsume.getMap();
                System.out.println("Choose map");
                for (String str: maps) {
                    System.out.println(str);
                }
                String map = choseMap(scanner, maps);
                System.out.println(map);
            } catch (RestClientException e){
                System.out.println("This lobby already exist");
                startMultiplayer(clientConsume);
            }
        } else {

        }
    }

    private static String choseMap(Scanner scanner, List<String> maps) {
        String chosenMap = scanner.nextLine();
        String finalMap = null;
        Optional<String> result = maps.stream().filter(map -> map.equals(chosenMap)).findFirst();
        if (result.isPresent()){
            finalMap = result.get();
        } else {
            System.out.println("This map does not exist, try again");
            return choseMap(scanner, maps);
        }
        return finalMap;
    }

}
