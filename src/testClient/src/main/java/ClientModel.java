import java.io.IOException;
import java.util.ArrayList;


public class ClientModel  implements Observable{

    private ArrayList<Observer> listObserver = new ArrayList<Observer>();
    private NetworkChannel channel;

    public ClientModel(){
        channel = new ClientSocket();
        try {
            channel.initiateConnection();
        } catch (IOException e){
            System.out.println(e.getStackTrace());
        }
    }

    @Override
    public void addObserver(Observer obs){
        listObserver.add(obs);
    }

    @Override
    public void removeObserver(Observer obs) {
        listObserver.remove(obs);
    }

    @Override
    public void notifyObserver(){
        for (Observer obs : listObserver){
            obs.update();
        }
    }
}
