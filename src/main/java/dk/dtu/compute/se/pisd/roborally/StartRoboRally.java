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
import javafx.scene.control.Alert;
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

    public static void main(String[] args) {
        System.out.println(System.getProperty("user.dir"));
        //localOrOnline();
        InitRoboRally.main(args);
    }
}

