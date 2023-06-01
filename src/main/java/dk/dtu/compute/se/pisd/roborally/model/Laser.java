package dk.dtu.compute.se.pisd.roborally.model;

import com.google.gson.annotations.Expose;

public class Laser extends FieldObject{

    @Expose
    private final Heading direction;
    @Expose
    private final String TYPE;

    public Laser(Heading direction, String TYPE) {
        this.direction = direction;
        this.TYPE = TYPE;
    }

    public Heading getDirection(){
        return direction;
    }
    public String getTYPE(){
        return TYPE;
    }
}
