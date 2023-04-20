package dk.dtu.compute.se.pisd.roborally.model;


import com.google.gson.annotations.Expose;

/**
 * Movement field
 *<p>
 * A special field, which moves the player on player interaction with the field.
 *
 */

public abstract class MovementField extends FieldObject {
    @Expose
    private Heading direction;

    public MovementField(Heading direction) {
        this.direction = direction;
    }


    public Heading getDirection() {
        return direction;
    }
}
