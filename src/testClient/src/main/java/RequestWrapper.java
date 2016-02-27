import org.json.simple.JSONObject;

import static org.calorycounter.shared.Constants.network.REQUEST_TYPE;
import static org.calorycounter.shared.Constants.network.DATA;

import java.util.List;

public class RequestWrapper {

    private static JSONObject requestToJSON(Request request){
        JSONObject data = new JSONObject();
        List<String> keys = request.getRequestKeys();
        List<String> values = request.getRequestValues();
        int size = request.getArraysSize();
        for(int i = 0; i < size; i++){
            data.put(keys.get(i), values.get(i));
        }
        return data;
    }

    public static JSONObject wrapRequest(Request request){
        JSONObject data = requestToJSON(request);
        JSONObject message = new JSONObject();
        message.put(REQUEST_TYPE, request.getRequestName());
        message.put(DATA, data);
        return message;
    }
}
