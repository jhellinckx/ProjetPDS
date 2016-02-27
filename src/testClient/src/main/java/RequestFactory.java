import java.util.List;

public class RequestFactory {

    public Request getRequestInstance(String name, List<String> keys, List<String> values){
        if (!hasValidArgs(keys, values)){
            return null;
        }
        Request request = new Request();
        request.setRequestName(name);
        request.setArraysSize(keys.size());
        request.setRequestKeys(keys);
        request.setRequestValues(values);
        return request;

    }

    private boolean hasValidArgs(List<String> keys, List<String> values){
        return keys.size() == values.size();
    }
}
