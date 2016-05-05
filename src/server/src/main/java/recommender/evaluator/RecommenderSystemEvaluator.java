package recommender.evaluator;

import dao.DAOFactory;
import dao.RecipeDAO;
import dao.UserDAO;
import dao.UserPrefDAO;
import org.calorycounter.shared.models.Recipe;
import org.calorycounter.shared.models.User;
import recommender.*;

import java.util.*;

import util.MeanErrorCalculator;

public class RecommenderSystemEvaluator {

    private final int NB_USER = 100;
    private final float RATINGS_WITHHELD_PERCENTAGE = 0.25f;

    private UserPrefDAO _userPrefdb;
    private UserDAO _userdb;
    private RecipeDAO _recipedb;
    private DAOFactory _daoFactory;

    private RecommenderSystem system;
    private double absolute_error;
    private HashMap correct_values;
    private List<Long> withheld_ids;
    private List<User> all_users;
    private List<Recipe> all_recipes;

    public RecommenderSystemEvaluator(){
        _daoFactory = DAOFactory.getInstance();
        _userPrefdb = _daoFactory.getUserPrefDAO();
        _userdb = _daoFactory.getUserDAO();
        _recipedb = _daoFactory.getRecipeDAO();
    }


    private void initUsers(){
        List<User> users = _userdb.findAllUsersWithRanks();
        all_users = new ArrayList<>();
        for (User user : users){
            all_users.add(_userdb.findById(user.getId()));
        }
    }

    private void filterUser(User user){
        List<Recipe> recipes = _recipedb.findByIds(withheld_ids);
        for (Recipe recipe: recipes){
            user.removeEdibleItem(recipe);
        }
    }

    private List<User> selectRandomUsers(){
        initUsers();
        HashSet<User> selected_users = new HashSet<>();
        Random rand = new Random();
        int pos;
        int size = all_users.size();

        int i = 0;
        while (i < NB_USER){
            pos = rand.nextInt(size);
            if(!selected_users.contains(all_users.get(pos))){
                selected_users.add(all_users.get(pos));
                i++;
            }
        }
        return new ArrayList<>(selected_users);
    }

    private List<Long> selectRandomIds(int size, int upper) {
        Random rand = new Random();
        int nbr = (int) (size*RATINGS_WITHHELD_PERCENTAGE);
        HashSet<Long> ids = new HashSet<>();

        int i = 0;
        while (i < nbr){
            long id = (long) rand.nextInt(upper)+1;
            if(!ids.contains(id) && correct_values.containsKey(id)){
                ids.add(id);
                i++;
            }
        }
        return new ArrayList<>(ids);
    }

    private void startRecommendation(User user){
        filterUser(user);
        system.updateData(all_recipes, all_users, user, 100);
        system.recommendItems();
    }

    private double computeError(){
        int size = withheld_ids.size();
        Map<Long, RecipeRatingPair> predictions = system.getRatingsPrediction();
        List<Float> correct = new ArrayList<>();
        List<Float> pred = new ArrayList<>();
        for (int i = 0; i < size; i++){
            Long id = withheld_ids.get(i);
            correct.add((Float) correct_values.get(id));
            pred.add(predictions.get(id).getRating());
        }
        return MeanErrorCalculator.getMeanAbsoluteError(correct, pred);
    }

    private double computeAbsoluteError(User user){
        HashMap map = user.getRankedEdibleItems();
        correct_values = new HashMap<>();
        for (Object obj: map.keySet()){
            Recipe recipe = (Recipe) obj;
            correct_values.put(recipe.getId(), map.get(obj));
        }

        withheld_ids = selectRandomIds(correct_values.size(), all_recipes.size());
        startRecommendation(user);
        return computeError();
    }

    private double evaluateSystem(){
        absolute_error = 0;

        System.out.println("Starting Evaluation");
        List<User> users = selectRandomUsers();
        List<Long> ids = _recipedb.findAllRecipeIds();
        all_recipes = _recipedb.findByIds(ids);

        for (int i = 0; i < NB_USER; i++){
            System.out.println("Step: " + Integer.toString(i));
            absolute_error += computeAbsoluteError(users.get(i));
        }
        return (absolute_error/NB_USER);
    }

    public double evaluateUserUser(){
        system = new RecommenderSystem(new UserUserStrategy(_userPrefdb));
        return evaluateSystem();
    }

    public double evaluateContentBased(){
        system = new RecommenderSystem(new NearestNeighborStrategy(_daoFactory.getRecipeSimilarityDAO()));
        return evaluateSystem();
    }

    public double evaluateFeatureAugmentation(){
        system = new RecommenderSystem(new NearestNeighborStrategy(_daoFactory.getRecipeSimilarityDAO()), new FeatureAugmentationStrategy());
        system.addRecommendationStrategyToHybridStrategy(new FAUserUserStrategy(_userPrefdb));
        return evaluateSystem();
    }

}
