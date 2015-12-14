import org.json.simple.JSONObject;
import org.calorycounter.shared.Constants;
import nioserver.AbstractNIOServer;
import nioserver.Message;

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