package recommender;

import dao.UserPrefDAO;
import org.calorycounter.shared.models.Recipe;
import org.calorycounter.shared.models.User;

import java.util.*;
import java.lang.Math;

/**
 * Created by aurelien on 5/05/16.
 */
public class FAUserUserStrategy extends UserUserStrategy {

    private Map<User, List<RecipeRatingPair>> pseudo_matrix;
    private HashMap user_pseudo_map;
    private HashMap<Long, HashMap> other_users_map;
    private List<Double> harmonic_vec;
    private double self_weight;

    public FAUserUserStrategy(UserPrefDAO pref){
        super(pref);
    }

    public void updateData(List<Recipe> recipes, List<User> users, User curUser, int nbRecom, Map<User, List<RecipeRatingPair>> cb_predictions){
        super.updateData(recipes, users, curUser, nbRecom);
        pseudo_matrix = cb_predictions;
    }

    private void setMap(HashMap items, User u, Map map){
        List<RecipeRatingPair> pseudo_ratings = pseudo_matrix.get(u);
        for (RecipeRatingPair pair : pseudo_ratings){
            Recipe recipe = pair.getFood();
            if (items.containsKey(recipe)){
                map.put(recipe, items.get(recipe));
            } else{
                map.put(recipe, pair.getRating());
            }
        }
    }

    private void setUserMap(HashMap items, User u){
        if (user_pseudo_map == null){
            user_pseudo_map = new HashMap();
            setMap(items, u, user_pseudo_map);
        }
    }

    private void setOtherUserMap(HashMap items, User v){
        HashMap other_user_map = new HashMap();
        setMap(items, v, other_user_map);
        other_users_map.put(v.getId(), other_user_map);
    }

    @Override
    public double computeConstrainedPearsonCorrelation(User u, User v){
        setUserMap(u.getRankedEdibleItems(), u);
        setOtherUserMap(v.getRankedEdibleItems(), v);

        Set<Recipe> commonRankedFoods = new HashSet<>(user_pseudo_map.keySet());
        return (pearsonNumerator(user_pseudo_map, other_users_map.get(v.getId()), commonRankedFoods)/pearsonDenominator(user_pseudo_map, other_users_map.get(v.getId()), commonRankedFoods));
    }

    protected double computeHarmonicMean(User u, User v){
        HashMap uRankedItems = u.getRankedEdibleItems();
        HashMap vRankedItems = v.getRankedEdibleItems();
        Set<Recipe> commonRankedFoods = new HashSet<>(uRankedItems.keySet());
        commonRankedFoods.retainAll(vRankedItems.keySet());

        double m_i = (uRankedItems.size() >= 50 ? 1 : (uRankedItems.size()/50));
        double m_j = (vRankedItems.size() >= 50 ? 1 : (vRankedItems.size()/50));
        double mean_weight = ((2*m_i*m_j)/(m_i+m_j));
        double s_g = (commonRankedFoods.size() >= 50 ? 1 : (commonRankedFoods.size()/50));

        return (mean_weight+s_g);
    }

    protected double computeSelfWeight(User u){
        HashMap uRankedItems = u.getRankedEdibleItems();
        double s_w = (uRankedItems.size() >= 50 ? 2 : ((uRankedItems.size()/50)*2));
        return s_w;
    }

    protected float computeMeanRank(User user){
        float sumRank = 0;
        int size = 1;
        Collection ranks = null;
        if (user.getId() == currentUser.getId()) {
            size = user_pseudo_map.size();
            ranks = user_pseudo_map.values();
        } else{
            HashMap map = other_users_map.get(user.getId());
            size = map.size();
            ranks = map.values();
        }
        Iterator it = ranks.iterator();
        while(it.hasNext()){
            sumRank += (float)it.next();
        }
        return (sumRank/size);
    }

    protected float computeStandardDeviation(User user){
        float mean = computeMeanRank(user);
        float tmp = 0;
        int size = 0;
        float rank = 0;
        Collection ranks = null;
        if (user.getId() == currentUser.getId()){
            size = user_pseudo_map.size();
            ranks = user_pseudo_map.values();
        } else{
            HashMap map = other_users_map.get(user.getId());
            size = map.size();
            ranks = map.values();
        }

        Iterator it = ranks.iterator();
        while (it.hasNext()){
            rank = (float) it.next();
            tmp += (mean-rank)*(mean-rank);
        }

        return ((float) Math.sqrt(tmp/size));

    }

    protected float ratingNumerator(int index, User user, Recipe food){
        float numerator;
        numerator = (float) ((similarityVec.get(index))*(harmonic_vec.get(index)));
        numerator = (numerator)*(((float)(other_users_map.get(user.getId()).get(food))) - computeMeanRank(user));
        numerator = (numerator)/(computeStandardDeviation(user));
        return numerator;
    }

    protected float ratingFactor(Recipe food, float mean, float std, float num, float den){
        float factor = mean;
        float numerator = (float) (self_weight*(((float) user_pseudo_map.get(food))-mean));
        numerator += num;
        factor += std*(numerator/(((float)self_weight)+den));
        return factor;
    }

    @Override
    protected float computeRating(Recipe food){
        float meanRankCurrUser = computeMeanRank(currentUser);
        float stdDevCurrUser = computeStandardDeviation(currentUser);
        float predictedRank;

        float numerator = 0.0f;
        float denominator = 0.0f;

        for (int i = 0; i < dataSize-1; i++){
            User otherUser = userData.get(i);
            numerator += ratingNumerator(i, otherUser, food);
            denominator += (float) ((harmonic_vec.get(i))*(similarityVec.get(i)));
        }
        predictedRank = ratingFactor(food, meanRankCurrUser, stdDevCurrUser, numerator, denominator);
        return predictedRank;
    }

    @Override
    protected void calculateSimilarityMatrix(){
        for (User user: userData){
            if (user.getId() != currentUser.getId()){
                double similarity = computeConstrainedPearsonCorrelation(currentUser, user);
                similarityVec.add(similarity);
                harmonic_vec.add(computeHarmonicMean(currentUser, user));
            } else{
                similarityVec.add(0.0);
                harmonic_vec.add(0.0);
            }
        }
        self_weight = (computeSelfWeight(currentUser));
    }

    @Override
    protected void computeRatingPredictions(){
        super.computeRatingPredictions();

    }

    @Override
    public ArrayList<Recipe> recommend() {
        user_pseudo_map = null;
        other_users_map = new HashMap<>();
        harmonic_vec = new ArrayList<>();
        return super.recommend();
    }
}
