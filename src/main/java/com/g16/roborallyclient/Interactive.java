package com.g16.roborallyclient;

public class Interactive {

    private String userID;

    private String step;

    private boolean chosen;

    private String command;

    public Interactive(String ID, String step, boolean chosen, String command){
        this.userID = ID;
        this.step = step;
        this.chosen = chosen;
        this.command = command;
    }


    public String getUserID() {
        return userID;
    }

    public String getStep() {
        return step;
    }

    public boolean isChosen() {
        return chosen;
    }

    public String getCommand(){return command;}
}
