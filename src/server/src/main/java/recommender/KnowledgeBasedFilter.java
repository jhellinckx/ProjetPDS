package recommender;
import static org.calorycounter.shared.Constants.network.*;

import dao.FoodDAO;
import dao.RecipeDAO;
import org.calorycounter.shared.models.EdibleItem;
import org.calorycounter.shared.models.Food;
import org.calorycounter.shared.models.User;
import org.calorycounter.shared.models.Recipe;

import java.util.List;
import java.util.ArrayList;

/* 
 * Class that filters the (for the moment) openFoodFact DB to select only items with the correct amount of needs depending on the currentUser gender
 */
public class KnowledgeBasedFilter {

	private FoodDAO foodDAO;
	private RecipeDAO recipeDAO;
	private User currentUser;
	private float energyUsedByExercice;
	private float energyNeeded;
	private float fatNeeded;
	private float proteinsNeeded;
	private float saturatedFatNeeded;
	private float carbohydratesNeeded;
	private float sugarsNeeded;
	private float saltNeeded;

	public KnowledgeBasedFilter(FoodDAO fDAO, RecipeDAO rDAO) {

		foodDAO = fDAO;
		recipeDAO = rDAO;
		currentUser = null;

	}

	public KnowledgeBasedFilter(FoodDAO fDAO, RecipeDAO rDAO, User user) {

		foodDAO = fDAO;
		recipeDAO = rDAO;
		currentUser = user;
	}

	public void updateUser(User user) {

		currentUser = user;
	}

	private void addPreviousNutriment(Food food){ //need to separated food from Recipe because recipe don't have salt, sugars & saturated fat
		energyNeeded = energyNeeded - food.getTotalEnergy();
		fatNeeded = fatNeeded - food.getTotalFat();
		proteinsNeeded = proteinsNeeded - food.getTotalProteins();
		saturatedFatNeeded = saturatedFatNeeded - food.getTotalSaturatedFat();
		carbohydratesNeeded = carbohydratesNeeded - food.getTotalCarbohydrates();
		sugarsNeeded = sugarsNeeded - food.getTotalSugars();
		saltNeeded = saltNeeded - food.getTotalSalt();
	}

	private void addPreviousNutriment(Recipe recipe){
		energyNeeded = energyNeeded - recipe.getTotalEnergy();
		fatNeeded = fatNeeded - recipe.getTotalFat();
		proteinsNeeded = proteinsNeeded - recipe.getTotalProteins();
		carbohydratesNeeded = carbohydratesNeeded - recipe.getTotalCarbohydrates();
	}

	private void previousEatenAliments(List<EdibleItem> previousAliments) {
		if (!previousAliments.isEmpty()) {
			for (int i=0; i<previousAliments.size(); i++) {
				if(previousAliments.get(i) instanceof Food){
					Food aliment = (Food) previousAliments.get(i);
					addPreviousNutriment(aliment);
				}else{
					Recipe aliment = (Recipe) previousAliments.get(i);
					addPreviousNutriment(aliment);
				}

			}
		}
	}

	private ArrayList<Food> recommendMethodFood(List<EdibleItem> previousAliments, String category) {
		previousEatenAliments(previousAliments);
		ArrayList<Food> results = new ArrayList<Food>(foodDAO.findFoodWithLessThanLevels(energyNeeded, fatNeeded, proteinsNeeded, saturatedFatNeeded, carbohydratesNeeded, sugarsNeeded, saltNeeded, category));
		return results;
	}

	private ArrayList<Recipe> recommendMethodRecipe(List<EdibleItem> previousAliments, String category) {
		previousEatenAliments(previousAliments);
		ArrayList<Recipe> results = new ArrayList<Recipe>(recipeDAO.findRecipeWithLessThanLevels(energyNeeded, fatNeeded, proteinsNeeded, carbohydratesNeeded, category));
		return results;
	}


	public ArrayList<Food> recommend(List<EdibleItem> previousAliments, float energySupBorn, float fatSupBorn, float proteinsSupBorn, float carbohydratesSupBorn, String category) {

		energyNeeded = energySupBorn;
		fatNeeded = fatSupBorn;
		proteinsNeeded = proteinsSupBorn;
		saturatedFatNeeded = HUMAN_DAILY_SATURATED_FAT;
		carbohydratesNeeded = carbohydratesSupBorn;
		sugarsNeeded = HUMAN_DAILY_SUGARS;
		saltNeeded = HUMAN_DAILY_SODIUM;
		return recommendMethodFood(previousAliments,category);
	}

	public ArrayList<Recipe> recommendRecipe(List<EdibleItem> previousAliments, float energySupBorn, float fatSupBorn, float proteinsSupBorn, float carbohydratesSupBorn, String category) {
		energyNeeded = energySupBorn;
		fatNeeded = fatSupBorn;
		proteinsNeeded = proteinsSupBorn;
		carbohydratesNeeded = carbohydratesSupBorn;
		return recommendMethodRecipe(previousAliments,category);
	}
}