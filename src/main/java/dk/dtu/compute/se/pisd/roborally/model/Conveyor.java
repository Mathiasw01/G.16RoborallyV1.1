package dk.dtu.compute.se.pisd.roborally.model;

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

    public Conveyor(Color color, Heading heading) {
        super(heading);
        this.color=color;
    }


}
