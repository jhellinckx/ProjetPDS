import java.io.IOException;
import java.net.Socket;

/**
 * Created by aurelien on 27/02/16.
 */
public class ClientSocket implements NetworkChannel{
    private static final int port = 8080;
    private static final String hostaddress = "10.0.1.29";
    private Socket socketClient;

    @Override
    public void initiateConnection() throws IOException{
        System.out.println("Connection to " + hostaddress+":"+port);
        socketClient = new Socket(hostaddress, port);
        System.out.println("Connection Established");
    }

    @Override
    public String read(){

        return null;

    }

    @Override
    public void write(String msg){

    }

}
