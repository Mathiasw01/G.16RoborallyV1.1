package dk.dtu.compute.se.pisd.roborally.model;

public class RebootField extends MovementField{
    private   int x;



    private int y;
    public RebootField(Heading direction, int x, int y) {
        super(direction);
        this.x=x;
        this.y=y;
    }
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

}
