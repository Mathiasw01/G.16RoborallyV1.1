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

    public static void serializeAndSave(GameController gc, String filePath) throws IOException {
        int width = gc.board.width;
        int height = gc.board.height;

        RuntimeTypeAdapterFactory<FieldObject> runtimeTypeAdapterFactory = RuntimeTypeAdapterFactory
                .of(FieldObject.class, "type")
                .registerSubtype(Wall.class, "wall")
                .registerSubtype(StartField.class, "startField")
                .registerSubtype(CheckpointField.class, "checkPoint")
                .registerSubtype(Gear.class, "gear")
                .registerSubtype(Conveyor.class, "conveyor");


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

        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
        writer.write(gson.toJson(gc));
        writer.close();

        writer.close();

    }
}
