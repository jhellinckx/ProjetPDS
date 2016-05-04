package recommender;

import org.calorycounter.shared.models.Recipe;

/**
 * Created by aurelien on 4/05/16.
 */
public class RecipeRatingPair {

    private Recipe recipe;
    private float rating;

    public RecipeRatingPair(Recipe f, float r){
        recipe = f;
        rating = r;
    }

    public Recipe getFood(){
        return recipe;
    }

    public float getRating() {
        return rating;
    }
}
