import org.json.simple.JSONObject;
import nioserver.Message;
import nioserver.AbstractNIOServer;
import nioserver.Constants;

public class CalorieCounterServer extends AbstractNIOServer{

	public CalorieCounterServer(){
		super();
	}

	public void handleMessage(Message msg){
		// Treat the received message.
		this.send(msg);
	}

	public static void main(String[] args){
		try{
			CalorieCounterServer ccserver = new CalorieCounterServer();
			ccserver.run();
		} 
		catch(Exception e){
			System.out.println(Constants.errorMessage("Uncaught exception : "
				+e.getMessage(), new String("root")));
			e.printStackTrace();
		}
	}
}