package nioserver;

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
		clientChannel.write(buffer);
		if(buffer.remaining() > 0){
			// Socket buffer is full; try to send the message later by pushing it at the end of queue.
			buffer.flip();
			this._controller.addOutgoingMessage(msg);
			throw new IOException("could not write message to "+Constants.repr(clientChannel)+
			" : clientChannel buffer is full.");
		}
		if(Constants.SHOW_LOG){
			System.out.println(Constants.repr(this) + " " + msg.socket().getRemoteSocketAddress().toString()
				+ Constants.O_OUT + msg.toString());
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