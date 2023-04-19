package dk.dtu.compute.se.pisd.roborally.model;

import java.util.ArrayList;

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
