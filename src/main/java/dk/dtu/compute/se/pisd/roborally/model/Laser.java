package dk.dtu.compute.se.pisd.roborally.model;

public class Laser extends FieldObject{

    private final Heading direction;
    private String type;

    public Laser(Heading direction, String type) {
        this.direction = direction;
        this.type = type;
    }

    public Heading getDirection(){
        return direction;
    }
    public String getType(){
        return type;
    }
}
