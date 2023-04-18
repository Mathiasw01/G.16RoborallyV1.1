package dk.dtu.compute.se.pisd.roborally.model;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;



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
