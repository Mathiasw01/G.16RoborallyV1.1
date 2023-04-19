package dk.dtu.compute.se.pisd.roborally.model;

import com.sun.jdi.IntegerValue;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class SpaceReader {
    String fileName;

    public SpaceReader(String fileName){
        this.fileName = fileName;
    }

    public void initMap(Board board){
        try{
            File map = new File(fileName);
            Scanner myReader = new Scanner(map);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                String[] result = data.split(";");
                Space space = board.getSpace(0,0);
                if (!result[0].equals("#")) {
                    space = board.getSpace(Integer.parseInt(result[1]), Integer.parseInt(result[2]));
                }
                switch (result[0]){
                    case "#":
                        break;
                    case "P":
                        space.addObjects(new StartField());
                        break;
                    case "Wall":
                        space.addObjects(new Wall(Heading.valueOf(result[3])));
                        break;
                    case "CheckPoint":
                        space.addObjects(new CheckpointField());
                        break;
                    case "BlueCon":
                        space.addObjects(new Conveyor(Color.BLUE,Heading.valueOf(result[3])));
                        break;
                    case "GreenCon":
                        space.addObjects(new Conveyor(Color.GREEN,Heading.valueOf(result[3])));
                        break;

                }
            }
        } catch (FileNotFoundException e){
            System.out.println("File not found");
        }
    }

    public void initPlayers(Board board, Player player, int playerNum){
        try{
            String data = Files.readAllLines(Paths.get(fileName)).get(playerNum);
            String[] result = data.split(";");

            player.setSpace(board.getSpace(Integer.parseInt(result[1]), Integer.parseInt(result[2])));
        } catch (FileNotFoundException e){
            System.out.println("File not found");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
