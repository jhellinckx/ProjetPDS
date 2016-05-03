package dao;

import static dao.DAOUtilitaire.initializationPreparedRequest;
import static dao.DAOUtilitaire.silentClosures;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


import org.calorycounter.shared.models.Recipe;

public class RecipeDAOImpl implements RecipeDAO {
	private DAOFactory daoFactory;
	private static final String SQL_SELECT_BY_NAME = "SELECT recipe_id, recipe_name, portions, recipe_image_url, recipe_url, image_pic, ingredients_list, portion_calorie, portion_fat, portion_carbo, portion_protein FROM Recipe WHERE recipe_name = ?";
	private static final String SQL_SELECT_BY_ID = "SELECT recipe_id, recipe_name, portions, recipe_image_url, recipe_url, image_pic, ingredients_list, portion_calorie, portion_fat, portion_carbo, portion_protein FROM Recipe WHERE recipe_id = ?";
	private static final String SQL_SELECT_ALL_IDS = "SELECT recipe_id FROM Recipe";
    private static final String SQL_SELECT_SUB_CAT_BY_ID = "SELECT C.category_id FROM RecipeCategories C, JDFCategory J WHERE C.recipe_id = ? AND C.category_id = J.category_id AND J.is_main = 0";
	private static final String SQL_SELECT_ORIGIN_BY_ID = "SELECT origin_id FROM RecipeOrigins WHERE recipe_id = ?";
	private static final String SQL_SELECT_INGREDIENTS_IDS = "SELECT ingredient_id FROM RecipeIngredients WHERE recipe_id = ?";
	private static final String SQL_SELECT_TAG_IDS = "SELECT tag_id FROM RecipeTags WHERE recipe_id = ?";
    private static final String SQL_SELECT_LESS_THAN_LEVELS = "SELECT recipe_id, portions, recipe_name, recipe_image_url, recipe_url, image_pic, ingredients_list, portion_calorie, portion_fat, portion_carbo, portion_protein FROM Recipe WHERE portion_calorie BETWEEN 0 AND ? AND portion_fat <= ? AND portion_protein <= ? AND portion_carbo <= ? ORDER BY portion_calorie DESC";
    private static final String SQL_SELECT_LESS_THAN_LEVELS_AND_CATEGORY = "SELECT Recipe.recipe_id, recipe_name, portions, recipe_image_url, recipe_url, image_pic, ingredients_list, portion_calorie, portion_fat, portion_carbo, portion_protein FROM Recipe JOIN RecipeCategories ON RecipeCategories.recipe_id = Recipe.recipe_id JOIN JDFCategory on RecipeCategories.category_id = JDFCategory.category_id WHERE portion_calorie BETWEEN 0 AND ? AND portion_fat <= ? AND portion_protein <= ? AND portion_carbo <= ? AND JDFCategory.category_name = ? ORDER BY portion_calorie DESC";    private static final String SQL_SELECT_IDS_BY_CATEGORY = "SELECT Recipe.recipe_id FROM Recipe JOIN RecipeCategories ON RecipeCategories.recipe_id = Recipe.recipe_id JOIN JDFCategory ON JDFCategory.category_id = RecipeCategories.category_id WHERE JDFCategory.category_name = ?";
 	
 	private static final String SQL_SELECT_LESS_THAN_LEVELS_ORDER_BY_CB_PREDICTIONS_WITH_LIMIT = 
    "SELECT Recipe.recipe_id, portions, Recipe.recipe_name,"+
    "recipe_image_url, recipe_url, image_pic, ingredients_list,"+
    "Recipe.portion_calorie, Recipe.portion_fat, Recipe.portions, Recipe.portion_carbo, Recipe.portion_protein "+
    "FROM CBUserPredictions "+
    "JOIN Recipe ON Recipe.recipe_id=CBUserPredictions.recipe_id "+
    "WHERE CBUserPredictions.user_id=? AND portion_calorie BETWEEN 0 AND ? "+
    "AND portion_fat BETWEEN 0 AND ? AND portion_protein BETWEEN 0 AND ? "+
    "AND portion_carbo BETWEEN 0 AND ? "+
    "ORDER BY CBUserPredictions.prediction DESC, Recipe.portion_calorie DESC "+
    "LIMIT ?";

    private static final String SQL_SELECT_LESS_THAN_LEVELS_AND_CATEGORY_ORDER_BY_CB_PREDICTIONS_WITH_LIMIT = 
    "SELECT Recipe.recipe_id, portions, Recipe.recipe_name,"+
    "recipe_image_url, recipe_url, image_pic, ingredients_list,"+
    "Recipe.portion_calorie, Recipe.portion_fat, Recipe.portions, Recipe.portion_carbo, Recipe.portion_protein "+
    "FROM CBUserPredictions "+
    "JOIN Recipe ON Recipe.recipe_id=CBUserPredictions.recipe_id "+ 
    "JOIN RecipeCategories ON RecipeCategories.recipe_id=CBUserPredictions.recipe_id "+
    "JOIN JDFCategory ON JDFCategory.category_id=RecipeCategories.category_id "+
    "AND JDFCategory.is_main=1 AND JDFCategory.category_name=? "+
    "WHERE CBUserPredictions.user_id=? AND portion_calorie BETWEEN 0 AND ? "+
    "AND portion_fat BETWEEN 0 AND ? AND portion_protein BETWEEN 0 AND ? "+
    "AND portion_carbo BETWEEN 0 AND ? "+
    "ORDER BY CBUserPredictions.prediction DESC, Recipe.portion_calorie DESC "+
    "LIMIT ?";

	RecipeDAOImpl(DAOFactory daoFactory) {
		this.daoFactory = daoFactory;
	}

	@Override
	public Recipe findByName(String recipeName) throws DAOException {
		Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Recipe recipe = null;

        try {
            // Recuperation d'une connexion depuis la Factory 
            connexion = daoFactory.getConnection();
            preparedStatement = initializationPreparedRequest( connexion, SQL_SELECT_BY_NAME, false, recipeName );
            resultSet = preparedStatement.executeQuery();
            // Parcours de la ligne de donnees de l'eventuel ResulSet retourne 
            if ( resultSet.next() ) {
                recipe = map( resultSet );
                addAllOtherInfos(recipe);
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            silentClosures( resultSet, preparedStatement, connexion );
        }

        return recipe;
	}

    @Override
    public List<Long> findAllRecipeIds() throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<Long> recipe_ids = new ArrayList<>();

        try {
            connexion = daoFactory.getConnection();
            preparedStatement = initializationPreparedRequest(connexion, SQL_SELECT_ALL_IDS, false);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                recipe_ids.add(new Long(resultSet.getInt("recipe_id")));
            }
        } catch (SQLException e) {
            throw new DAOException (e);
        } finally {
            silentClosures (resultSet, preparedStatement, connexion);
        }

        return recipe_ids;
    }

	@Override
	public Recipe findById(int id) {
		Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Recipe recipe = null;

        try {
            // Recuperation d'une connexion depuis la Factory 
            connexion = daoFactory.getConnection();
            preparedStatement = initializationPreparedRequest( connexion, SQL_SELECT_BY_ID, false, id );
            resultSet = preparedStatement.executeQuery();
            // Parcours de la ligne de donnees de l'eventuel ResulSet retourne 
            if ( resultSet.next() ) {
                recipe = map( resultSet );
                addAllOtherInfos(recipe);
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            silentClosures( resultSet, preparedStatement, connexion );
        }

        return recipe;
	}

	@Override
	public List<Recipe> findByIds( List<Long> ids ){
        List<Recipe> recipeList = new ArrayList<Recipe>();
        for(int i = 0 ; i<ids.size() ; ++i){
            recipeList.add(findById((int) (long) ids.get(i)));
        }
        return recipeList;
    }

	@Override
	public List<Recipe> findRecipeWithLessThanLevelsOrderByPredictionsWithLimit(float energy, float fat, float proteins, float carbohydrates, int limit, long user_id, String category){
		List<Recipe> recipes = new ArrayList<Recipe>();
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connexion = daoFactory.getConnection();
            if(category.equals("None")){
                preparedStatement = initializationPreparedRequest( connexion, SQL_SELECT_LESS_THAN_LEVELS_ORDER_BY_CB_PREDICTIONS_WITH_LIMIT, false, user_id, energy, fat, proteins, carbohydrates,limit);
            }else{
                preparedStatement = initializationPreparedRequest( connexion, SQL_SELECT_LESS_THAN_LEVELS_AND_CATEGORY_ORDER_BY_CB_PREDICTIONS_WITH_LIMIT, false, category, user_id, energy, fat, proteins, carbohydrates, limit);
            }
            resultSet = preparedStatement.executeQuery();
            while ( resultSet.next() ) {
            	Recipe recipe = map(resultSet);
            	addAllOtherInfos(recipe);
                recipes.add(recipe);
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            silentClosures( resultSet, preparedStatement, connexion );
        }

        return recipes;
    }
	

	@Override
    public List<Recipe> findRecipeWithLessThanLevels(float energy, float fat, float proteins, float carbohydrates, String category) throws DAOException {
        List<Recipe> recipes = new ArrayList<Recipe>();
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connexion = daoFactory.getConnection();
            if(category.equals("None")){
                preparedStatement = initializationPreparedRequest( connexion, SQL_SELECT_LESS_THAN_LEVELS, false, energy, fat, proteins, carbohydrates);
            }else{
                preparedStatement = initializationPreparedRequest( connexion, SQL_SELECT_LESS_THAN_LEVELS_AND_CATEGORY, false, energy, fat, proteins, carbohydrates, category);
            }
            resultSet = preparedStatement.executeQuery();
            while ( resultSet.next() ) {
            	Recipe recipe = map(resultSet);
            	//addAllOtherInfos(recipe);
                recipes.add(recipe);
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            silentClosures( resultSet, preparedStatement, connexion );
        }

        return recipes;
    }

    @Override
     public List<Long> getRecipeIdsByCategory(String categoryName) throws DAOException{
     	List<Long> ids = new ArrayList<Long>();
     	Connection connexion = null;
         PreparedStatement preparedStatement = null;
         ResultSet resultSet = null;
         try {
             connexion = daoFactory.getConnection();
             preparedStatement = initializationPreparedRequest( connexion, SQL_SELECT_IDS_BY_CATEGORY, false, categoryName);
             resultSet = preparedStatement.executeQuery();
             while ( resultSet.next() ) {
                 ids.add(resultSet.getLong("recipe_id"));
             }
         } catch ( SQLException e ) {
             throw new DAOException( e );
         } finally {
             silentClosures( resultSet, preparedStatement, connexion );
         }
 
         return ids;
     }

	private void addSubCategory(Recipe recipe) {
		Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            // Recuperation d'une connexion depuis la Factory 
            connexion = daoFactory.getConnection();
            preparedStatement = initializationPreparedRequest( connexion, SQL_SELECT_SUB_CAT_BY_ID, false, recipe.getId() );
            resultSet = preparedStatement.executeQuery();
            // Parcours de la ligne de donnees de l'eventuel ResulSet retourne 
            if ( resultSet.next() ) {
                recipe.setSubCategoryId( resultSet.getInt(1)); // Il reconnait pas le nom de la colone a cause du C. devant, d'ou l'indice 1 pr le get
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            silentClosures( resultSet, preparedStatement, connexion );
        }
	}

	private void addOriginId(Recipe recipe){
		Connection connexion = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
	    try {
	        // Recuperation d'une connexion depuis la Factory 
	        connexion = daoFactory.getConnection();
	        preparedStatement = initializationPreparedRequest( connexion, SQL_SELECT_ORIGIN_BY_ID, false, recipe.getId() );
	        resultSet = preparedStatement.executeQuery();
	        // Parcours de la ligne de donnees de l'eventuel ResulSet retourne 
	        if ( resultSet.next() ) {
	            recipe.setOriginId( resultSet.getInt("origin_id")); 
	        }
	    } catch ( SQLException e ) {
	        throw new DAOException( e );
	    } finally {
	        silentClosures( resultSet, preparedStatement, connexion );
	    }
	}

	private void addIngredientsIds(Recipe recipe){
		Connection connexion = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
	    List<Integer> ingIds = new ArrayList<Integer>();
	    try {
	        // Recuperation d'une connexion depuis la Factory 
	        connexion = daoFactory.getConnection();
	        preparedStatement = initializationPreparedRequest( connexion, SQL_SELECT_INGREDIENTS_IDS, false, recipe.getId() );
	        resultSet = preparedStatement.executeQuery();
	        // Parcours de la ligne de donnees de l'eventuel ResulSet retourne 
	        while ( resultSet.next() ) {
	            ingIds.add(resultSet.getInt("ingredient_id")); 
	        }
	    } catch ( SQLException e ) {
	        throw new DAOException( e );
	    } finally {
	        silentClosures( resultSet, preparedStatement, connexion );
	    }
		recipe.setIngredientsIds(ingIds);
	}

	private void addTagIds(Recipe recipe){
		Connection connexion = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
	    List<Integer> tagIds = new ArrayList<Integer>();
	    try {
	        // Recuperation d'une connexion depuis la Factory 
	        connexion = daoFactory.getConnection();
	        preparedStatement = initializationPreparedRequest( connexion, SQL_SELECT_TAG_IDS, false, recipe.getId() );
	        resultSet = preparedStatement.executeQuery();
	        // Parcours de la ligne de donnees de l'eventuel ResulSet retourne 
	        while ( resultSet.next() ) {
	            tagIds.add(resultSet.getInt("tag_id")); 
	        }
	    } catch ( SQLException e ) {
	        throw new DAOException( e );
	    } finally {
	        silentClosures( resultSet, preparedStatement, connexion );
	    }
		recipe.setTagIds(tagIds);
	}

	private void addAllOtherInfos(Recipe recipe) {
		addSubCategory(recipe);
		addOriginId(recipe);
		addIngredientsIds(recipe);
		addTagIds(recipe);
	}


	private static Recipe map(ResultSet resultSet) throws SQLException {
		Recipe recipe = new Recipe();
		recipe.setId( new Long(resultSet.getInt( "recipe_id" )) );
        recipe.setUrl( resultSet.getString( "recipe_url" ) );
        recipe.setProductName( resultSet.getString( "recipe_name" ) );
        recipe.setImageUrl( resultSet.getString( "recipe_image_url" ) );
        recipe.setTotalEnergy(resultSet.getFloat( "portion_calorie" ) );
        recipe.setTotalFat(resultSet.getFloat( "portion_fat" ) ) ;
        recipe.setTotalProteins(resultSet.getFloat( "portion_protein" ) )  ;
        recipe.setTotalCarbohydrates(resultSet.getFloat( "portion_carbo" )  );
        recipe.notEaten();
        recipe.setImagePath(resultSet.getString("image_pic"));
        recipe.setQuantity(Integer.toString(resultSet.getInt("portions")));
        return recipe;
	}

}