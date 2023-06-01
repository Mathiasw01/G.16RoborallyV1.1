package dk.dtu.compute.se.pisd.roborally.model;
import com.google.gson.annotations.Expose;


public class RebootField extends MovementField{
    @Expose
    private final int x;
    @Expose
    private final int y;
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
