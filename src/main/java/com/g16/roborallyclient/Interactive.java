package com.g16.roborallyclient;

public class Interactive {

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public boolean isChosen() {
        return chosen;
    }

    public void setChosen(boolean chosen) {
        this.chosen = chosen;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    private int userID;

    private String step;

    private boolean chosen;

    private String command;

    public Interactive(int userID, String step, boolean chosen, String command){
        this.userID = userID;
        this.step = step;
        this.chosen = chosen;
        this.command = command;
    }

    public Interactive(){
        this.userID = 0;
        this.step = null;
        this.chosen = false;
        this.command = null;
    }


}
