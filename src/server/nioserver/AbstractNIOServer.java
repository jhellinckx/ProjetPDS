package nioserver;
import org.calorycounter.shared.Constants;

import org.json.simple.JSONObject;
import java.io.IOException;

public abstract class AbstractNIOServer{
	private StreamController _controller;
	private Boolean _running;

	public AbstractNIOServer(){
		this._controller = new StreamController(this);
		this._running = new Boolean(false);
	}

	public void send(Message msg){
		this._controller.addOutgoingMessage(msg);
	}

	public void listen(){
		while(this.isRunning()){
			try{
				Message msg = this._controller.receiveIncomingMessage();
				this.handleMessage(msg);
			}
			catch(InterruptedException e){
				System.out.println(e.getMessage());
			}
		}
	}

	public abstract void handleMessage(Message msg);

	public void run(){
		this.setRunning();
		_controller.startStreams();
		Runtime.getRuntime().addShutdownHook(new Thread(new ServerShutdownHook(this, this._controller)));
		System.out.println("Server " + Constants.color.GREEN + Constants.network.HOST + Constants.color.RESET
			+" launched. Listening on port " + Constants.color.YELLOW + Constants.network.PORT + Constants.color.RESET + ".");
		this.listen();
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
		this._controller.stopStreams();
	}
}