import org.json.simple.JSONObject;
import java.net.*;
import java.io.*;

public class Test{
	public static void main(String[] args){
		try{
			Socket client = new Socket(Constants.LOCALHOST, Constants.PORT);
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
			client.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
 
	}
}