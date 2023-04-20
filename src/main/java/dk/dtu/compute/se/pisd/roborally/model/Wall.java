package dk.dtu.compute.se.pisd.roborally.model;


/**
 * Wall object
 *<p>
 * A wall object, that blocks players movement. Has a direction on which side of the space
 * the wall is placed.
 */
public class Wall extends FieldObject{
    private final Heading dir;
    public Wall(Heading dir){
        this.dir = dir;

    }

    public Heading getDir() {
        return dir;
    }
}
