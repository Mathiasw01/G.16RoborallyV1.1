package com.g16.roborallyclient;

public class Interactive {

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
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

    private String userID;

    private String step;

    private boolean chosen;

    private String command;

    public Interactive(String userID, String step, boolean chosen, String command){
        this.userID = userID;
        this.step = step;
        this.chosen = chosen;
        this.command = command;
    }

    public Interactive(){
        this.userID = null;
        this.step = null;
        this.chosen = false;
        this.command = null;
    }


}
