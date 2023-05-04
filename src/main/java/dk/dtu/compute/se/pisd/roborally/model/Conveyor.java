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

    public Paint getColor() {
        return color;
    }



    private Color color;


    @Expose
    private final boolean isDouble;

    public Conveyor(Color color, Heading heading) {
        super(heading);
        this.color=color;
        this.isDouble = color.equals(Color.BLUE);

    }


}
