import java.util.ArrayList;
import java.util.Arrays;

import static org.calorycounter.shared.Constants.network.*;

public class RequestNameContainer {
    private static final ArrayList<String> names = new ArrayList<>(Arrays.asList(LOG_IN_REQUEST,
            LOG_OUT_REQUEST, SIGN_UP_REQUEST, FOOD_CODE_REQUEST, RANDOM_UNRANKED_FOODS_REQUEST,
            SEND_RATINGS_REQUEST, SPORTS_LIST_REQUEST, CHOSEN_SPORT_REQUEST, UPDATE_DATA_REQUEST,
            DATA_REQUEST, RECOMMEND_REQUEST));

    public static ArrayList<String> getRequestNames(){
        return names;
    }
}
