import java.net.ConnectException;
import org.json.simple.JSONObject;
import java.net.*;
import java.io.*;
import org.calorycounter.shared.Constants;
import java.net.InetAddress;

public class Test{
	public static void main(String[] args){

		try{
			Socket client = new Socket(Constants.network.HOST, Constants.network.PORT);
			DataOutputStream out = new DataOutputStream(client.getOutputStream());
			JSONObject greetings = new JSONObject();
			greetings.put("greetings","slt sa va??");
			byte[] data = greetings.toString().getBytes(Constants.network.ENCODING);
			out.writeInt(data.length);
			out.write(data, 0, data.length);
			DataInputStream in = new DataInputStream(client.getInputStream());
			int size = in.readInt();
			byte[] inMsg = new byte[size];
			in.read(inMsg, 0, size);
			System.out.println("Server response : "+new String(inMsg,Constants.network.ENCODING));
		}
		catch(ConnectException e){
			System.out.println("Could not connect to " + Constants.color.GREEN + Constants.network.HOST + Constants.color.RESET
			+":" + Constants.color.YELLOW + Constants.network.PORT + Constants.color.RESET + ". Server maybe offline ?");
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
}