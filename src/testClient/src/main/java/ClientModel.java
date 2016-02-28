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
            notifyObserver(">>> Client request: " + msg.toJSONString());
            channel.write(msg);
        } catch (IOException e){
            System.err.println("Sending Error: " + e.getMessage());
        }
    }

    public void getFromNetwork(){
        try{
            String msg = channel.read();
            notifyObserver("<<< Server response: " + msg);
        } catch (IOException e){
            System.err.println("Getting Error: " + e.getMessage());
        }
    }

    public void closeChannel(){
        try {
            channel.terminateConnection();
        } catch (IOException e){
            System.err.println("Socket Closing Error: " + e.getMessage());
        }
    }
}
