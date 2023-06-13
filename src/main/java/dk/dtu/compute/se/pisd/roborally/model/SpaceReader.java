package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.roborally.controller.SaveLoadController;
import javafx.scene.paint.Color;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Space Reader
 * <p>
 * Class that reads a map file and places down objects on the board
 *
 */

public class SpaceReader {
    final String fileName;
    private boolean isCsv;

    public SpaceReader(String fileName){
        this.fileName = fileName;
        try {
            File map = new File(fileName);
            Scanner myReader = new Scanner(map);
            if(myReader.hasNextLine()){
                String line = myReader.nextLine();
                isCsv = line.toCharArray()[0] != '[';
                return;
            }

        }
         catch (Exception e){
            System.out.println("Couldn't read map file");
        }
        isCsv =false;
    }

    /**
     * Initializes the map by reading a map file and places down objects to the board
     * @param board the board where the objects are placed
     */

    public void initMap(Board board){
        if(isCsv){
            initMapFromCSV(board);
        }else {
            initMapFromJSON(board);
        }

    }

    public void initMapFromCSV(Board board){
        try{
            File map = new File(fileName);
            Scanner myReader = new Scanner(map);
            int lineNum = 0;
            while (myReader.hasNextLine()) {
                lineNum += 1;
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
                    case "BlueCon":
                        space.addObjects(new Conveyor(Color.BLUE,Heading.valueOf(result[3])));
                        break;
                    case "GreenCon":
                        space.addObjects(new Conveyor(Color.GREEN,Heading.valueOf(result[3])));
                        break;
                    case "Gear":
                        space.addObjects(new Gear(Direction.valueOf(result[3])));
                        break;
                    case "Laser":
                        space.addObjects(new Laser(Heading.valueOf(result[3]), result[4]));
                        break;
                    case "Reboot":
                        board.setRebootField(new RebootField(Heading.valueOf(result[3]),Integer.parseInt(result[1]),Integer.parseInt(result[2])));
                        space.addObjects(new RebootField(Heading.valueOf(result[3]),Integer.parseInt(result[1]),Integer.parseInt(result[2])));
                        break;
                    case "PushPanel":
                        space.addObjects(new PushPanel(Heading.valueOf(result[3]), new int[]{2, 4}));
                        break;
                    case "CheckPoint":
                        CheckpointField newCheckpoint = new CheckpointField(Integer.parseInt(result[3]));
                        space.addObjects(newCheckpoint);
                        board.addCheckpoint(newCheckpoint);
                        break;
                    default:
                        System.out.println("Unknown object in " + fileName);
                        System.out.println("On line " +lineNum);
                        break;
                }
            }
        } catch (FileNotFoundException e){
            System.out.println("File not found");
        }
    }

    public void initMapFromJSON(Board board){
        try{
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            String mapJson = sb.toString();
            br.close();

            Space[][] spaces = SaveLoadController.deserializeSpacesFromString(mapJson);


            for (Space[] space : spaces) {
                for (Space savedSpace : space) {
                    Space targetSpace = board.getSpace(savedSpace.x, savedSpace.y);

                    for (FieldObject spaceObject : savedSpace.getObjects()) {
                        if (spaceObject instanceof Conveyor conveyor) {
                            if (conveyor.isDouble()) {
                                targetSpace.addObjects(new Conveyor(Color.BLUE, conveyor.getDirection()));
                            } else {
                                targetSpace.addObjects(new Conveyor(Color.ORANGE, conveyor.getDirection()));
                            }
                        } else if (spaceObject instanceof StartField) {
                            targetSpace.addObjects(new StartField());
                        } else if (spaceObject instanceof CheckpointField cp) {
                            CheckpointField cpf = new CheckpointField(cp.getCheckpointNumber());
                            targetSpace.addObjects(cpf);
                            board.addCheckpoint(cpf);
                        } else if (spaceObject instanceof Gear gear) {
                            targetSpace.addObjects(new Gear(gear.getDirection()));
                        } else if (spaceObject instanceof Wall wall) {
                            targetSpace.addObjects(new Wall(wall.getDir()));
                        } else if (spaceObject instanceof Laser laser) {
                            targetSpace.addObjects(new Laser(laser.getDirection(), laser.getTYPE()));
                        } else if (spaceObject instanceof RebootField rebootField) {
                            rebootField = new RebootField(rebootField.getDirection(), rebootField.getX(), rebootField.getY());
                            targetSpace.addObjects(rebootField);
                            board.setRebootField(rebootField);
                        }
                    }
                }
            }
        } catch (IOException e){
            System.out.println("File not found");
        }
    }

    /**
     * Initializes the player by placing them on the start fields
     * @param board the board where the players are placed
     * @param player the player which receives a space from the map file
     * @param playerNum the players number which determines their order
     */
    public void initPlayers(Board board, Player player, int playerNum){
        if(!isCsv){
            int i = 0;
            for(Space[] spaceArr : board.getSpaces()){
                for(Space space  : spaceArr){
                    if(space.getObjects().stream().anyMatch(fo -> fo instanceof StartField)) {
                        if(i==playerNum) {
                            player.setSpace(space);
                            return;
                        }
                        i++;
                    }

                }
            }
            return;
        }

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
