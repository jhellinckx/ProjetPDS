package ui.lottery;

import ui.mvc.ServerModel;

public class LotteryController {
    ServerModel model;

    public LotteryController(ServerModel model){
        this.model = model;
    }

    public void findWinner(){
        model.setParticipantNeeded();
    }
}
