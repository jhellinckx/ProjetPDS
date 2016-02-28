import org.json.simple.JSONObject;

public class RequestControler {
    private ClientModel model;
    private Request last_request = null;

    public RequestControler(ClientModel mod){
        model = mod;
    }

    public void sendRequest(Request request){
        last_request = request;
        JSONObject msg = RequestWrapper.wrapRequest(request);
        model.sendToNetwork(msg);
    }

    public void sendLastRequest(){
        if (last_request != null) {
            JSONObject msg = RequestWrapper.wrapRequest(last_request);
            model.sendToNetwork(msg);
        }
    }

    public void getResponse(){
        model.getFromNetwork();
    }

    public void terminateModel(){
        model.closeChannel();
    }
}
