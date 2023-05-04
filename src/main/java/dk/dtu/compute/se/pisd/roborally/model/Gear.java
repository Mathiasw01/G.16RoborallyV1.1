package dk.dtu.compute.se.pisd.roborally.model;

import com.google.gson.annotations.Expose;

public class Gear extends FieldObject{

    @Expose
    private final Direction dir;



    public Gear(Direction dir) {
        this.dir = dir;


    }

    public Direction getDirection(){
        return dir;
    }
}
