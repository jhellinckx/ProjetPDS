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
	private JSONObject _obj;

	public Message(Socket clientSocket, byte[] dataBytes, int msgSize){
		this._clientSocket = clientSocket;
		this._dataBuffer = ByteBuffer.allocate(Constants.network.INT_SIZE + msgSize);
		this._dataBuffer.putInt(msgSize);
		this._dataBuffer.put(dataBytes, 0, msgSize);
		this._obj = null;
	}

	public Message(Socket clientSocket, JSONObject obj){
		this._clientSocket = clientSocket;
		setByteBuffer(obj);
	}

	private void setByteBuffer(JSONObject obj){
		byte[] dataBytes = obj.toString().getBytes(Constants.network.ENCODING);
		this._dataBuffer = ByteBuffer.allocate(Constants.network.INT_SIZE + dataBytes.length);
		this._dataBuffer.putInt(dataBytes.length);
		this._dataBuffer.put(dataBytes);
		this._obj = null;
	}


	/* Returns byte array of the JSON object -- thus ignoring the size header. */
	private byte[] rawObject() {
		byte[] dataBytes = new byte[this._dataBuffer.capacity()-Constants.network.INT_SIZE];
		this._dataBuffer.position(Constants.network.INT_SIZE);
		this._dataBuffer.get(dataBytes);
		return dataBytes;
	}

	public Socket socket(){ return this._clientSocket; }

	public String toString(){ return new String(this.rawObject(), Constants.network.ENCODING); }

	public JSONObject toJSON(){
		if(this._obj == null){
			try{
				this._obj = (JSONObject)(new JSONParser()).parse(new String(this.rawObject(), Constants.network.ENCODING));
			}
			catch(ParseException e){
				System.out.println(Constants.errorMessage(e.getMessage(), this));
				this._obj = null;
			}
		}
		return this._obj;
	}

	public void setJSON(JSONObject other){
		setByteBuffer(other);
		this._obj = other;
	}

	public ByteBuffer raw(){ 
		this._dataBuffer.flip();
		return this._dataBuffer;
	}
}