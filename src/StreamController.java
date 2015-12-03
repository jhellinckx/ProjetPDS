import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.json.simple.JSONObject;

public class StreamController{
	private InStream _inStream;
	private OutStream _outStream;
	private List<Message> _in;
	private List<Message> _out;
	private Server _server;


	public StreamController(Server server){
		this._inStream = new InStream(this);
		this._outStream = new OutStream(this);
		this._in = new LinkedList<Message>();
		this._out = new LinkedList<Message>();
		this._server = server;
	}

	public void startStreams(){
		(new Thread(this._inStream)).start();
		(new Thread(this._outStream)).start();
	}

	public void stopStreams(){
		this._inStream.stop();
		this._outStream.stop();
	}

	public InStream inStream() { return this._inStream; }
	public OutStream outStream() { return this._outStream; }

	public void wakeupIn() { this._in.notifyAll(); }
	public void wakeupOut() { this._out.notifyAll(); }

	public void addIncomingMessage(Message msg){
		synchronized(this._in){
			this._in.add(msg);
			this._in.notify();
		}
	}

	public Message receiveIncomingMessage() throws InterruptedException{
		synchronized(this._in){
			while(this._in.isEmpty()){
				if(!this._server.isRunning()){
					throw new InterruptedException("server stopped.");
				}
				try{
					this._in.wait();
				}
				catch(InterruptedException e){}
			}
			return this._in.remove(0);
		}
	}

	public void addOutgoingMessage(Message msg){
		synchronized(this._out){
			this._out.add(msg);
			this._out.notify();
		}
	}
	
	public Message receiveOutgoingMessage() throws InterruptedException{
		synchronized(this._out){
			while(this._out.isEmpty()){
				if(!this._outStream.isRunning()){
					throw new InterruptedException("outstream stopped.");
				}
				try{
					this._out.wait();
				}
				catch(InterruptedException e){}
			}
			return this._out.remove(0);
		}
	}

	public boolean messageAvailable(){
		synchronized(this._in){
			return !this._in.isEmpty();
		}
	}
}