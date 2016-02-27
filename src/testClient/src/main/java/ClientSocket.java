import org.json.simple.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.io.UnsupportedEncodingException;

import static org.calorycounter.shared.Constants.network.ENCODING;


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
    public String read() throws IOException{
        DataInputStream instream = new DataInputStream(socketClient.getInputStream());
        try{
            int msgLength = instream.readInt();
            byte[] rawMsg = new byte[msgLength];
            int bytesRead = instream.read(rawMsg, 0, msgLength);
            if(bytesRead != msgLength)
                throw new IOException("could not read a message of given size.");
            String msg = new String(rawMsg, ENCODING);
            return msg;
        }
        catch(UnsupportedEncodingException e){
            System.err.println("Encoding Error: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void write(JSONObject msg) throws IOException{
        DataOutputStream outstream = new DataOutputStream(socketClient.getOutputStream());
        try{
            byte[] rawMsg = msg.toString().getBytes(ENCODING);
            outstream.writeInt(rawMsg.length);
            outstream.write(rawMsg, 0, rawMsg.length);
        }
        catch(UnsupportedEncodingException e){
            System.err.println("Encoding Error: " + e.getMessage());
        }

    }

}