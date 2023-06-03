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
package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class RoboRallyMenuBar extends MenuBar {

    private AppController appController;

    private Menu controlMenu;
    private Menu mapMenu;
    private MenuItem testMap;
    private MenuItem dizzy;
    private MenuItem octane;

    private MenuItem saveGame;

    private MenuItem newGame;

    private MenuItem loadGame;
    private MenuItem startMulti;

    private MenuItem stopGame;

    private MenuItem exitApp;
    private String map = "src/main/java/dk/dtu/compute/se/pisd/roborally/Maps/testMap";

    public RoboRallyMenuBar(AppController appController) {
        this.appController = appController;

        controlMenu = new Menu("File");
        this.getMenus().add(controlMenu);

        mapMenu = new Menu("Select Map");
        this.getMenus().add(mapMenu);

        testMap = new MenuItem("Test Map");
        testMap.setOnAction(e -> {
            this.map = "src/main/java/dk/dtu/compute/se/pisd/roborally/Maps/testMap";
            mapMenu.setText("Test Map");
        });
        mapMenu.getItems().add(testMap);

        dizzy = new MenuItem("Dizzy Highway");
        dizzy.setOnAction(e -> {
            mapMenu.setText("Dizzy Highway");
            this.map = "src/main/java/dk/dtu/compute/se/pisd/roborally/Maps/DizzyHighway";
        });
        mapMenu.getItems().add(dizzy);

        octane = new MenuItem("High Octane");
        octane.setOnAction(e -> {
            mapMenu.setText("High Octane");
            this.map = "src/main/java/dk/dtu/compute/se/pisd/roborally/Maps/High Octane";
        });
        mapMenu.getItems().add(octane);

        newGame = new MenuItem("New Game");
        newGame.setOnAction( e ->  this.appController.newGame(map));
        controlMenu.getItems().add(newGame);

        stopGame = new MenuItem("Stop Game");
        stopGame.setOnAction( e -> this.appController.stopGame());
        controlMenu.getItems().add(stopGame);

        saveGame = new MenuItem("Save Game");
        saveGame.setOnAction( e -> this.appController.saveGame());
        controlMenu.getItems().add(saveGame);

        loadGame = new MenuItem("Load Game");
        loadGame.setOnAction( e -> this.appController.loadGame());
        controlMenu.getItems().add(loadGame);

        startMulti = new MenuItem("Start multiplayer");
        startMulti.setOnAction( e -> this.appController.loadFromServer());
        controlMenu.getItems().add(startMulti);

        exitApp = new MenuItem("Exit");
        exitApp.setOnAction( e -> this.appController.exit());
        controlMenu.getItems().add(exitApp);

        controlMenu.setOnShowing(e -> update());
        controlMenu.setOnShown(e -> this.updateBounds());
        update();
    }

    public void update() {
        if (appController.isGameRunning()) {
            newGame.setVisible(false);
            stopGame.setVisible(true);
            saveGame.setVisible(true);
            loadGame.setVisible(false);
        } else {
            newGame.setVisible(true);
            stopGame.setVisible(false);
            saveGame.setVisible(false);
            loadGame.setVisible(true);
        }
    }

}
