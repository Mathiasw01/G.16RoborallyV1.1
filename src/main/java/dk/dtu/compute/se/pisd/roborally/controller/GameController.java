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

import com.google.gson.annotations.Expose;
import dk.dtu.compute.se.pisd.roborally.model.*;
import javafx.scene.control.Alert;
import javafx.scene.paint.Color;
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


    public GameController(@NotNull Board board) {
        this.board = board;
    }

    /**
     * Moves current player to space if possible
     * <p>
     * Moves current player to the parsed space if the space is empty
     */
    public void moveCurrentPlayerToSpace(@NotNull Space space, boolean backupflag, Player player, Heading conveyorHeading) {
        board.setCounter(board.getCounter() + 1);
        Wall wall = (Wall) space.findObjectOfType(Wall.class);
        Wall currentSpaceWall = (Wall) player.getSpace().findObjectOfType(Wall.class);

        if (wall != null ){
           if (backupflag & wall.getDir() == player.getHeading().next().next()) {
                System.out.println("wall");
                return;
            }
        }

        if (currentSpaceWall != null){
            if (!backupflag & currentSpaceWall.getDir() == player.getHeading()){
                return;
            } else if (backupflag & currentSpaceWall.getDir() == player.getHeading().next().next()) {
                return;
            }
        }

        if (space.getPlayer() == null) {
            player.setSpace(space);
        } else {
            Player player2 = space.getPlayer();
            Wall player2CurrenSpaceWall = (Wall) player2.getSpace().findObjectOfType(Wall.class);
            int x = space.x;
            int y = space.y;

                if (backupflag) {
                    switch (player.getHeading()){
                        case EAST -> {x--;}
                        case WEST -> {x++;}
                        case NORTH -> {y++;}
                        case SOUTH -> {y--;}
                    }
                } else if (conveyorHeading == null){
                    if (player2CurrenSpaceWall != null) {
                        if (player.getHeading() != player2CurrenSpaceWall.getDir()) {
                            switch (player.getHeading()) {
                                case EAST -> {
                                    x++;
                                }
                                case WEST -> {
                                    x--;
                                }
                                case NORTH -> {
                                    y--;
                                }
                                case SOUTH -> {
                                    y++;
                                }
                            }
                        } else {
                            return;
                        }
                    }else {
                        switch (player.getHeading()){
                            case EAST -> {x++;}
                            case WEST -> {x--;}
                            case NORTH -> {y--;}
                            case SOUTH -> {y++;}
                        }
                    }
                } else {
                    switch (conveyorHeading){
                        case EAST -> {x++;}
                        case WEST -> {x--;}
                        case NORTH -> {y--;}
                        case SOUTH -> {y++;}
                    }
                } if (board.getSpace(x,y) != null) {
                    player2.setSpace(board.getSpace(x, y));
                    player.setSpace(space);
                }
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
                    /*
                    field.setCard(generateRandomCommandCard());
                    field.setVisible(true);
                     */
                    field.setCard(drawCard(board.getCurrentPlayer().getProgrammingDeck(),player));
                    field.setVisible(true);

                }
            }
        }
    }

    // XXX: V2
    private CommandCard generateRandomCommandCard() {
        Command[] commands = Command.values();
        int random = (int) (Math.random() * commands.length);
        return new CommandCard(commands[random]);
    }

    public CommandCard drawCard(List<CommandCard> deck, Player currentPLayer) {
        if (currentPLayer.getProgrammingDeck().isEmpty()) {
            shuffleDeck(currentPLayer.getProgrammingDeck(),currentPLayer.getDiscardpile());
        }
        CommandCard topCard = currentPLayer.getProgrammingDeck().get(0);
        currentPLayer.getProgrammingDeck().remove(0);
        discardCard(currentPLayer,topCard);
        if (topCard==null){
            drawCard(currentPLayer.getProgrammingDeck(),currentPLayer);
        }
        return topCard;
    }

    public void discardCard(Player player, CommandCard card) {
        player.getDiscardpile().add(card);
    }

    public void shuffleDeck(List<CommandCard> deck, List<CommandCard> discardPile) {
        deck.addAll(discardPile);
        discardPile.clear();
        Collections.shuffle(deck);
    }


    /**
     * Finish programming phase and start activation phase
     * <p>
     * Finished programming phase by locking the progam fields and makes the programmed card invisble except for
     * the card in register 0. Afterwards changes the game phase to acivation phase and sets the current
     * player and step to index 0
     */
    public void finishProgrammingPhase() {
        makeProgramFieldsInvisible();
        makeProgramFieldsVisible(0);
        board.setPhase(Phase.ACTIVATION);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);
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
                CommandCard card = currentPlayer.getProgramField(step).getCard();
                if (card != null) {
                    Command command = card.command;
                    if (command.isInteractive()){
                        board.setPhase(Phase.PLAYER_INTERACTION);
                        return;
                    }
                    executeCommand(currentPlayer, command);
                }
                int nextPlayerNumber = board.getPlayerNumber(currentPlayer) + 1;
                if (nextPlayerNumber < board.getPlayersNumber()) {
                    board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));
                } else {
                    multiThreadExecute(step);
                    step++;
                    if (step < Player.NO_REGISTERS) {
                        makeProgramFieldsVisible(step);
                        board.setStep(step);
                        board.setCurrentPlayer(board.getPlayer(0));
                    } else {
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
        for (FieldObject object : player.getSpace().getObjects()) {
            if (object instanceof Conveyor) {
                if (((Conveyor) object).getColor().equals(Color.BLUE)) {
                    moveBoardElement(player, object);
                    for (FieldObject object2 : player.getSpace().getObjects()) {
                        moveBoardElement(player, object2);
                    }
                } else if (((Conveyor) object).getColor().equals(Color.GREEN)) {
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
                        System.out.println(player.getName() + " won!");
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
            if (object instanceof PushPanel pp){
                for (int i=0;i<pp.getActivation().length;i++){
                    if (step==pp.getActivation()[i]){
                        moveBoardElement(player,pp);
                    }
                }
            }
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
        switch (player.getHeading()){
            case EAST -> {x++;}
            case WEST -> {x--;}
            case NORTH -> {y--;}
            case SOUTH -> {y++;}
        }
        System.out.println(x+ " " +y);
        if(board.getSpace(x,y) != null) {
            boolean backupflag = false;
            moveCurrentPlayerToSpace(board.getSpace(x, y), backupflag, player, null);
        } else {

            System.out.println("OUT OF BOUNDS");
        }
    }

    private void reboot(Player player){
        player.getDiscardpile().add(new CommandCard(Command.SPAM));
        player.getDiscardpile().add(new CommandCard(Command.SPAM));

    }

    private void moveBoardElement(@NotNull Player player, FieldObject fieldObject) {
        Space currentSpace=player.getSpace();
        int x=currentSpace.x;
        int y=currentSpace.y;
        // Husk outofbounds fejl
        switch (((MovementField)fieldObject).getDirection()){
            case EAST -> {x++;}
            case WEST -> {x--;}
            case NORTH -> {y--;}
            case SOUTH -> {y++;}
        }
        System.out.println(x+ " " +y);
        boolean backupflag = false;
        if(board.getSpace(x,y) != null) {
            moveCurrentPlayerToSpace(board.getSpace(x, y), backupflag, player, ((MovementField)fieldObject).getDirection());
        } else System.out.println("OUT OF BOUNDS");
    }

    /**
     * Moves the player two spaces forward
     * <p>
     * This method moves the player two spaces forward
     * @param  player  the player which will be moved.
     */
    public void fastForward(@NotNull Player player, int moves) {
        for (int i = 0; i < moves; i++) {
            moveForward(player);
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
        // Husk outofbounds fejl
        switch (player.getHeading()){
            case EAST -> {x--;}
            case WEST -> {x++;}
            case NORTH -> {y++;}
            case SOUTH -> {y--;}
        }
        System.out.println(x+ " " +y);
        if(board.getSpace(x,y) != null) {
            boolean backupflag = true;
            moveCurrentPlayerToSpace(board.getSpace(x, y), backupflag ,player, null);
        } else System.out.println("OUT OF BOUNDS");
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
        board.setPhase(Phase.ACTIVATION);
        int step = board.getStep();
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
