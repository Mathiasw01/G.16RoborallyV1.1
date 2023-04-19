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

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.model.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import org.jetbrains.annotations.NotNull;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class SpaceView extends StackPane implements ViewObserver {

    final public static int SPACE_HEIGHT = 45; // 60; // 75;
    final public static int SPACE_WIDTH = 45;  // 60; // 75;

    public final Space space;


    public SpaceView(@NotNull Space space) {
        this.space = space;

        // XXX the following styling should better be done with styles
        this.setPrefWidth(SPACE_WIDTH);
        this.setMinWidth(SPACE_WIDTH);
        this.setMaxWidth(SPACE_WIDTH);

        this.setPrefHeight(SPACE_HEIGHT);
        this.setMinHeight(SPACE_HEIGHT);
        this.setMaxHeight(SPACE_HEIGHT);

        if ((space.x + space.y) % 2 == 0) {
            this.setStyle("-fx-background-color: white;");
        } else {
            this.setStyle("-fx-background-color: black;");
        }



        // updatePlayer();

        // This space view should listen to changes of the space
        space.attach(this);
        update(space);
    }

    private void updatePlayer() {

        Player player = space.getPlayer();
        if (player != null) {
            Polygon arrow = new Polygon(0.0, 0.0,
                    10.0, 20.0,
                    20.0, 0.0 );
            try {
                arrow.setFill(Color.valueOf(player.getColor()));
            } catch (Exception e) {
                arrow.setFill(Color.MEDIUMPURPLE);
            }

            arrow.setRotate((90*player.getHeading().ordinal())%360);

            this.getChildren().add(arrow);
        }

    }


    private void drawFieldObjects(){
        Wall wall = (Wall)space.findObjectOfType(Wall.class);
        Conveyor conveyor = (Conveyor)space.findObjectOfType(Conveyor.class);
        if( wall != null) {
            Rectangle wallGfx = new Rectangle();
            wallGfx.setWidth(47);
            wallGfx.setHeight(10);

            switch (wall.getDir()) {
                case SOUTH:
                    wallGfx.setTranslateY(20);
                    break;
                case NORTH:
                    wallGfx.setTranslateY(-20);
                    break;
                case EAST:
                    wallGfx.setRotate(90);
                    wallGfx.setTranslateX(20);
                    break;
                case WEST:
                    wallGfx.setRotate(90);
                    wallGfx.setTranslateX(-20);
                    break;
            }

            wallGfx.setFill(Color.MEDIUMPURPLE);

            this.getChildren().add(wallGfx);
        }


        //Checkpoints
        CheckpointField checkpoint = (CheckpointField) space.findObjectOfType(CheckpointField.class);
        if(checkpoint != null){
            Circle cpGfx = new Circle();
            cpGfx.setRadius(15);
            cpGfx.setFill(Color.CORAL);
            this.getChildren().add(cpGfx);
        }

        //Finish
        FinishField finishField = (FinishField) space.findObjectOfType(FinishField.class);

        if(finishField != null){
            Circle cpGfx = new Circle();
            cpGfx.setRadius(16);
            cpGfx.setFill(Color.RED);
            this.getChildren().add(cpGfx);
        }

        //Start
        StartField startField = (StartField) space.findObjectOfType(StartField.class);

        if(startField != null){
            Circle cpGfx = new Circle();
            cpGfx.setRadius(16);
            cpGfx.setFill(Color.GOLD);
            this.getChildren().add(cpGfx);
        }
        if (conveyor != null) {
            Rectangle conveyorGfx = new Rectangle();
            conveyorGfx.setWidth(25);
            conveyorGfx.setHeight(47);
            Circle convCircleGfx = new Circle();
            convCircleGfx.setRadius(2);
            convCircleGfx.setFill(Color.YELLOW);
            switch (conveyor.getDirection()) {
                case SOUTH:
                    convCircleGfx.setTranslateY(15);
                    break;
                case NORTH:
                    convCircleGfx.setTranslateY(-15);
                    break;
                case EAST:
                    conveyorGfx.setRotate(90);
                    convCircleGfx.setTranslateX(15);
                    break;
                case WEST:
                    conveyorGfx.setRotate(90);
                    convCircleGfx.setTranslateX(-15);
                    break;
            }
            if (conveyor.getColor().equals(Color.BLUE)) {
                conveyorGfx.setFill(Color.BLUE);
            }else {
                conveyorGfx.setFill(Color.GREEN);
            }

            this.getChildren().add(conveyorGfx);
            this.getChildren().add(convCircleGfx);
        }
    }




    @Override
    public void updateView(Subject subject) {
        this.getChildren().clear();

        drawFieldObjects();

        if (subject == this.space) {
            updatePlayer();
        }

    }

}
