package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameControllerTest {

    private final int TEST_WIDTH = 13;
    private final int TEST_HEIGHT = 10;

    private GameController gameController;


    @BeforeEach
    void setUp() {
        Board board = new Board(TEST_WIDTH, TEST_HEIGHT, "src/main/java/dk/dtu/compute/se/pisd/roborally/Maps/DizzyHighway");
        gameController = new GameController(board);
        for (int i = 0; i < 6; i++) {
            Player player = new Player(board, null,"Player " + i);
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
        gameController.moveCurrentPlayerToSpace(board.getSpace(0, 4), false, player1, null);
        Assertions.assertEquals(player1, board.getSpace(0, 4).getPlayer(), "Player " + player1.getName() + " should beSpace (0,4)!");

        //Test original field empty
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");

        //Test set player turn
        gameController.board.setCurrentPlayer(player2);
        Assertions.assertEquals(player2, board.getCurrentPlayer(), "Current player should be " + player2.getName() +"!");

        //Test player 2 pushing player 1
        player2.setHeading(Heading.EAST);
        gameController.moveCurrentPlayerToSpace(board.getSpace(0, 4), false, player2, null);

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

}