package manager;
import nioserver.Message;

import org.json.simple.JSONObject;

public interface RequestManager{
	public JSONObject manageRequest(Message msg);
}