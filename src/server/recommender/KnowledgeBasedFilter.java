package recommender;

import dao.FoodDAO;
import items.Food;
import items.User;

import java.util.List;

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
	private float sodiumNeeded;

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

	public List<Food> recommend() {

		List<Food> knowledgeBasedFilterFoods = null;
		fatNeeded = Constants.HUMAN_DAILY_FAT;
		proteinsNeeded = Constants.HUMAN_DAILY_PROTEINS;
		saturatedFatNeeded = Constants.HUMAN_DAILY_SATURATED_FAT;
		carbohydratesNeeded = Constants.HUMAN_DAILY_CARBOHYDRATES;
		sugarsNeeded = Constants.HUMAN_DAILY_SUGARS;
		sodiumNeeded = Constants.HUMAN_DAILY_SODIUM;
		if(currentUser.getGender() == "M") {
			energyNeeded = Constants.MEN_DAILY_ENERGY;
		}
		else if (currentUser.getGender() == "F") {
			energyNeeded = Constants.WOMEN_DAILY_ENERGY;
		}
		else if (currentUser.getGender() == "T") {
			energyNeeded = Constants.TEEN_DAILY_ENERGY;
		}
		else {
			energyNeeded = Constants.CHILD_DAILY_ENERGY;
		}

		knowledgeBasedFilterFoods = foodDAO.findFoodWithLessThanLevels(energyNeeded, fatNeeded, proteinsNeeded, saturatedFatNeeded, carbohydratesNeeded, sugarsNeeded, sodiumNeeded);

		/* To test */
		for (Food i : knowledgeBasedFilterFoods) {
			System.out.println(i.toString());
		}
		return knowledgeBasedFilterFoods;
	}


	}