package dk.dtu.compute.se.pisd.roborally.model;

import com.google.gson.annotations.Expose;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 * Conveyor field
 *<p>
 * An special type of movement field which moves the player in conveyors direction on interaction.
 * Moves one or two spaces depending on the color
 *
 */

public class Conveyor extends MovementField {


    private final Color COLOR;
    @Expose
    private final boolean isDouble;

    public Conveyor(Color COLOR, Heading heading) {
        super(heading);
        this.COLOR = COLOR;
        this.isDouble = COLOR.equals(Color.BLUE);
    }

    public Color getCOLOR() {
        return COLOR;
    }

    public boolean isDouble(){
        return isDouble;
    }


}
