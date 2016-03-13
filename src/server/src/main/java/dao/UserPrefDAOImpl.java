package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import static dao.DAOUtilitaire.*;

import org.calorycounter.shared.models.Food;
import org.calorycounter.shared.models.User;
import org.calorycounter.shared.models.Recipe;

public class UserPrefDAOImpl implements UserPrefDAO {
	private DAOFactory daoFactory;
	private static final String SQL_INSERT = "INSERT INTO User_preferences (numUser,numRecipe,rank) VALUES (?,?,?)";
	private static final String SQL_FIND_USER_DE_APPRECIATED_FOOD = "SELECT numRecipe FROM User_preferences WHERE numUser = ? and rank = ?";
	private static final String SQL_FIND_USERS_APPRECIATING_FOOD = "SELECT numUser FROM User_preferences WHERE numRecipe = ?";
	private static final String SQL_DELETE = "DELETE FROM User_preferences WHERE numUser = ? AND numRecipe = ? AND rank = ?";
	private static final String SQL_UPDATE = "UPDATE User_preferences SET numRecipe = ? WHERE numUser = ?";

	private static final String	SQL_FIND_USER_RANK = "SELECT numRecipe, rank FROM User_preferences WHERE numUser = ?";
	private static final String SQL_FIND_FOOD_RANK = "SELECT rank FROM User_preferences WHERE numRecipe = ?";
	
	UserPrefDAOImpl( DAOFactory daoFactory) {
		this.daoFactory = daoFactory;
	}
	

	@Override
	public void create(Long id_user, Long id_recipe, float rank) throws IllegalArgumentException, DAOException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		
		try {
			connection = daoFactory.getConnection();
			preparedStatement = initializationPreparedRequest( connection, SQL_INSERT, false, id_user, id_recipe, rank );
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DAOException ("Failed to create a user preference, no new line added to the DB");
			}
		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			silentClosures(preparedStatement, connection );
		}
	}

	@Override
	public void create(User user, Recipe recipe, float rank) throws IllegalArgumentException, DAOException {
		create(user.getId(), recipe.getId(), rank);
	}

	@Override
	public List<Food> findFoodsForUser(User user){
		List<Food> foods = new ArrayList<Food>();
		for(float rank = 0.0f; rank<5.0f ; rank+= 0.5f){
			foods.addAll(findFoodsForUserAndRank(user,rank));
		}
		return foods;
	}

	@Override
	public List<Recipe> findRecipesForUser(User user){
		List<Recipe> recipes = new ArrayList<Recipe>();
		for(float rank = 0.0f; rank<5.0f ; rank+= 0.5f){
			recipes.addAll(findRecipesForUserAndRank(user,rank));
		}
		return recipes;
	}
	
	@Override
	public List<User> findUsersAppreciating(Recipe recipe) throws DAOException {
		List<User> usersAppreciatingFood = new ArrayList<User>();
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		UserDAO userDAO = null;
		
		try {
			connection = daoFactory.getConnection();
			preparedStatement = initializationPreparedRequest( connection, SQL_FIND_USERS_APPRECIATING_FOOD, false, recipe.getId() );
			resultSet = preparedStatement.executeQuery();
			userDAO = this.daoFactory.getUserDAO();
			while (resultSet.next()) {
				Long idUser = (long) resultSet.getInt("numUser");
				User user = userDAO.findById(idUser);
				usersAppreciatingFood.add(user);
			}
		} catch (SQLException e) {
			throw new DAOException (e);
		} finally {
			silentClosures( resultSet, preparedStatement, connection );
		}
		return usersAppreciatingFood;
	}
	

	@Override
	public void delete(User user, Recipe recipe, float rank) throws IllegalArgumentException, DAOException {
		Connection connection = null;
	    PreparedStatement preparedStatement = null;

	    try {
	        connection = daoFactory.getConnection();
	        preparedStatement = initializationPreparedRequest( connection, SQL_DELETE, false, user.getId(), recipe.getId(), rank);
	        int statut = preparedStatement.executeUpdate();
	        if ( statut == 0 ) {
	            throw new DAOException( "Failed to delete the user preference, no modifications to the table." );
	        }
	    } catch ( SQLException e ) {
	        throw new DAOException( e );
	    } finally {
	        silentClosures( preparedStatement, connection );
	    }
    }
	

	@Override
	public List<Recipe> findRecipesForUserAndRank( User user, float rank ) throws DAOException {
		List<Recipe> rankedRecipe = new ArrayList<Recipe>();
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		RecipeDAO recipeDAO = null;
		
		try {
			connection = daoFactory.getConnection();
			preparedStatement = initializationPreparedRequest( connection, SQL_FIND_USER_DE_APPRECIATED_FOOD, false, user.getId(), rank );
			resultSet = preparedStatement.executeQuery();
			recipeDAO = this.daoFactory.getRecipeDAO();
			while (resultSet.next()) {
				Long idRecipe = (long) resultSet.getInt("numRecipe");
				Recipe recipe = recipeDAO.findById( (int)(long)idRecipe );
				rankedRecipe.add(recipe);
			}
		} catch (SQLException e) {
			throw new DAOException (e);
		} finally {
			silentClosures( resultSet, preparedStatement, connection );
		}
		return rankedRecipe;
	}

	@Override
	public List<Food> findFoodsForUserAndRank( User user, float rank ) throws DAOException {
		List<Food> rankedFood = new ArrayList<Food>();
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		FoodDAO foodDAO = null;
		
		try {
			connection = daoFactory.getConnection();
			preparedStatement = initializationPreparedRequest( connection, SQL_FIND_USER_DE_APPRECIATED_FOOD, false, user.getId(), rank );
			resultSet = preparedStatement.executeQuery();
			foodDAO = this.daoFactory.getFoodDAO();
			while (resultSet.next()) {
				Long idFood = (long) resultSet.getInt("numFood");
				Food food = foodDAO.findById( idFood );
				rankedFood.add(food);
			}
		} catch (SQLException e) {
			throw new DAOException (e);
		} finally {
			silentClosures( resultSet, preparedStatement, connection );
		}
		return rankedFood;
	}

	@Override
	public List<Float> findRankForRecipe(Recipe recipe) throws DAOException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		ArrayList<Float> rankList = new ArrayList<Float>();
		try {
			connection = daoFactory.getConnection();
			preparedStatement = initializationPreparedRequest( connection, SQL_FIND_FOOD_RANK, false, recipe.getId() );
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				float rank = (float) resultSet.getFloat("rank");
				rankList.add(rank);
			}
		} catch (SQLException e) {
			throw new DAOException (e);
		} finally {
			silentClosures( resultSet, preparedStatement, connection );
		}
		return rankList ;
	}

	@Override
	public HashMap findRecipesAndRankForUser(User user) throws DAOException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		RecipeDAO recipeDAO = null;
		HashMap m = new HashMap();
		try {
			connection = daoFactory.getConnection();
			preparedStatement = initializationPreparedRequest( connection, SQL_FIND_USER_RANK, false, user.getId() );
			resultSet = preparedStatement.executeQuery();
			recipeDAO = this.daoFactory.getRecipeDAO();
			while (resultSet.next()) {
				Long idRecipe = (long) resultSet.getInt("numRecipe");
				Recipe recipe = recipeDAO.findById((int)(long)idRecipe);
				float rank = (float) resultSet.getFloat("rank"); 
				m.put(recipe,rank);
			}
		} catch (SQLException e) {
			throw new DAOException (e);
		} finally {
			silentClosures( resultSet, preparedStatement, connection );
		}
		return m ;
	}

	@Override
	public List<Float> findRankForFood(Food food) throws DAOException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		ArrayList<Float> rankList = new ArrayList<Float>();
		try {
			connection = daoFactory.getConnection();
			preparedStatement = initializationPreparedRequest( connection, SQL_FIND_FOOD_RANK, false, food.getId() );
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				float rank = (float) resultSet.getFloat("rank");
				rankList.add(rank);
			}
		} catch (SQLException e) {
			throw new DAOException (e);
		} finally {
			silentClosures( resultSet, preparedStatement, connection );
		}
		return rankList ;
	}

	@Override
	public HashMap findFoodsAndRankForUser(User user) throws DAOException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		FoodDAO foodDAO = null;
		HashMap m = new HashMap();
		try {
			connection = daoFactory.getConnection();
			preparedStatement = initializationPreparedRequest( connection, SQL_FIND_USER_RANK, false, user.getId() );
			resultSet = preparedStatement.executeQuery();
			foodDAO = this.daoFactory.getFoodDAO();
			while (resultSet.next()) {
				Long idFood = (long) resultSet.getInt("numFood");
				Food food = foodDAO.findById(idFood);
				float rank = (float) resultSet.getFloat("rank"); 
				m.put(food,rank);
			}
		} catch (SQLException e) {
			throw new DAOException (e);
		} finally {
			silentClosures( resultSet, preparedStatement, connection );
		}
		return m ;
	}
}
