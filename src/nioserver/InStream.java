package nioserver;

import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.Selector;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.net.InetSocketAddress;
import java.io.IOException;
import java.util.Iterator;
import java.nio.channels.ClosedChannelException;

public class InStream implements Runnable{
	private ByteBuffer _msgDataBuffer;
	private ByteBuffer _msgSizeBuffer;
	private ServerSocketChannel _acceptChannel;
	private Selector _selector;
	private StreamController _controller;
	private Boolean _running;

	public InStream(StreamController controller){
		try{
			this._msgDataBuffer = ByteBuffer.allocate(Constants.BUFFER_SIZE);
			this._msgSizeBuffer = ByteBuffer.allocate(Constants.INT_SIZE);
			this._acceptChannel = ServerSocketChannel.open();
			this._acceptChannel.configureBlocking(false);
			this._acceptChannel.socket().bind(new InetSocketAddress(Constants.HOST, Constants.PORT));
			this._selector = Selector.open();
			this._acceptChannel.register(this._selector, SelectionKey.OP_ACCEPT);
			this._controller = controller;
			this._running = new Boolean(false);
		}
		catch(IOException e){
			System.out.println(Constants.errorMessage(e.getMessage(), this));
		}
	}

	private void _connect(ServerSocketChannel acceptChannel){
		try{
			SocketChannel clientChannel = acceptChannel.accept();
			clientChannel.configureBlocking(false);
			clientChannel.register(this._selector,SelectionKey.OP_READ);
			if(Constants.SHOW_LOG){
				System.out.println(Constants.repr(this) + " " 
					+ clientChannel.socket().getRemoteSocketAddress().toString() 
					+ Constants.OC_YELLOW + " connected" + Constants.OC_RESET);
			}
		}
		catch(IOException e){
			System.out.println(Constants.errorMessage(e.getMessage(), this));
		}
	}

	private void _disconnect(SocketChannel clientChannel){
		try{
			clientChannel.keyFor(this._selector).cancel();
			clientChannel.close();
			if(Constants.SHOW_LOG){
				System.out.println(Constants.repr(this) + " " 
					+ clientChannel.socket().getRemoteSocketAddress().toString() 
					+ Constants.OC_YELLOW + " disconnected" + Constants.OC_RESET);
			}
		} 
		catch(IOException e){
			System.out.println(Constants.errorMessage(e.getMessage(), this));
		}		
	}

	private void _read(SocketChannel clientChannel) {
		this._msgDataBuffer.clear();
		this._msgSizeBuffer.clear();
		int bytesRead, msgSize = 0; 
		try{
			/* 	Put the 4 first bytes of the message in a dedicated buffer.
				These bytes should contain the size of the message. */
			bytesRead = clientChannel.read(this._msgSizeBuffer);
			if(bytesRead == -1){
				// Client clean-closed the connection.
				this._disconnect(clientChannel);
				return;
			}
			// Get the integer from the 4 bytes.
			msgSize = ((ByteBuffer) this._msgSizeBuffer.flip()).getInt();
			if(msgSize <= 0) return;
			// Read n (msgSize) bytes, where n = size of the message which is thus given in the 4 bytes header.
			this._msgDataBuffer.limit(msgSize);
			bytesRead = clientChannel.read(this._msgDataBuffer);
			if(bytesRead == -1){
				this._disconnect(clientChannel);
				return;
			}

		} 
		catch(IOException e){
			// Client force-closed the connection
			this._disconnect(clientChannel);
			return;
		}

		// Incorrect message length header, data buffer too small (thus message too big).
		if(bytesRead != msgSize){
			System.out.println("could not read incoming message from "+Constants.repr(clientChannel)
				+" : incorrect message length ("+Integer.toString(bytesRead)+" bytes read and "
				+Integer.toString(msgSize)+" bytes is the given message size).");
			return;
		}

		// No error detected, copy the data of the buffer into a byte array and push it in message list.
		Message msg = new Message(clientChannel.socket(), this._msgDataBuffer.array(), msgSize);
		if(Constants.SHOW_LOG){
			System.out.println(Constants.repr(this) + " " + clientChannel.socket().getRemoteSocketAddress().toString() 
				+ Constants.O_IN + msg.toString());
		}
		this._controller.addIncomingMessage(msg);
	}

	public void run(){
		this.setRunning();
		while(this.isRunning()){
			try{
				this._selector.select();
				Iterator<SelectionKey> selectedKeys = this._selector.selectedKeys().iterator();
				while(selectedKeys.hasNext()){
					SelectionKey key = selectedKeys.next();
					if(key.isAcceptable()){
						//First case : accept new connection on the accept channel
						this._connect((ServerSocketChannel)key.channel());
					}
					else if(key.isReadable()){
						//Second case : data is readable on a socket
						this._read((SocketChannel)key.channel());
					}
					selectedKeys.remove();
				}
			}
			catch(IOException e){
				System.out.println(Constants.errorMessage(e.getMessage(), this));
			}
		}
		System.out.println("instream stopped.");
	}

	public void disconnectAll(){
		for(SelectionKey key : this._selector.keys()){
			if(key.interestOps() == SelectionKey.OP_READ){
				this._disconnect((SocketChannel)key.channel());
			}
			else if(key.interestOps() == SelectionKey.OP_ACCEPT){
				try{
					key.channel().close();
					key.cancel();
				}
				catch(IOException e){
					System.out.println(Constants.errorMessage(e.getMessage(), this));
				}
			}
		}
	}

	public void setRunning(){ 
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
		this._selector.wakeup();
	}
}