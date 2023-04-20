package dk.dtu.compute.se.pisd.roborally.model;

public class Gear extends FieldObject{
    private final Direction dir;

    public Gear(Direction dir) {
        this.dir = dir;
    }

    public Direction getDirection(){
        return dir;
    }
}
