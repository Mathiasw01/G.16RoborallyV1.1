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
import com.g16.roborallyclient.Connection;
import com.g16.roborallyclient.GameSession;
import com.g16.roborallyclient.WaitForProgramming;
import com.google.gson.annotations.Expose;
import dk.dtu.compute.se.pisd.roborally.RoboRally;
import dk.dtu.compute.se.pisd.roborally.model.*;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class GameController {

    @Expose
    final public Board board;
    private Heading originalHeading = null;

    private boolean isOnline;

    private boolean winnerFound = false;
    private Player winner;


    public Heading getOriginalHeading() {
        return originalHeading;
    }

    public void setOriginalHeading(Heading originalHeading) {
        this.originalHeading = originalHeading;
    }

    public GameController(@NotNull Board board, boolean isOnline) {
        this.board = board;
        this.isOnline = isOnline;
    }

    /**
     * Moves current player to space if possible
     * <p>
     * Moves current player to the parsed space if the space is empty
     */
    public void moveCurrentPlayerToSpace(@NotNull Space space, boolean backupflag, Player player, Heading conveyorHeading, boolean conPush) {
        board.setCounter(board.getCounter() + 1);
        ArrayList<FieldObject> walls = space.findObjectsOfType(Wall.class);
        ArrayList<FieldObject> currentSpaceWalls = player.getSpace().findObjectsOfType(Wall.class);

        /*
         * Stops current player from moving if there is a wall in the way
         */
        if (originalHeading == null) {
            if (conPush){
                setOriginalHeading(conveyorHeading);
            } else {
                setOriginalHeading(player.getHeading());
            }
        }

        /*
         *Stops current player from moving if there is a wall in the way
         */
        for (FieldObject object : walls) {
            if (isWallBlocking((Wall) object, backupflag, false))
                return;
        }
        for (FieldObject object: currentSpaceWalls) {
            if (isWallBlocking((Wall) object, backupflag, true))
                return;
        }
        /*
        If the target space is free, move and return!
         */
        if (space.getPlayer() == null) {
            player.setSpace(space);
            return;
        }

        /*
         * Target space is occupied, push if possible!
         * Pushes other players if they occupy the space the current player is moving through
         * Stops if there is a wall in the way
         */
        handleRobotCollision(space, backupflag, player, conveyorHeading, conPush);

    }

    private void handleRobotCollision(@NotNull Space space, boolean backupflag, Player player, Heading conveyorHeading, boolean conPush) {
        Player player2 = space.getPlayer();
        Wall player2CurrentSpaceWall = (Wall) player2.getSpace().findObjectOfType(Wall.class);
        int x = space.x;
        int y = space.y;
        int[] newCoordinates = getNewCoordinates(getOriginalHeading(),x,y, backupflag);
        x = newCoordinates[0];
        y = newCoordinates[1];

        if (!backupflag && conveyorHeading == null){
            if (player2CurrentSpaceWall != null) {
                if (getOriginalHeading() == player2CurrentSpaceWall.getDir()) {
                    return;
                }
            }
        } else {
            newCoordinates = getNewCoordinates(conveyorHeading,x,y, backupflag);
            x = newCoordinates[0];
            y = newCoordinates[1];
        }

        if(board.getSpace(x,y) == null){
            return;
        }

        if (canPush(board.getSpace(x,y), conPush ? conveyorHeading :originalHeading, backupflag, player)) {
            moveCurrentPlayerToSpace(board.getSpace(x, y), backupflag, player2, conveyorHeading, conPush);
            player.setSpace(space);
        }
    }


    private boolean isWallBlocking(Wall wall, boolean backupflag, boolean ownField){
        if(wall == null){
            return false;
        }
        boolean reversed = backupflag;
        if(!ownField){
            reversed = !reversed;
        }
        return wall.getDir() == (reversed ? getOriginalHeading().next().next() :
                getOriginalHeading());
    }

    /**
     *
     * @param heading The players heading or the conveyor belts heading
     * @param x x coordinate for the space
     * @param y y coordinate for the space
     * @param backupflag A boolean for whether the player is backing up or moving forward
     * @return Returns coordinates for the space in front of the player and behind them
     * This function is used to get the coordinates for the space behind and in front of the player.
     * These values are determined by the heading.
     */
    public static int[] getNewCoordinates(Heading heading, int x, int y, boolean backupflag) {
        int prvx = x;
        int prvy = y;
        if (backupflag){
            switch (heading) {
                case EAST -> {x--;prvx++;}
                case WEST -> {x++;prvx--;}
                case NORTH -> {y++;prvy--;}
                case SOUTH -> {y--;prvy++;}
            }
        } else {
            switch (heading) {
                case EAST -> {x++;prvx--;}
                case WEST -> {x--;prvx++;}
                case NORTH -> {y--;prvy++;}
                case SOUTH -> {y++;prvy--;}
            }
        }
        return new int[] { x, y, prvx, prvy};
    }

    public boolean canPush(Space space, Heading heading,  boolean backupflag, Player player) {
        if (space == null) {
            reboot(player);
            return false;
        }
        player = space.getPlayer();
        int x = space.x;
        int y = space.y;
        int prvx;
        int prvy;

        int[] newCoordinates = getNewCoordinates(heading,x,y,backupflag);
        x = newCoordinates[0];
        y = newCoordinates[1];
        prvx = newCoordinates[2];
        prvy = newCoordinates[3];

        Space nextSpace = board.getSpace(x, y);
        Space prvSpace = board.getSpace(prvx,prvy);
            if (space.getPlayer() == null) {
                Wall wall = (Wall) space.findObjectOfType(Wall.class);
                Wall prvWall = (Wall) prvSpace.findObjectOfType(Wall.class);
                if (wall != null){
                    if (!backupflag) {
                        if (wall.getDir().next().next() == heading) {
                            return false;
                        }
                    } else if (wall.getDir() == heading){
                        return false;
                    }
                }
                if (prvWall != null){
                    if (backupflag) {
                        if (prvWall.getDir().next().next() == heading) {
                            return false;
                        }
                    } else if (prvWall.getDir() == heading){
                        return false;
                    }
                }
                return true;
            } else {
                return canPush(nextSpace, heading, backupflag, player);
            }
        }

    /**
     * Start programming phase
     * <p>
     * Sets the current phase to programming phase and sets current player and step index to 0
     * Removes all the cards in the command card fields and replaces them with new, random command cards
     */
    public void startProgrammingPhase() {
        board.setPhase(Phase.PROGRAMMING);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            if (player != null) {
                for (int j = 0; j < Player.NO_REGISTERS; j++) {
                    CommandCardField field = player.getProgramField(j);
                    field.setCard(null);
                    field.setVisible(true);
                }
                for (int j = 0; j < Player.NO_CARDS; j++) {
                    CommandCardField field = player.getCardField(j);
                  //field.setCard(generateRandomCommandCard());
                    /* Get new cards from server...*/
                        field.setCard(drawCard(board.getCurrentPlayer().getProgrammingDeck(),player));
                    field.setVisible(true);
                }
            }
        }
    }

    public CommandCard drawCard(List<CommandCard> deck, Player currentPLayer) {
        if (currentPLayer.getProgrammingDeck().isEmpty()) {
            shuffleDeck(currentPLayer.getProgrammingDeck(), currentPLayer.getDiscardPile());
        }
        CommandCard topCard = currentPLayer.getProgrammingDeck().get(0);
        discardCard(currentPLayer,topCard);
        return topCard;
    }

    public void discardCard(Player player, CommandCard card) {
        player.getProgrammingDeck().remove(card);
        player.getDiscardPile().add(card);
    }

    public void shuffleDeck(List<CommandCard> deck, List<CommandCard> discardPile) {
        deck.addAll(discardPile);
        discardPile.clear();
        Collections.shuffle(deck);
    }

    /**
     * Removes a single command card with a specific command, it's used when a damage card is played.
     * @param discardPile
     * @param command The command that you want to be removed
     */
    public void removeOneCardWithCommand(List<CommandCard> discardPile, Command command) {


        Iterator<CommandCard> discardIterator = discardPile.iterator();
        while (discardIterator.hasNext()) {
            CommandCard card = discardIterator.next();
            if (card.command == command) {
                discardIterator.remove();

            }
        }
    }

    /**
     * Finish programming phase and start activation phase
     * <p>
     * Finished programming phase by locking the progam fields and makes the programmed card invisble except for
     * the card in register 0. Afterwards changes the game phase to acivation phase and sets the current
     * player and step to index 0
     */
    public void finishProgrammingPhase() throws InterruptedException {
        makeProgramFieldsInvisible();
        makeProgramFieldsVisible(0);

        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);

        if(isOnline){
            board.setPhase(Phase.WAITING);
            waitForOtherPlayersToFinishProgramming();
        } else {
            board.setPhase(Phase.ACTIVATION);
        }


    }

    private void waitForOtherPlayersToFinishProgramming() throws InterruptedException {
        for (Player player : board.getPlayers()) {
            if (player.getName().equals(Connection.getPlayerToken())){
                ClientConsume.sendProgram(ClientConsume.conn.gameSession.gameID, ClientConsume.conn.userID, player.getProgram());
                break;
            }
        }
        String[] cards = new String[board.getPlayersNumber()*5];
        WaitForProgramming waitForProgramming = new WaitForProgramming(this, cards);
        Thread thread = new Thread(waitForProgramming);
        thread.start();


    }

    public void executeCommandsFromServer(String[] cards){


        int playerIndex = 0;


        for (Player player : board.getPlayers()) {
            for (int i = 0; i < 5; i++) {
                player.getProgramField(i).setCard(convertCommand(cards[i + (playerIndex * 5)]));
            }
            playerIndex++;
        }
        board.setPhase(Phase.ACTIVATION);
    }

    private CommandCard convertCommand(String sCom){
        return switch (sCom) {
            case "Move 1" -> new CommandCard(Command.FORWARD);
            case "Move 2" -> new CommandCard(Command.FAST_FORWARD);
            case "Move 3" -> new CommandCard(Command.MOVE_THREE);
            case "Turn Right" -> new CommandCard(Command.RIGHT);
            case "Turn Left" -> new CommandCard(Command.LEFT);
            case "Do a u-turn" -> new CommandCard(Command.UTURN);
            case "Power Up" -> new CommandCard(Command.POWERUP);
            case "Back Up" -> new CommandCard(Command.MOVE_BACK);
            case "Repeat last card" -> new CommandCard(Command.AGAIN);
            case "Turn left or right" -> new CommandCard(Command.CHOOSETURN);
            case "Spam" -> new CommandCard(Command.SPAM);
            //case "wait" -> new CommandCard(Command.WAIT);
            default -> null;
        };
    }

    // XXX: V2
    private void makeProgramFieldsVisible(int register) {
        if (register >= 0 && register < Player.NO_REGISTERS) {
            for (int i = 0; i < board.getPlayersNumber(); i++) {
                Player player = board.getPlayer(i);
                CommandCardField field = player.getProgramField(register);
                field.setVisible(true);
            }
        }
    }

    // XXX: V2
    private void makeProgramFieldsInvisible() {
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            for (int j = 0; j < Player.NO_REGISTERS; j++) {
                CommandCardField field = player.getProgramField(j);
                field.setVisible(false);
            }
        }
    }

    /**
     * Disables step mode and executes the programs
     * <p>
     * Disables step mode and runs the programs programmed by the player.
     */
    public void executePrograms() {
        board.setStepMode(false);
        continuePrograms();
    }

    /**
     * Executes the next step
     * <p>
     * Execute the card in the current step for the current player and doesn't continue execution afterwards.
     */
    public void executeStep() {
        board.setStepMode(true);
        continuePrograms();
    }

    /**
     * Executes the players programs continuously
     * <p>
     * This method executes all the programmed cards. It executes the next step and the cards programmed in each step
     * by the individual players, until the phase is no longer the activation phase or the game is not in step mode.
     */
    private void continuePrograms() {
        do {
            executeNextStep();
        } while (board.getPhase() == Phase.ACTIVATION && !board.isStepMode());


        if (winnerFound){
            displayWinner();
        }

    }

    /**
     * Execute the programmed cards next step
     * <p>
     * This method executes the next card. After executing the card, it sets the turn to the
     * next player, and if all the players have executed their cards programmed in the current step, it will
     * increase the step by one. If the step is larger than the number of registers the players have, the
     * method will change the game's phase to the programing phase.
     */
    private void executeNextStep() {
        List<Player> players = board.getPlayers();
        Player currentPlayer = board.getCurrentPlayer();
        if (board.getPhase() == Phase.ACTIVATION && currentPlayer != null) {
            int step = board.getStep();
            if (step >= 0 && step < Player.NO_REGISTERS) {
                originalHeading = null;
                CommandCard card = currentPlayer.getProgramField(step).getCard();
                if (card != null) {
                    Command command = card.command;
                    if (command.isInteractive()){
                        if (isOnline){
                            if (Objects.equals(currentPlayer.getName(), Connection.getPlayerToken())) {
                                board.setPhase(Phase.PLAYER_INTERACTION);
                                return;
                            } else {
                                do {
                                    try {
                                        TimeUnit.SECONDS.sleep(2);
                                    } catch (InterruptedException e){
                                        System.out.println("interrupt");
                                    }
                                } while (ClientConsume.getInteractive(ClientConsume.conn.gameSession.gameID, ClientConsume.conn.userID, String.valueOf(step)) == null);
                                String respone = ClientConsume.getInteractive(ClientConsume.conn.gameSession.gameID, ClientConsume.conn.userID, String.valueOf(step)).getCommand();
                                command = convertCommand(respone).command;

                                /*
                                String response;
                                do {
                                    try {
                                        TimeUnit.SECONDS.sleep(2);
                                    } catch (InterruptedException e){
                                        System.out.println("interrupt");
                                    }
                                    response = ClientConsume.getInteractive(ClientConsume.conn.gameSession.gameID, ClientConsume.conn.userID);
                                } while (response.equals("wait"));

                                 */
                            }
                        } else {
                            board.setPhase(Phase.PLAYER_INTERACTION);
                            return;
                        }
                    }
                    if (!currentPlayer.getRebooting()) {
                        executeCommand(currentPlayer, command);
                    }
                }
                int nextPlayerNumber = board.getPlayerNumber(currentPlayer) + 1;
                if (nextPlayerNumber < board.getPlayersNumber()) {
                    board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));
                } else {
                    multiThreadExecute(step);
                    /*
                    if (winnerFound){
                        displayWinner();
                        return;
                    }

                     */
                    step++;
                    if (step < Player.NO_REGISTERS) {
                        makeProgramFieldsVisible(step);
                        board.setStep(step);
                        board.setCurrentPlayer(board.getPlayer(0));
                    } else {
                        for (Player player: players) {
                            player.setRebooting(false);
                        }
                        startProgrammingPhase();
                    }
                }
            } else {
                // this should not happen
                assert false;
            }
        } else {
            // this should not happen
            assert false;
        }
    }

    private void displayWinner(){

        //board.setPhase(Phase.INITIALISATION);
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "The winner is Player " + winner.getPlayerNum());
        alert.setTitle("WINNER FOUND");
        ImageView imageView;
        System.out.println(Connection.getPlayerToken());
        if (Connection.getPlayerToken() == null || Connection.getPlayerToken().equals(winner.getName())){
            Image image = new Image("https://upload.wikimedia.org/wikipedia/commons/thumb/3/33/Twemoji_1f60e.svg/1024px-Twemoji_1f60e.svg.png");
            imageView = new ImageView(image);
            imageView.setFitHeight(64);
            imageView.setFitWidth(64);
        } else {
            Image image = new Image("https://upload.wikimedia.org/wikipedia/commons/thumb/4/42/Emojione_1F62D.svg/64px-Emojione_1F62D.svg.png");
            imageView = new ImageView(image);
        }
        alert.setGraphic(imageView);
        alert.showAndWait();

        System.exit(0);
    }

    /**
     * Execute a command on a player
     * <p>
     * This method executed the parsed command on the parsed player.
     * @param  player  The player on which the command should be executed
     * @param  command The command that should be executed on the player.
     */
    private void executeCommand(@NotNull Player player, Command command) {
        if (player != null && player.board == board && command != null) {
            // XXX This is a very simplistic way of dealing with some basic cards and
            //     their execution. This should eventually be done in a more elegant way
            //     (this concerns the way cards are modelled as well as the way they are executed).

            switch (command) {
                case FORWARD:
                    this.moveForward(player);
                    break;
                case RIGHT:
                    this.turnRight(player);
                    break;
                case LEFT:
                    this.turnLeft(player);
                    break;
                case FAST_FORWARD:
                    this.fastForward(player, 2);
                    break;
                case MOVE_THREE:
                    this.fastForward(player, 3);
                    break;
                case UTURN:
                    this.uturn(player);
                    break;
                case POWERUP:
                    this.powerup(player);
                    break;
                case MOVE_BACK:
                    this.backup(player);
                    break;
                case AGAIN:
                    this.again(player);
                    break;
                case CHOOSETURN:
                    break;
                case SPAM:
                    removeOneCardWithCommand(player.getDiscardPile(),Command.SPAM);
                    break;
                default:
                    // DO NOTHING (for now)
            }
        }
    }

    public void multiThreadExecute(int step){
        ExecutorService executor = Executors.newFixedThreadPool(board.getPlayers().size());

        for (Player player : board.getPlayers()) {
            executor.execute(() -> executeBoardElement(player, step));
        }
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            System.out.println("Exception: " + e);
        }
    }

    private void executeBoardElement(Player player, int step) {
        if(player.getSpace() == null){
            return;
        }
        for (FieldObject object : player.getSpace().getObjects()) {
            if (object instanceof Conveyor) {
                if (((Conveyor) object).getCOLOR().equals(Color.BLUE)) {
                    moveBoardElement(player, object);
                    for (FieldObject object2 : player.getSpace().getObjects()) {
                        moveBoardElement(player, object2);
                    }
                } else if (((Conveyor) object).getCOLOR().equals(Color.GREEN)) {
                    moveBoardElement(player, object);
                }
            }

            if(object instanceof CheckpointField cp){
                if(cp.playerHasCheckpoint(player)){
                    return;
                }
                ArrayList<CheckpointField> cps = board.getCheckpoints();
                int obtainedCheckpoints = (int)cps.stream().filter(c -> c.playerHasCheckpoint(player)).count();

                if(cp.getCheckpointNumber()-1 == obtainedCheckpoints){
                    cp.addPlayerIfUnobtained(player);

                    if(obtainedCheckpoints+1 == cps.size()){
                        //Player won!
                        winnerFound = true;
                        winner = player;

                        //ALERT DOESN'T WORK AS IT IS NOT IN JAVA FX THREAD
                        // FIX FIX FIX

                    }
                }
            }
            if (object instanceof Gear gear){
                if (gear.getDirection() == Direction.LEFT){
                    turnLeft(player);
                } else if (gear.getDirection() == Direction.RIGHT) {
                    turnRight(player);
                }
            }
            if (object instanceof Laser laser){
                Space space = player.getSpace();
                if (laser.getTYPE().equals("SHOT")) {
                    if (testIfLaserIsBlocked(space.x, space.y, laser)){
                        System.out.println("Space is blocked");
                    } else {
                        player.getDiscardPile().add(new CommandCard(Command.SPAM));
                        System.out.println("Player " + player.getPlayerNum() + " got hit");
                    }
                } else {
                    player.getDiscardPile().add(new CommandCard(Command.SPAM));
                    System.out.println("Player " + player.getPlayerNum() + " got hit");
                }
            }
            if (object instanceof PushPanel pp){
                for (int i=0;i<pp.getActivation().length;i++){
                    if (step==pp.getActivation()[i]){
                        moveBoardElement(player,pp);
                    }
                }
            }
        }
    }

    public boolean testIfLaserIsBlocked(int x, int y, Laser laser){
        int[] cords = getNewCoordinates(laser.getDirection(),x,y,true);
        Space space = board.getSpace(cords[0],cords[1]);
        /*
        #TODO Find out why space is null
         */
        if(space == null){
            return false;
        }
        System.out.println(space.x + space.y);
        for (FieldObject object : space.getObjects()){
            if (object instanceof Laser l){
                if (l.getTYPE().equals("EMITER") && space.getPlayer() == null){
                    return false;
                }
            }
        }
        if (space.getPlayer() == null) {
            return testIfLaserIsBlocked(cords[0], cords[1],laser);
        } else {
            return true;
        }
    }

    /**
     * Moves the player one spaces forward
     * <p>
     * This method moves the player one spaces forward, if the target space is valid.
     * @param  player  the player which will be moved.
     */
    public void moveForward(@NotNull Player player) {
        Space currentSpace=player.getSpace();
        int x=currentSpace.x;
        int y=currentSpace.y;
        // Husk outofbounds fejl
        int[] newCoordinates = getNewCoordinates(player.getHeading(),x,y,false);
        x = newCoordinates[0];
        y = newCoordinates[1];
        System.out.println(x+ " " +y);
        if(board.getSpace(x,y) != null) {
            boolean backupflag = false;
            moveCurrentPlayerToSpace(board.getSpace(x, y), backupflag, player, null, false);
        } else {
            System.out.println("OUT OF BOUNDS");
            reboot(player);
        }
    }

    /**
     * Reboots a player, adding two spam cards to their discard pile. Not finished yet, it doesn't set the
     * players position to a reboot field.
     * @param player, the player that needs to be rebooted
     */
    private void reboot(Player player){
        int x = board.getRebootField().getX();
        int y = board.getRebootField().getY();
        player.setHeading(board.getRebootField().getDirection());

        if (board.getSpace(x,y).getPlayer() != null){
            moveForward(board.getSpace(x,y).getPlayer());
        }

        player.setSpace(board.getSpace(x, y));

        player.setRebooting(true);
        player.getDiscardPile().add(new CommandCard(Command.SPAM));
        player.getDiscardPile().add(new CommandCard(Command.SPAM));
    }

    /**
     * Makes a player interact with a movementField
     * <p>
     * This method makes the player interact with a movement field
     * @param  player  the player which will interact with the field
     * @param fieldObject the object which the player will interact with
     */
    private void moveBoardElement(@NotNull Player player, FieldObject fieldObject) {
        Space currentSpace=player.getSpace();
        int x=currentSpace.x;
        int y=currentSpace.y;
        boolean backupflag = false;
        // Husk outofbounds fejl
        int[] newCoordinates = getNewCoordinates(((MovementField)fieldObject).getDirection(),x,y,backupflag);
        x = newCoordinates[0];
        y = newCoordinates[1];
        //System.out.println(x+ " " +y);
        if(board.getSpace(x,y) != null) {
            moveCurrentPlayerToSpace(board.getSpace(x, y), backupflag, player, ((MovementField)fieldObject).getDirection(), true);
        } else {
            reboot(player);
            System.out.println("OUT OF BOUNDS");
        }

    }

    /**
     * Moves the player two spaces forward
     * <p>
     * This method moves the player two spaces forward
     * @param  player  the player which will be moved.
     */
    public void fastForward(@NotNull Player player, int moves) {
        for (int i = 0; i < moves; i++) {
            if (!player.getRebooting()) {
                moveForward(player);
            }
        }
    }

    /**
     * Turns the player right.
     * <p>
     * This method rotates the 90 degrees right, relative to the players heading.
     * @param  player  the player which will be rotated.
     */
    public void turnRight(@NotNull Player player) {
        player.setHeading(player.getHeading().next());
    }

    /**
     * Turns the player left.
     * <p>
     * This method rotates the 90 degrees left, relative to the players heading.
     * @param  player  the player which will be rotated.
     */
    public void turnLeft(@NotNull Player player) {
        player.setHeading(player.getHeading().prev());
    }


    /**
     * Turns the player to the opposition direction.
     * <p>
     * This method rotates the player 180 degrees.
     * @param  player  the player which will be rotated.
     */
    public void uturn(@NotNull Player player){
        player.setHeading(player.getHeading().next());
        player.setHeading(player.getHeading().next());
    }

    /**
     * Adds one energy to the player's energy balance
     * <p>
     * This method adds one energy to the player's energy balance
     * @param  player  the player which energy balance will be increased with one.
     */
    public void powerup (@NotNull Player player){
        player.setEnergy(player.getEnergy()+1);
    }

    /**
     * Moves the player one slot back.
     * <p>
     * This method executes moves the player one position in the opposite the direction,
     * that the player is facing. If the destination space is not valid, the player will not move.
     * @param  player  the player which will move one space back.
     */

    public void backup (@NotNull Player player){
        Space currentSpace=player.getSpace();
        int x=currentSpace.x;
        int y=currentSpace.y;
        boolean backupflag = true;
        // Husk outofbounds fejl
        int[] newCoordinates = getNewCoordinates(player.getHeading(),x,y,backupflag);
        x = newCoordinates[0];
        y = newCoordinates[1];
        System.out.println(x+ " " +y);
        if(board.getSpace(x,y) != null) {
            moveCurrentPlayerToSpace(board.getSpace(x, y), backupflag ,player, null, false);
        } else{
            reboot(player);
            System.out.println("OUT OF BOUNDS");
        }
    }

    /**
     * Repeats last command card
     * <p>
     * This method executes the players last used command card.
     * If there was no program field before the current field, no card will execute.
     * @param  player  the player which will repeat and execute the last card
     */
    public void again (@NotNull Player player){
        int step = board.getStep()-1;
        if (step > -1) {
            CommandCard card = player.getProgramField(step).getCard();
            Command command = card.command;
            executeCommand(player, command);
        }
    }


    /**
     * Moves a card from one card field to another
     * <p>
     * This method moves a card from a card field to an empty card field
     * If the source card field is empty or the target card field occupied,
     * the method will return false, indicating the move operation was invalid.
     * The method will return true, if the card is moved successfully.
     * @param  source  the source command card field
     * @param  target  the target command card field
     * @return boolean, if the move operation was successful.
     */
    public boolean moveCards(@NotNull CommandCardField source, @NotNull CommandCardField target) {
        CommandCard sourceCard = source.getCard();
        CommandCard targetCard = target.getCard();
        if (sourceCard != null && targetCard == null) {
            target.setCard(sourceCard);
            source.setCard(null);
            return true;
        } else {
            return false;
        }
    }


    /**
     * Executes a command that is selected by the player and resumes execution afterwards.
     * <p>
     * This method always executes the parsed command given by the command parameter.
     * Afterwards the method continues execution of the rest of the registers by
     * setting the phase to ACTIVATION_PHASE. The game continues execution with/without
     * step mode as when the card interaction card was reached.
     * @param  command  the player-chosen command
     */
    public void executeCommandOptionAndContinue(Command command){
        executeCommand(board.getCurrentPlayer(),command);
        int step = board.getStep();
        if (isOnline) {
            String comm = ClientConsume.conn.userID + ":" +  step + ":" + "Done" + ":" + command.displayName;
            ClientConsume.sendInteractiveCommand(ClientConsume.conn.gameSession.gameID, ClientConsume.conn.userID, comm);
        }
        board.setPhase(Phase.ACTIVATION);
        int nextPlayerNumber = board.getPlayerNumber(board.getCurrentPlayer()) + 1;
        if (nextPlayerNumber < board.getPlayersNumber()) {
            board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));
        } else {
            step++;
            if (step < Player.NO_REGISTERS) {
                makeProgramFieldsVisible(step);
                board.setStep(step);
                board.setCurrentPlayer(board.getPlayer(0));
            } else {
                startProgrammingPhase();
                return;
            }
        }
        continuePrograms();
    }
}
