package dk.dtu.compute.se.pisd.roborally.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import dk.dtu.compute.se.pisd.roborally.model.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import java.io.*;

public class SaveLoadController {

    private static final RuntimeTypeAdapterFactory<FieldObject> runtimeTypeAdapterFactory = RuntimeTypeAdapterFactory
            .of(FieldObject.class, "type")
            .registerSubtype(Wall.class, "wall")
            .registerSubtype(StartField.class, "startField")
            .registerSubtype(CheckpointField.class, "checkPoint")
            .registerSubtype(Gear.class, "gear")
            .registerSubtype(Conveyor.class, "conveyor");

    private static Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .setPrettyPrinting()
            .registerTypeAdapterFactory(runtimeTypeAdapterFactory)
            .create();
    public static void serializeAndSave(GameController gc, String filePath) throws IOException {





        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .setPrettyPrinting()
                .registerTypeAdapterFactory(runtimeTypeAdapterFactory)
                .create();


        /*
        for(int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
                jsonString.append(gson.toJson(gc.board.getSpace(x, y)));
            }
        }
         */
        String jso = gson.toJson(gc);

        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
        writer.write(jso);
        writer.close();
        System.out.println("Saved game to " + filePath);

    }

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

            GameController fromJson = gson.fromJson(everything, GameController.class);
            return fromJson;
        }catch (IOException ioe){
            System.out.println("Couldnt load file.. ");
        }
        return null;



    }
}
