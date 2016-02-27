import org.calorycounter.shared.Constants;
import org.json.simple.parser.ParseException;
import org.json.simple.JSONObject;

public class Client{
    public static final void main(String[] args){

        System.out.println("Hello World !" +Constants.network.FOOD_URL);
        JSONObject obj = new JSONObject();
        obj.put("client","test");
        System.out.println(obj.toString());
    }
}