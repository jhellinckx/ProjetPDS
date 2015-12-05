import java.net.ConnectException;
import org.json.simple.JSONObject;
import java.net.*;
import java.io.*;
import nioserver.Constants;

public class Test{
	public static void main(String[] args){
		try{
			Socket client = new Socket(Constants.HOST, Constants.PORT);
			DataOutputStream out = new DataOutputStream(client.getOutputStream());
			JSONObject greetings = new JSONObject();
			greetings.put("greetings","slt sa va??");
			byte[] data = greetings.toString().getBytes(Constants.ENCODING);
			out.writeInt(data.length);
			out.write(data, 0, data.length);
			DataInputStream in = new DataInputStream(client.getInputStream());
			int size = in.readInt();
			byte[] inMsg = new byte[size];
			in.read(inMsg, 0, size);
			System.out.println("Server response : "+new String(inMsg,Constants.ENCODING));
		}
		catch(ConnectException e){
			System.out.println("Could not connect to " + Constants.OC_GREEN + Constants.HOST + Constants.OC_RESET
			+":" + Constants.OC_YELLOW + Constants.PORT + Constants.OC_RESET + ". Server maybe offline ?");
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
}