import java.util.ArrayList;
import java.util.List;

public class Request {

    private String request_name;
    private ArrayList<String> request_keys;
    private ArrayList<String> request_values;
    private int arrays_size;

    public String getRequestName(){
        return request_name;
    }

    public List<String> getRequestKeys(){
        return request_keys;
    }

    public List<String> getRequestValues(){
       return request_values;
    }

    public int getArraysSize(){
        return arrays_size;
    }
    public void setArraysSize(int size){
        arrays_size = size;
    }

    public void setRequestName(String name){
        request_name = name;
    }

    public void setRequestKeys(List<String> keys){
        request_keys = new ArrayList<>(keys);
    }

    public void setRequestValues(List<String> values){
        request_values = new ArrayList<>(values);
    }
}
