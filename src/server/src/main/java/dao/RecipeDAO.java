package dao;

import java.util.List;

import org.calorycounter.shared.models.Recipe;

public interface RecipeDAO {

	public Recipe findByName ( String recipeName ) throws DAOException;
		
	public Recipe findById (int id ) throws DAOException;

	public List<Recipe> findRecipeWithLessThanLevels(float energy, float fat, float proteins, float carbohydrates, String category) throws DAOException;
	

	public List<Recipe> findRecipeWithLessThanLevelsOrderByPredictionsWithLimit(float energy, float fat, float proteins, float carbohydrates, int limit, int user_id, String category);

	public List<Long> getRecipeIdsByCategory(String categoryName) throws DAOException;
}