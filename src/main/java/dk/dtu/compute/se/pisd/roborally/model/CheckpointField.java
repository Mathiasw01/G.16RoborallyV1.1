package dk.dtu.compute.se.pisd.roborally.model;

import java.util.ArrayList;


/**
 * Checkpoint field object
 *<p>
 * A checkpoint on with which the player can interact to obtain it. The player must obtain
 * checkpoints in order. E.g. checkpoint with checkpointNumber 1 must be collected first.
 * A checkpoint can only be obtained once. If a player obtains all checkpoints, the player will win the game.
 */
public class CheckpointField extends FieldObject{
    private ArrayList<Player> playersObtained;
    private int checkpointNumber;

    public CheckpointField(int checkpointNumber){
        playersObtained = new ArrayList<>();
        this.checkpointNumber = checkpointNumber;
    }

    public boolean addPlayerIfUnobtained(Player player){
        if(playersObtained.contains(player)){
            return false;
        }

        playersObtained.add(player);
        return true;
    }

    public boolean playerHasCheckpoint(Player player){
        return playersObtained.contains(player);
    }

    public int getCheckpointNumber(){
        return this.checkpointNumber;
    }

}
