import org.json.simple.JSONObject;
import java.io.IOException;

public class Server{
	private StreamController _controller;
	private Boolean _running;

	public Server(){
		this._controller = new StreamController(this);
		this._running = new Boolean(false);
	}

	public void send(Message msg){
		this._controller.addOutgoingMessage(msg);
	}

	public void listen(){
		while(this.isRunning() || this._controller.messageAvailable()){
			try{
				Message msg = this._controller.receiveIncomingMessage();
				this.handleMessage(msg);
			}
			catch(InterruptedException e){
				System.out.println(Constants.errorMessage(e.getMessage(), this));
			}
		}
	}

	public void handleMessage(Message msg){
		// Treat the received message.
		this.send(msg);
	}

	public void run(){
		this.setRunning();
		_controller.startStreams();
		System.out.println("Server " + Constants.OC_GREEN + Constants.LOCALHOST + Constants.OC_RESET
			+" launched. Listening on port " + Constants.OC_YELLOW + Constants.PORT + Constants.OC_RESET + ".");
		this.listen();
	}

	public static void main(String[] args){
		try{
			Server server = new Server();
			server.run();
		} 
		catch(Exception e){
			System.out.println(Constants.errorMessage("Uncatched exception : "
				+e.getMessage(), new String("root")));
			e.printStackTrace();
		}
	}

	public void setRunning() { 
		synchronized(this._running){
			this._running = true; 
		}
	}

	public boolean isRunning() { 
		synchronized(this._running){
			return this._running; 
		}
	}

	public void stop() { 
		synchronized(this._running){
			this._running = false;
		}
		this._controller.wakeupIn();
	}
}