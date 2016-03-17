package ui.lottery;

import ui.lottery.LotteryController;
import ui.mvc.ServerModel;
import ui.windows.LotteryWindow;

import javax.swing.SwingUtilities;
import java.util.Observable;
import java.util.Observer;

public class LotteryView implements Observer, LotteryWindow.WinnerListener{

    private LotteryController controller;
    private LotteryWindow window;

    public LotteryView(LotteryController controller){
        this.controller = controller;
        window = new LotteryWindow(this);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                window.setVisible(true);
            }
        });
    }

    @Override
    public void onWinnerRequest(){
        controller.findWinner();
    }

    @Override
    public void update(Observable o, Object arg){
        ServerModel model = (ServerModel) o;
        Boolean participant_chosen = (Boolean) arg;
        if (participant_chosen.booleanValue()){
            String name = model.getChosenParticipantName();
            (new WinnerThread(name)).start();
        }
    }

    private class WinnerThread extends Thread {
        private String winner;

        public WinnerThread(String winner){
            this.winner = winner;
        }

        @Override
        public void run(){
            window.displayWinner(winner);
        }
    }
}
