package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

class GameControllerTest {

    private final int TEST_WIDTH = 13;
    private final int TEST_HEIGHT = 10;

    private GameController gameController;


    @BeforeEach
    void setUp() {
        Board board = new Board(TEST_WIDTH, TEST_HEIGHT, "src/main/java/dk/dtu/compute/se/pisd/roborally/Maps/DizzyHighway");
        gameController = new GameController(board, false);
        ProgrammingDeckInit programmingDeckInit = new ProgrammingDeckInit();
        for (int i = 0; i < 6; i++) {
            Player player = new Player(board, null, "Player " + i, i + 1, programmingDeckInit.init());
            board.addPlayer(player);
            player.setSpace(board.getSpace(i, i));
            player.setHeading(Heading.values()[i % Heading.values().length]);
        }
        board.setCurrentPlayer(board.getPlayer(0));
    }


    @AfterEach
    void tearDown() {
        gameController = null;
    }

    @Test
    void moveCurrentPlayerToSpace() {
        Board board = gameController.board;
        Player player1 = board.getPlayer(0);
        Player player2 = board.getPlayer(1);


        //Test moving player 1
        gameController.moveCurrentPlayerToSpace(board.getSpace(0, 4), false, player1, null, false);
        Assertions.assertEquals(player1, board.getSpace(0, 4).getPlayer(), "Player " + player1.getName() + " should beSpace (0,4)!");

        //Test original field empty
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");

        //Test set player turn
        gameController.board.setCurrentPlayer(player2);
        Assertions.assertEquals(player2, board.getCurrentPlayer(), "Current player should be " + player2.getName() + "!");

        //Test player 2 pushing player 1
        player2.setHeading(Heading.EAST);
        gameController.moveCurrentPlayerToSpace(board.getSpace(0, 4), false, player2, null, false);

        //Test player 2 source field empty
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");

        //Test player 2 position, test player 1
        Assertions.assertEquals(player2, board.getSpace(0, 4).getPlayer(), "Player " + player2.getName() + " should beSpace (0,4)!");
        Assertions.assertEquals(player1, board.getSpace(1, 4).getPlayer(), "Player " + player1.getName() + " should beSpace (0,4)!");


    }

    @Test
    void moveForward() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        gameController.moveForward(current);

        Assertions.assertEquals(current, board.getSpace(0, 1).getPlayer(), "Player " + current.getName() + " should beSpace (0,1)!");
        Assertions.assertEquals(Heading.SOUTH, current.getHeading(), "Player 0 should be heading SOUTH!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
    }

    @Test
    void executeCommand() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        gameController.executeCommand(current, Command.MOVE_THREE);
        gameController.executeCommand(current, Command.UTURN);

        //Test move three
        Assertions.assertEquals(current,
                gameController.board.getSpace(0, 3).getPlayer(), "Player" + current.getName() + "should beSpace(0,3)!");
        //Test Uturn
        Assertions.assertEquals(Heading.NORTH, current.getHeading(), "Player 0 should be heading SOUTH!");
        //Test MOVE_BACK
        gameController.executeCommand(current, Command.MOVE_BACK);
        Assertions.assertEquals(current,
                gameController.board.getSpace(0, 4).getPlayer(), "Player" + current.getName() + "should beSpace(0,4)!");

    }

    @Test
    void reboot() {
        Board board = gameController.board;
        Player currentPlayer = board.getCurrentPlayer();
        currentPlayer.setSpace(board.getSpace(0, 0));
        currentPlayer.setHeading(Heading.WEST);
        gameController.executeCommand(currentPlayer, Command.FORWARD);
        // Tests if a player gets teleported to the reboot field after being rebooted
        Assertions.assertEquals(board.getRebootField().getY(),currentPlayer.getSpace().y);
        Assertions.assertEquals(board.getRebootField().getX(),currentPlayer.getSpace().x );
        // Tests if a player get set as rebooting after being rebooted
        Assertions.assertEquals( true,currentPlayer.getRebooting());
        //Tests if a player can execute commands after being rebooted
        gameController.executeCommand(currentPlayer, Command.RIGHT);
        Assertions.assertEquals( Heading.WEST,currentPlayer.getHeading());
    }

    @Test
    void laser() {
        gameController.moveCurrentPlayerToSpace(gameController.board.getSpace(6, 4), false, gameController.board.getCurrentPlayer(), gameController.board.getCurrentPlayer().getHeading(), false);
        gameController.executeBoardElement(gameController.board.getCurrentPlayer(),gameController.board.getStep());
        Iterator<CommandCard> discardIterator = gameController.board.getCurrentPlayer().getDiscardPile().iterator();
        int noOfSpamcards = 0;
        while (discardIterator.hasNext()) {
            CommandCard card = discardIterator.next();
            if (card.command == Command.SPAM) {
                noOfSpamcards++;
            }
        }
        // tests if a player get a spam card in their discard pile when they get hit by laser
        Assertions.assertEquals(1,noOfSpamcards);
    }

    @Test
    void conveyor(){
        gameController.moveCurrentPlayerToSpace(gameController.board.getSpace(4,0),false,
                gameController.board.getCurrentPlayer(),gameController.board.getCurrentPlayer().getHeading(),true);
        System.out.println(gameController.board.getCurrentPlayer().getSpace().x);
        System.out.println(gameController.board.getCurrentPlayer().getSpace().y);
        gameController.executeBoardElement(gameController.board.getCurrentPlayer(),gameController.board.getStep());
        //Tests if a conveyor moves a player correctly
        Assertions.assertEquals(gameController.board.getSpace(4,2),gameController.board.getCurrentPlayer().getSpace());
    }

    @Test
    void walls(){
        gameController.board.getCurrentPlayer().setSpace(gameController.board.getSpace(1,2));
        gameController.board.getCurrentPlayer().setHeading(Heading.NORTH);
        gameController.executeCommand(gameController.board.getCurrentPlayer(),Command.FORWARD);
        //Tests if a player moves when trying to go through a wall
        Assertions.assertEquals(gameController.board.getSpace(1,2),gameController.board.getCurrentPlayer().getSpace());
    }


    @Test
    void gears(){
        gameController.board.getCurrentPlayer().setSpace(gameController.board.getSpace(5,2));
        gameController.board.getCurrentPlayer().setHeading(Heading.EAST);
        gameController.executeBoardElement(gameController.board.getCurrentPlayer(),gameController.board.getStep());
        Assertions.assertEquals(Heading.SOUTH,gameController.board.getCurrentPlayer().getHeading());
    }

    @Test
    void laserIsblocked(){
        gameController.moveCurrentPlayerToSpace(gameController.board.getSpace(6, 4), false, gameController.board.getCurrentPlayer(), gameController.board.getCurrentPlayer().getHeading(), false);
        gameController.board.getPlayer(1).setSpace(gameController.board.getSpace(6,3));
        gameController.executeBoardElement(gameController.board.getCurrentPlayer(),gameController.board.getStep());
        gameController.executeBoardElement(gameController.board.getPlayer(1),gameController.board.getStep());
        Iterator<CommandCard> discardIterator = gameController.board.getCurrentPlayer().getDiscardPile().iterator();
        int noOfSpamcards = 0;
        while (discardIterator.hasNext()) {
            CommandCard card = discardIterator.next();
            if (card.command == Command.SPAM) {
                noOfSpamcards++;
            }
        }
        // Tests if a player doesnt get hit when the laser is block
        Assertions.assertEquals(0,noOfSpamcards);

        Iterator<CommandCard> discardIterator2 = gameController.board.getPlayer(1).getDiscardPile().iterator();
        int noOfSpamcards2 = 0;
        while (discardIterator2.hasNext()) {
            CommandCard card = discardIterator2.next();
            if (card.command == Command.SPAM) {
                noOfSpamcards2++;
            }
        }
        // Tests if the player blocking the laser gets hit, and get a spam card in their discard pile.
        Assertions.assertEquals(1,noOfSpamcards2);
    }





}
