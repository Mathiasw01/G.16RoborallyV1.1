package dk.dtu.compute.se.pisd.roborally.model;

import com.sun.jdi.IntegerValue;

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
                Space space = board.getSpace(Integer.parseInt(result[1]),Integer.parseInt(result[2]));
                switch (result[0]) {
                    case "P" -> space.addObjects(new StartField());
                    case "Wall" -> {
                        space.addObjects(new Wall(Heading.valueOf(result[3])));
                    }
                    case "CheckPoint" -> {
                        CheckpointField newCheckpoint = new CheckpointField(Integer.parseInt(result[3]));
                        space.addObjects(newCheckpoint);
                        board.addCheckpoint(newCheckpoint);
                    }
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
