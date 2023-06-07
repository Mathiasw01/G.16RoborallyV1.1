package dk.dtu.compute.se.pisd.roborally.controller;

import com.g16.roborallyclient.ClientConsume;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import dk.dtu.compute.se.pisd.roborally.model.*;
import java.io.*;

public class SaveLoadController {

    /**
     * A runtime type adapter which maps classes to strings. This makes the gson builder able
     * to (de)serialize lists of objects inheriting from the FieldObject class and still maintain
     * information of which subclass the object is an instance of.
     */

    private static final RuntimeTypeAdapterFactory<FieldObject> runtimeTypeAdapterFactory = RuntimeTypeAdapterFactory
            .of(FieldObject.class, "type")
            .registerSubtype(Wall.class, "wall")
            .registerSubtype(StartField.class, "startField")
            .registerSubtype(CheckpointField.class, "checkPoint")
            .registerSubtype(Gear.class, "gear")
            .registerSubtype(Conveyor.class, "conveyor")
            .registerSubtype(Laser.class, "laser")
            .registerSubtype(RebootField.class, "rebootField");

    private static final Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .setPrettyPrinting()
            .registerTypeAdapterFactory(runtimeTypeAdapterFactory)
            .create();

    /**
     * Serialize the game controller and save it to a file
     * <p>
     * This method serializes a game controller and serializes its state to a JSON-formatted file.
     * This is useful for saving the game state and can be deserialized to load the game in the future
     * @param  gc the game controller to be serialized
     * @param filePath the path to the file, in which the JSON will be saved
     */

    public static void serializeAndSave(GameController gc, String filePath) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
        writer.write(serializeGameController(gc));
        writer.close();
        System.out.println("Saved game to " + filePath);

    }

    private static String serializeGameController(GameController gc){
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .setPrettyPrinting()
                .registerTypeAdapterFactory(runtimeTypeAdapterFactory)
                .create();

        return gson.toJson(gc);
    }

    public static void serializeAndSaveToServer(GameController gc, String fileName) throws IOException {

        ClientConsume.saveGame(fileName, serializeGameController(gc));

    }




    /**
     * Deserialize a JSON formatted file to a GameController object
     * <p>
     * This method deserializes a JSON-formatted savefile to a game controller object.
     * The game controller object is returned and can be used for initializing a game from a previous game state.
     * @param filePath the path to the JSON-formatted savefile
     * @return a GameController object which contains the board and the game state.
     */
    public static GameController deserializeAndLoad(String filePath){
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            String everything = sb.toString();
            br.close();

            return gson.fromJson(everything, GameController.class);
        }catch (IOException ioe){
            System.out.println("Couldn't load file.. ");
        }
        return null;
    }

    public static GameController deserializeString(String str){
        return gson.fromJson(str, GameController.class);
    }
}
