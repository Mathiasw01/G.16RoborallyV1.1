package dk.dtu.compute.se.pisd.roborally.model;

public abstract class MovementField extends FieldObject {
    private Heading direction;

    public MovementField(Heading direction) {
        this.direction = direction;
    }


    public Heading getDirection() {
        return direction;
    }
}
