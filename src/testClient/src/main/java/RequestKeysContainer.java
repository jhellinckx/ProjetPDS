import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.calorycounter.shared.Constants.network.*;

public class RequestKeysContainer {
    private static final ArrayList<String> keys = new ArrayList<>(Arrays.asList(USERNAME, PASSWORD, FOOD_CODE, FOOD_IMAGE_URL,
            FOOD_RATING, UPDATE_DATA_GENDER, UPDATE_DATA_WEIGHT, PAST_FOODS_LIST, MAX_ENERGY, MAX_CARBOHYDRATES,MAX_FAT,
            MAX_PROT, SPORT_NAME, SPORT_DURATION));

    public static List<String> getKeys(){
        return keys;
    }
}
