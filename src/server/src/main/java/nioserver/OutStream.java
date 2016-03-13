package nioserver;
import org.calorycounter.shared.Constants;
import static org.calorycounter.shared.Constants.network.*;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.io.IOException;
import java.util.Arrays;


public class OutStream implements Runnable{
	private Boolean _running;
	private StreamController _controller;

	public OutStream(StreamController controller){
		this._running = new Boolean(false);
		this._controller = controller;
	}

	private void _write(Message msg) throws IOException {		
		SocketChannel clientChannel = msg.socket().getChannel();
		ByteBuffer buffer = msg.raw();
		while(buffer.remaining() > 0){
			clientChannel.write(buffer);
		}
		// if(buffer.remaining() > 0){
		// 	// Socket buffer is full; try to send the message later by pushing it at the end of queue.
		// 	buffer.flip();
		// 	this._controller.addOutgoingMessage(msg);
		// 	throw new IOException("could not write message to "+Constants.repr(clientChannel)+
		// 	" : clientChannel buffer is full.");
		// }
		if(Constants.SHOW_LOG){
			// copy item
			JSONObject obj = new JSONObject(msg.toJSON());
			if(obj != null){
				JSONObject data = (JSONObject)obj.get(DATA);
				if(data.containsKey(FOOD_LIST)){
					JSONArray jsonFoods = (JSONArray) data.get(FOOD_LIST);
		            for (int i = 0; i < jsonFoods.size(); i++){
		            	JSONObject jsonFood = (JSONObject)jsonFoods.get(i);
		            	if(jsonFood.containsKey(FOOD_IMAGE)){
		            		JSONObject jsonFoodImage = (JSONObject)jsonFood.get(FOOD_IMAGE);
		            		jsonFoodImage.put(IMAGE_PIC, "REPLACED_IMAGE_FOR_LOG");
		            	}
		            }
				}
			}
			System.out.println(Constants.repr(this) + " " + msg.socket().getRemoteSocketAddress().toString()
				+ Constants.OUT + obj.toString());
		}
	}

	public void run(){
		this.setRunning();
		while(this.isRunning()){
			try{
				Message msg = this._controller.receiveOutgoingMessage();
				this._write(msg);
			}
			catch(InterruptedException e){
				// Thread stopped
				System.out.println(e.getMessage());
			}
			catch(IOException e){
				System.out.println(Constants.errorMessage(e.getMessage(), this));
			}
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
		this._controller.wakeupOut();
	}
}