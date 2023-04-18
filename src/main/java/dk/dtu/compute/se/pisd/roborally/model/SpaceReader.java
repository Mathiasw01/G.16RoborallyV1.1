package dk.dtu.compute.se.pisd.roborally.model;

import com.sun.jdi.IntegerValue;

import java.io.File;
import java.io.FileNotFoundException;
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
                switch (result[0]){
                    case "Wall":
                        Space space = board.getSpace(Integer.parseInt(result[1]),Integer.parseInt(result[2]));
                        space.addObjects(new Wall(Heading.valueOf(result[3])));
                }
            }
        } catch (FileNotFoundException e){
            System.out.println("File not found");
        }
    }
}
