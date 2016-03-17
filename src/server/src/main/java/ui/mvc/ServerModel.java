package ui.mvc;

import ui.action.UserAction;
import ui.action.ActionIdContainer;
import ui.lottery.Lottery;

import java.util.Observable;

public class ServerModel extends Observable {

    private static final String PARTICIPANT_CHOSEN = "Un participant a été désigné ...";

    private String message;
    private Boolean participant_needed;
    private Lottery lottery;

    public ServerModel(){
        participant_needed = false;
        lottery = new Lottery();
    }

    public void addParticipants(String name, UserAction action){
        if (action.isIdEqualsTo(ActionIdContainer.RATING)){
            lottery.updateParticipants(name, action);
        }
    }

    public void setMessage(String name, UserAction action, String ending){
        this.message = name + " " + action.getId() + " " + action.getAssociatedValue() + " " + ending;
        addParticipants(name, action);
        setChanged();
        notifyObservers(participant_needed);
    }

    public void setMessage(String name, String state){
        this.message = name + " " + state;
        setChanged();
        notifyObservers(participant_needed);
    }

    public void setParticipantNeeded(){
        participant_needed = true;
        this.message = PARTICIPANT_CHOSEN;
        setChanged();
        notifyObservers(participant_needed);
    }

    public String getMessage(){
        return message;
    }

    public String getChosenParticipantName(){
        String participant_name = lottery.getWinner();
        participant_needed = false;             // As soon as a participant is chosen, the boolean is reset to false.
        return participant_name;
    }
}
