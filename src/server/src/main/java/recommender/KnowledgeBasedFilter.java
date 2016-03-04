package recommender;
import static org.calorycounter.shared.Constants.network.*;

import dao.FoodDAO;
import org.calorycounter.shared.models.Food;
import org.calorycounter.shared.models.User;

import java.util.List;
import java.util.ArrayList;

/* 
 * Class that filters the (for the moment) openFoodFact DB to select only items with the correct amount of needs depending on the currentUser gender
 */
public class KnowledgeBasedFilter {

	private FoodDAO foodDAO;
	private User currentUser;
	private float energyUsedByExercice; //not yet implemented
	private float energyNeeded;
	private float fatNeeded;
	private float proteinsNeeded;
	private float saturatedFatNeeded;
	private float carbohydratesNeeded;
	private float sugarsNeeded;
	private float saltNeeded;

	public KnowledgeBasedFilter(FoodDAO fDAO) {

		foodDAO = fDAO;
		currentUser = null;

	}

	public KnowledgeBasedFilter(FoodDAO fDAO, User user) {

		foodDAO = fDAO;
		currentUser = user;
	}

	public void updateUser(User user) {

		currentUser = user;
	}

	private void previousEatenAliments(List<Food> previousAliments) {
		if (!previousAliments.isEmpty()) {
			for (Food aliment : previousAliments) {
				energyNeeded = energyNeeded - aliment.getTotalEnergy();
				fatNeeded = fatNeeded - aliment.getTotalFat();
				proteinsNeeded = proteinsNeeded - aliment.getTotalProteins();
				saturatedFatNeeded = saturatedFatNeeded - aliment.getTotalSaturatedFat();
				carbohydratesNeeded = carbohydratesNeeded - aliment.getTotalCarbohydrates();
				sugarsNeeded = sugarsNeeded - aliment.getTotalSugars();
				saltNeeded = saltNeeded - aliment.getTotalSalt();
			}
		}
	}

	private ArrayList<Food> recommendMethod(List<Food> previousAliments) {
		previousEatenAliments(previousAliments);
		ArrayList<Food> results = new ArrayList<Food>(foodDAO.findFoodWithLessThanLevels(energyNeeded, fatNeeded, proteinsNeeded, saturatedFatNeeded, carbohydratesNeeded, sugarsNeeded, saltNeeded));
		return results;
	}


	public ArrayList<Food> recommend(List<Food> previousAliments, float energySupBorn, float fatSupBorn, float proteinsSupBorn, float carbohydratesSupBorn) {

		energyNeeded = energySupBorn;
		fatNeeded = fatSupBorn;
		proteinsNeeded = proteinsSupBorn;
		saturatedFatNeeded = HUMAN_DAILY_SATURATED_FAT;
		carbohydratesNeeded = carbohydratesSupBorn;
		sugarsNeeded = HUMAN_DAILY_SUGARS;
		saltNeeded = HUMAN_DAILY_SODIUM;
		return recommendMethod(previousAliments);
	}
}