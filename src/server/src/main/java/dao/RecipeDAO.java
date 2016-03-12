package dao;

import java.util.List;

import org.calorycounter.shared.models.Recipe;

public interface RecipeDAO {

	Recipe findByName ( String recipeName ) throws DAOException;
		
	Recipe findById (int id ) throws DAOException;

	List<Recipe> findRecipeWithLessThanLevels(float energy, float fat, float proteins, float carbohydrates, String category) throws DAOException;
		
	}