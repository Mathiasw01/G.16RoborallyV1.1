/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
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
package dk.dtu.compute.se.pisd.roborally.controller;

import com.g16.roborallyclient.ClientConsume;
import dk.dtu.compute.se.pisd.designpatterns.observer.Observer;
import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;

import dk.dtu.compute.se.pisd.roborally.RoboRally;

import dk.dtu.compute.se.pisd.roborally.model.*;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;



/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class AppController implements Observer {

    final private List<Integer> PLAYER_NUMBER_OPTIONS = Arrays.asList(2, 3, 4, 5, 6);
    final private List<String> PLAYER_COLORS = Arrays.asList("red", "green", "blue", "orange", "grey", "magenta");
    final private RoboRally roboRally;
    private GameController gameController;
    private final ProgrammingDeck programmingDeck = new ProgrammingDeck();
    private final boolean isOnline;

    public AppController(@NotNull RoboRally roboRally, boolean isOnline) {
        this.roboRally = roboRally;this.isOnline = isOnline;
    }

    /**
     * New game
     * <p>
     * Initialises a new game. Asks the user for desired number of players, initialises board and
     * draws UI elements.
     * @param map the map that the user chose
     */
    public void newGame(String map) {
        SpaceReader spaceReader = new SpaceReader(map);

        ChoiceDialog<Integer> dialog = new ChoiceDialog<>(PLAYER_NUMBER_OPTIONS.get(0), PLAYER_NUMBER_OPTIONS);
        dialog.setTitle("Player number");
        dialog.setHeaderText("Select number of players");
        Optional<Integer> result = dialog.showAndWait();

        if (result.isPresent()) {
            if (gameController != null) {
                // The UI should not allow this, but in case this happens anyway.
                // give the user the option to save the game or abort this operation!
                if (!stopGame()) {
                    return;
                }
            }

            // XXX the board should eventually be created programmatically or loaded from a file
            //     here we just create an empty board with the required number of players.
            Board board = new Board(13,10, map);
            gameController = new GameController(board, isOnline);
            int no = result.get();

            for (int i = 0; i < no; i++) {
                Player player = new Player(board, PLAYER_COLORS.get(i),"Player " + (i + 1),i+1, programmingDeck.init());
                board.addPlayer(player);
                spaceReader.initPlayers(board,player, i);
            }

            // XXX: V2
            // board.setCurrentPlayer(board.getPlayer(0));
            gameController.startProgrammingPhase();

            roboRally.createBoardView(gameController);
        }
    }

    /**
     * Save game
     * <p>
     * TODO - implement
     */
    public void saveGame() {

        try {
            TextInputDialog dialog = new TextInputDialog("savegame_01");
            dialog.setTitle("Save game");
            dialog.setHeaderText("Save game");
            dialog.setContentText("Please enter the name of the save file");


            Optional<String> result = dialog.showAndWait();
            if (result.isEmpty()){
                return;
            }
            SaveLoadController.serializeAndSave(gameController, result.get());
        } catch (IOException ioe){
            System.out.println("Couldn't save game as file doesnt exist!!!");
        }


    }

    public void saveGameToServer() {

        TextInputDialog dialog = new TextInputDialog("savegame_01");
        dialog.setTitle("Save game to server");
        dialog.setHeaderText("Save game to server");
        dialog.setContentText("Please enter the name of the save file");

        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()){
            return;
        }
        SaveLoadController.serializeAndSaveToServer(gameController, result.get());


    }


    /**
     * Load game
     * <p>
     */
    public void loadGame() {
        TextInputDialog dialog = new TextInputDialog("savegame_01");
        dialog.setTitle("Load game");
        dialog.setHeaderText("Load save");
        dialog.setContentText("Please enter the name of the save file");


        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()){
            return;
        }

        GameController g = SaveLoadController.deserializeAndLoad(result.get());
        if(g == null){
            return;
        }
        if (gameController != null) {
            // The UI should not allow this, but in case this happens anyway.
            // give the user the option to save the game or abort this operation!
           stopGame();
        }
        Board board = new Board(g.board.width,g.board.height, g.board, PLAYER_COLORS);
        gameController = new GameController(board, isOnline);


        gameController.startProgrammingPhase();

        roboRally.createBoardView(gameController);
    }

    /**
     * Stop playing the current game, giving the user the option to save
     * the game or to cancel stopping the game. The method returns true
     * if the game was successfully stopped (with or without saving the
     * game); returns false, if the current game was not stopped. In case
     * there is no current game, false is returned.
     *
     * @return true if the current game was stopped, false otherwise
     */
    public boolean stopGame() {
        if (gameController != null) {

            // here we save the game (without asking the user).
            saveGame();

            gameController = null;
            roboRally.createBoardView(null);
            return true;
        }
        return false;
    }

    /**
     * Exits game
     * <p>
     * Asks the player if the player really wants to exit the game. If confirmed,
     * the program is exited.
     */
    public void exit() {
        if (gameController != null) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Exit RoboRally?");
            alert.setContentText("Are you sure you want to exit RoboRally?");
            Optional<ButtonType> result = alert.showAndWait();

            if (result.isEmpty() || result.get() != ButtonType.OK) {
                return; // return without exiting the application
            }
        }

        // If the user did not cancel, the RoboRally application will exit
        // after the option to save the game
        if (gameController == null || stopGame()) {
            Platform.exit();
        }
    }

    /**
     * Returns if the games is running or not
     * <p>
     * Returns a bool representing if the game is running
     * @return boolean, game is running
     */
    public boolean isGameRunning() {
        return gameController != null;
    }


    @Override
    public void update(Subject subject) {
        // XXX do nothing for now
    }

    public void loadFromServer() {

        GameController gm = ClientConsume.conn.gameSession.getController();

        if(gm== null){
            System.out.println("gm was null");
            return;
        }
        if (gameController != null) {
            // The UI should not allow this, but in case this happens anyway.
            // give the user the option to save the game or abort this operation!
            stopGame();
        }
        Board board = new Board(gm.board.width,gm.board.height, gm.board, PLAYER_COLORS);
        gameController = new GameController(board, isOnline);



        gameController.startProgrammingPhaseFromOnline();

        roboRally.createBoardView(gameController);

    }

    public void saveMap(){
        SaveLoadController.saveMapToJSON(gameController.board.getSpaces(),"src/main/java/dk/dtu/compute/se/pisd/roborally/Maps/jsonTest" );
    }

}
