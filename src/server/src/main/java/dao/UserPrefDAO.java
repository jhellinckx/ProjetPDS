package dao;

import java.util.List;
import java.util.HashMap;
import org.calorycounter.shared.models.User;
import org.calorycounter.shared.models.Food;
import org.calorycounter.shared.models.Recipe;

public interface UserPrefDAO {
	
	void create( User user, Recipe recipe , float rank) throws DAOException;

	void create(Long id_user, Long id_recipe, float rank) throws IllegalArgumentException, DAOException;

	void createAll(Long id_user, List<Long> id_recipe, List<Float> rank) throws IllegalArgumentException, DAOException;

	List<Food> findFoodsForUser(User user);

	List<Recipe> findRecipesForUser(User user);
	
	List<User> findUsersAppreciating( Recipe recipe ) throws DAOException;

	List<Float> findRankForFood(Food food) throws DAOException;

	List<Float> findRankForRecipe(Recipe recipe) throws DAOException;

	List<Food> findFoodsForUserAndRank( User user, float rank ) throws DAOException;

	List<Recipe> findRecipesForUserAndRank( User user, float rank ) throws DAOException;

	HashMap findFoodsAndRankForUser(User user) throws DAOException;

	HashMap findRecipesAndRankForUser(User user) throws DAOException;
	
	void delete (User user, Recipe recipe, float mark) throws DAOException;

}
