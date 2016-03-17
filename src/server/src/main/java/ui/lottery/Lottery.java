package ui.lottery;

import ui.action.UserAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Lottery {

    private static final int THRESHOLD = 2;

    private HashMap<String, Integer> participants;              // A user will be selected randomly from this map.
    private HashMap<Integer, String> participants_id;           // By choosing a number in this one.

    public Lottery(){
        participants = new HashMap<>();
        participants_id = new HashMap<>();
    }

    private List<String> constructPossibleWinnerWeightedList(String name, int number_of_notes){     // Construct a list containing one possible winner name. This
        ArrayList<String> possible_winner_weighted_list = new ArrayList();  // list.size() can be > 1 if the participant has more chances to win.
        int weight = number_of_notes/THRESHOLD;
        for (int i = 0; i < weight; i++){
            possible_winner_weighted_list.add(name);
        }
        return possible_winner_weighted_list;
    }

    private List<String> constructPossibleWinnersList(){            // Construct a list containing the possible winners.
        ArrayList<String> possible_winners = new ArrayList<>();
        for (Integer i : participants_id.keySet()){
            String participant = participants_id.get(i);
            int number_of_notes = participants.get(participant);
            if (number_of_notes >= THRESHOLD){
                possible_winners.addAll(constructPossibleWinnerWeightedList(participant, number_of_notes));
            }
        }
        return possible_winners;
    }

    private String selectAWinner(List<String> possible_winners_list){
        Random rand = new Random();
        if (possible_winners_list.size() > 0){
            int index = rand.nextInt(possible_winners_list.size());
            return possible_winners_list.get(index);
        }
        else{
            return null;
        }

    }

    private void clearParticipants(){
        participants = new HashMap<>();
        participants_id = new HashMap<>();
    }

    public void updateParticipants(String name, UserAction action){
        int value = 0;
        if (participants.containsKey(name)){
            value += participants.get(name);
        }
        else{
            participants_id.put(participants_id.size()+1, name);
        }
        value += action.getAssociatedValue();
        participants.put(name, value);
    }

    public String getWinner(){
        List<String> possible_winners_list = constructPossibleWinnersList();
        String winner = selectAWinner(possible_winners_list);
        clearParticipants();
        return winner;
    }


}
