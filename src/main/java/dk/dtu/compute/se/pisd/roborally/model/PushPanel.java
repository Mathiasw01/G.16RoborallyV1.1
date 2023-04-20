package dk.dtu.compute.se.pisd.roborally.model;

public class PushPanel extends MovementField{
    public int[] getActivation() {
        return activation;
    }

    int[] activation;
    public PushPanel(Heading direction, int[] activation) {
        super(direction);
        this.activation=activation;
    }
}
