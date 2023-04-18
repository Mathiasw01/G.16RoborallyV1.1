package dk.dtu.compute.se.pisd.roborally.model;

public class Wall extends FieldObject{
    private final Heading dir;
    public Wall(Heading dir){
        this.dir = dir;

    }

    public Heading getDir() {
        return dir;
    }
}
