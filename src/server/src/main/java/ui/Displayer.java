package ui;

import ui.action.UserAction;
import ui.mvc.ServerModel;
import ui.mvc.MVCBuilder;

public class Displayer {

    private static final String WORK_IN_PROGRESS = "calculs des prédictions en cours";

    private ServerModel server;
    private static Displayer instance;


    private Displayer(){

    }

    private void checkServer(){
        if (server == null){
            MVCBuilder.buildMVC();
            server = MVCBuilder.getModelInstance();
        }
    }

    public static Displayer getInstance(){
        if (instance == null){
            return new Displayer();
        }
        return instance;
    }

    public void displayUserAction(String username, UserAction action, String ending){
        checkServer();
        server.setMessage(username, action, ending);

    }

    public void displayWorkInProgressForUser(String username){
        checkServer();
        server.setMessage(username, WORK_IN_PROGRESS);
    }
}
