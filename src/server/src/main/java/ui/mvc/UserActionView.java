package ui.mvc;

import ui.windows.MessageWindow;

import javax.swing.SwingUtilities;
import java.util.Observable;
import java.util.Observer;

public class UserActionView implements Observer{

    private MessageWindow window;

    public UserActionView(){
        window = new MessageWindow();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                window.setVisible(true);
            }
        });
    }

    private synchronized void updateWindow(String message){
        window.setMessage(message);
    }

    @Override
    public void update(Observable o, Object arg){
        ServerModel model = (ServerModel) o;
        String message = model.getMessage();
        (new MessageThread(message)).start();
    }

    private class MessageThread extends Thread {
        private String message;

        public MessageThread(String message){
            this.message = message;
        }

        @Override
        public void run(){
            updateWindow(message);
        }
    }
}
