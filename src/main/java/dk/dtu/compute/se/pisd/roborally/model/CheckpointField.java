package dk.dtu.compute.se.pisd.roborally.model;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;


/**
 * Checkpoint field object
 *<p>
 * A checkpoint on with which the player can interact to obtain it. The player must obtain
 * checkpoints in order. E.g. checkpoint with checkpointNumber 1 must be collected first.
 * A checkpoint can only be obtained once. If a player obtains all checkpoints, the player will win the game.
 */
public class CheckpointField extends FieldObject{


    @Expose
    private ArrayList<Player> playersObtained;

    @Expose
    private int checkpointNumber;

    public CheckpointField(int checkpointNumber){
        playersObtained = new ArrayList<>();
        this.checkpointNumber = checkpointNumber;
    }

    // Hvad gør dette
    public boolean addPlayerIfUnobtained(Player player){
        if(playersObtained.contains(player)){
            return false;
        }

        playersObtained.add(player);
        return true;
    }

    /**
     * Gets a list of the players who has obtained the checkpoint
     * <p>
     * Returns an array list of the players who have obtained this checkpoint
     * @return Arraylist of the players who have obtained the
     */
    public ArrayList<Player> getPlayersObtained(){
        return this.playersObtained;
    }

    /**
     * Returns whether a player has the checkpoint
     * <p>
     * Returns true/false depending on if the player has obtained the checkpoint
     * @param player the player which for the condition will be checked
     * @return True if Player has checkpoint
     */
    public boolean playerHasCheckpoint(Player player){
        return playersObtained.contains(player);
    }


    /**
     * Get checkpoint number
     * <p>
     * Returns the number of the checkpoint
     * The number is also the index of the order in which the checkpoints need to be obtained
     * @return The number of the checkpoint
     */
    public int getCheckpointNumber(){
        return this.checkpointNumber;
    }

}
