package ui.mvc;

import ui.lottery.LotteryController;
import ui.lottery.LotteryView;

public class MVCBuilder {

    private static ServerModel model;
    private static LotteryController controller;
    private static UserActionView user_action_view;
    private static LotteryView timer_view;

    public static void buildMVC(){
        model = new ServerModel();
        controller = new LotteryController(model);
        user_action_view = new UserActionView();
        timer_view = new LotteryView(controller);
        model.addObserver(timer_view);
        model.addObserver(user_action_view);
    }

    public static ServerModel getModelInstance(){
        return model;
    }
}
