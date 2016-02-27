import org.json.simple.JSONObject;

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
            System.err.println("Connection Error: " + e.getMessage());
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
    public void notifyObserver(String msg){
        for (Observer obs : listObserver){
            obs.update(msg);
        }
    }

    public void sendToNetwork(JSONObject msg){
        try{
            channel.write(msg);
        } catch (IOException e){
            System.err.println("Sending Error: " + e.getMessage());
        }
    }

    public String getFromNetwork(){
        try{
            return channel.read();
        } catch (IOException e){
            System.err.println("Getting Error: " + e.getMessage());
            return null;
        }
    }
}
