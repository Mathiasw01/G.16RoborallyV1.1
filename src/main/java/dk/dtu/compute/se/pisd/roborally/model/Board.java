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
package dk.dtu.compute.se.pisd.roborally.model;

import com.google.gson.annotations.Expose;
import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import javafx.scene.paint.Color;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static dk.dtu.compute.se.pisd.roborally.model.Phase.INITIALISATION;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class Board extends Subject {

    @Expose
    public final int width;

    @Expose
    public final int height;

    @Expose
    public final String boardName;

    @Expose
    private Integer gameId;

    @Expose
    private final Space[][] spaces;

    @Expose
    private ArrayList<CheckpointField> checkpoints = new ArrayList<>();


    @Expose
    private final List<Player> players = new ArrayList<>();

    @Expose
    private Player current;

    @Expose
    private Phase phase = INITIALISATION;

    private int step = 0;

    @Expose
    private boolean stepMode;

    private int counter=0;
    private SpaceReader spaceReader;

    /**
     * Creates a new board
     * <p>
     * Creates a new board by instantiating spaces.
     * @param width the width of the board in blocks
     * @param height the height of the board in blocks
     * @param boardName The name of the board
     * @param map the map name to be loaded from the resources folder
     */
    public Board(int width, int height, String map, @NotNull String boardName) {
        this.boardName = boardName;
        this.width = width;
        this.height = height;
        spaces = new Space[width][height];
        for (int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                Space space = new Space(this, x, y);
                spaces[x][y] = space;

            }
        }
        spaceReader = new SpaceReader(map);
        spaceReader.initMap(this);
        this.stepMode = false;
    }

    /**
     * Creates a new default board
     * <p>
     * Creates a new board by instantiating spaces.
     * @param width the width of the board in blocks
     * @param height the height of the board in blocks
     * @param map the map name to be loaded from the resources folder
     */
    public Board(int width, int height, String map) {
        this(width, height, map,  "defaultboard");
    }


    /**
     * Get game id
     * <p>
     * Returns game ID
     * @return Integer gameID
     */
    public Integer getGameId() {
        return gameId;
    }


    public Board(int width, int height, Board savedBoard, List<String> PLAYER_COLORS) {
        this.boardName = savedBoard.boardName;
        this.width = savedBoard.width;
        this.height = savedBoard.height;
        spaces = new Space[width][height];
        for (int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                Space space = new Space(this, x, y);
                spaces[x][y] = space;

            }
        }

        int currentp = 0;
        for (int i = 0; i < savedBoard.getPlayersNumber(); i++) {
            Player player = new Player(this, PLAYER_COLORS.get(i), "Player " + (i + 1));
            player.setHeading(savedBoard.getPlayer(i).getHeading());
            this.addPlayer(player);
            if(Objects.equals(savedBoard.getPlayers().get(i).getName(), savedBoard.getCurrentPlayer().getName())){
                currentp = i;
            }
        }

        setCurrentPlayer(getPlayer(currentp));


        for (Space[] sr : savedBoard.spaces){
            for(Space s : sr){

                for(FieldObject fo : s.getObjects()){
                    if(fo instanceof Conveyor conveyor){
                        spaces[s.x][s.y].addObjects(new Conveyor(Color.BLUE, conveyor.getDirection()));
                    } else if (fo instanceof  StartField sf) {
                        spaces[s.x][s.y].addObjects(new StartField());
                    }  else if (fo instanceof  CheckpointField cp) {
                        CheckpointField cpf = new CheckpointField(cp.getCheckpointNumber());
                        for(Player p : cp.getPlayersObtained()){
                            for(Player bp : players){
                                if(Objects.equals(bp.getName(), p.getName())){
                                    cpf.addPlayerIfUnobtained(bp);
                                }
                            }
                        }
                        spaces[s.x][s.y].addObjects(cpf);
                        checkpoints.add(cpf);
                    }  else if (fo instanceof  Gear gear) {
                        spaces[s.x][s.y].addObjects(new Gear(gear.getDirection()));
                    }   else if (fo instanceof  Wall wall) {
                        spaces[s.x][s.y].addObjects(new Wall(wall.getDir()));
                }


                }
                if(s.getPlayer() != null){
                    Optional<Player> p = players.stream().filter(x -> Objects.equals(x.getName(), s.getPlayer().getName())).findFirst();
                    if(p.isPresent()){
                        p.get().setSpace(spaces[s.x][s.y]);
                        System.out.println("Test");
                    }

                }
            }
        }
        this.stepMode = false;
    }


    /**
     * Set game id
     * <p>
     * Sets the current game ID
     * @param gameId the id to be assigned this game
     */
    public void setGameId(int gameId) {
        if (this.gameId == null) {
            this.gameId = gameId;
        } else {
            if (!this.gameId.equals(gameId)) {
                throw new IllegalStateException("A game with a set id may not be assigned a new id!");
            }
        }
    }

    /**
     * Get space
     * <p>
     * Returns the space at the given location
     * @param x x-coordinate
     * @param y y-coordinate
     * @return Space object
     */
    public Space getSpace(int x, int y) {
        if (x >= 0 && x < width &&
                y >= 0 && y < height) {
            return spaces[x][y];
        } else {
            return null;
        }
    }
    /**
     * Get counter
     * <p>
     * Returns the board counter
     * @return int board counter
     */
    public int getCounter() {
        return counter;
    }

    /**
     * Set counter
     * <p>
     * Sets the board counter
     * @param counter the value to be assigned to the counter
     */
    public void setCounter(int counter) {
        this.counter = counter;
    }

    /**
     * Get number of players
     * <p>
     * @return int number of players
     */
    public int getPlayersNumber() {
        return players.size();
    }

    /**
     * Add player
     * <p>
     * Adds a player to the board
     * @param player The player to be added
     */
    public void addPlayer(@NotNull Player player) {
        if (player.board == this && !players.contains(player)) {
            players.add(player);
            notifyChange();
        }
    }

    /**
     * Get player by index
     * <p>
     * Returns player based on the index
     * @param i player index
     */
    public Player getPlayer(int i) {
        if (i >= 0 && i < players.size()) {
            return players.get(i);
        } else {
            return null;
        }
    }

    /**
     * Get current player
     * <p>
     * Returns the player who has the current turn
     * @return player object
     */
    public Player getCurrentPlayer() {
        return current;
    }

    /**
     * Set current player
     * <p>
     * Sets the current player
     * @param player the player who will get its turn
     */
    public void setCurrentPlayer(Player player) {
        if (player != this.current && players.contains(player)) {
            this.current = player;
            notifyChange();
        }
    }

    /**
     * Get phase
     * <p>
     * Get current board phase
     * @return phase enum object
     */
    public Phase getPhase() {
        return phase;
    }

    /**
     * Set game phase
     * <p>
     * Set current board phase
     * @param phase phase enum to be the new phase
     */
    public void setPhase(Phase phase) {
        if (phase != this.phase) {
            this.phase = phase;
            notifyChange();
        }
    }

    /**
     * Get step
     * <p>
     * Get the current game step
     * @return integer representing the current step
     */
    public int getStep() {
        return step;
    }

    /**
     * Set step
     * <p>
     * Set the current game step
     * @param step the new step value
     */
    public void setStep(int step) {
        if (step != this.step) {
            this.step = step;
            notifyChange();
        }
    }

    /**
     * Is step
     * <p>
     * Returns whether the game is in step mode
     * @return boolean, if the game is in stepmode
     */
    public boolean isStepMode() {
        return stepMode;
    }

    /**
     * Setstep
     * <p>
     * Sets step mode
     * @param stepMode whether stepMode should be on or not
     */
    public void setStepMode(boolean stepMode) {
        if (stepMode != this.stepMode) {
            this.stepMode = stepMode;
            notifyChange();
        }
    }

    /**
     * Get player number
     * <p>
     * Returns a players index in the players array
     * @param player The player of which the index will be returned
     * @return integer, index of the player
     */
    public int getPlayerNumber(@NotNull Player player) {
        if (player.board == this) {
            return players.indexOf(player);
        } else {
            return -1;
        }
    }

    /**
     * Returns the neighbour of the given space of the board in the given heading.
     * The neighbour is returned only, if it can be reached from the given space
     * (no walls or obstacles in either of the involved spaces); otherwise,
     * null will be returned.
     *
     * @param space the space for which the neighbour should be computed
     * @param heading the heading of the neighbour
     * @return the space in the given direction; null if there is no (reachable) neighbour
     */
    public Space getNeighbour(@NotNull Space space, @NotNull Heading heading) {
        int x = space.x;
        int y = space.y;
        switch (heading) {
            case SOUTH:
                y = (y + 1) % height;
                break;
            case WEST:
                x = (x + width - 1) % width;
                break;
            case NORTH:
                y = (y + height - 1) % height;
                break;
            case EAST:
                x = (x + 1) % width;
                break;
        }

        return getSpace(x, y);
    }

    public String getStatusMessage() {
        // this is actually a view aspect, but for making assignment V1 easy for
        // the students, this method gives a string representation of the current
        // status of the game

        // XXX: V2 changed the status so that it shows the phase, the player and the step
        return "Phase: " + getPhase().name() +
                ", Player = " + getCurrentPlayer().getName() +
                ", Step: " + getStep();
    }


    public void addCheckpoint(CheckpointField checkpoint){
        checkpoints.add(checkpoint);
    }


    public ArrayList<CheckpointField> getCheckpoints(){
        return this.checkpoints;
    }

    public List<Player> getPlayers() {
        return players;
    }


}
