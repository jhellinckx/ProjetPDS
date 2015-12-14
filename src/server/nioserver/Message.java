package nioserver;
import org.calorycounter.shared.Constants;

import java.net.Socket;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class Message{
	private Socket _clientSocket;
	private ByteBuffer _dataBuffer;

	public Message(Socket clientSocket, byte[] dataBytes, int msgSize){
		this._clientSocket = clientSocket;
		this._dataBuffer = ByteBuffer.allocate(Constants.INT_SIZE + msgSize);
		this._dataBuffer.putInt(msgSize);
		this._dataBuffer.put(dataBytes, 0, msgSize);
	}

	public Message(Socket clientSocket, JSONObject obj){
		this._clientSocket = clientSocket;
		byte[] dataBytes = obj.toString().getBytes(Constants.ENCODING);
		this._dataBuffer = ByteBuffer.allocate(Constants.INT_SIZE + dataBytes.length);
		this._dataBuffer.putInt(dataBytes.length);
		this._dataBuffer.put(dataBytes);
	}

	/* Returns byte array of the JSON object -- thus ignoring the size header. */
	private byte[] rawObject() {
		byte[] dataBytes = new byte[this._dataBuffer.capacity()-Constants.INT_SIZE];
		this._dataBuffer.position(Constants.INT_SIZE);
		this._dataBuffer.get(dataBytes);
		return dataBytes;
	}

	public Socket socket(){ return this._clientSocket; }

	public String toString(){ return new String(this.rawObject(), Constants.ENCODING); }

	public JSONObject toJSON() throws ParseException { 
		return (JSONObject)(new JSONParser()).parse(new String(this.rawObject(), Constants.ENCODING));
	}

	public ByteBuffer raw(){ 
		this._dataBuffer.flip();
		return this._dataBuffer;
	}
}