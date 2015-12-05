import org.json.simple.JSONObject;
import nioserver.Message;
import nioserver.AbstractNIOServer;
import nioserver.Constants;

public class AppliServer extends AbstractNIOServer{

	public AppliServer(){
		super();
	}

	public void handleMessage(Message msg){
		// Treat the received message.
		this.send(msg);
	}

	public static void main(String[] args){
		try{
			AppliServer appserver = new AppliServer();
			appserver.run();
		} 
		catch(Exception e){
			System.out.println(Constants.errorMessage("Uncaught exception : "
				+e.getMessage(), new String("root")));
			e.printStackTrace();
		}
	}
}