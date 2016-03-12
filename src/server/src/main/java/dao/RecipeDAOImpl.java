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
	private static final String SQL_SELECT_BY_NAME = "SELECT recipe_id, recipe_name, recipe_image_url, recipe_url, ingredients_list, portion_calorie, portion_fat, portion_carbo, portion_protein FROM Recipe WHERE recipe_name = ?";
	private static final String SQL_SELECT_BY_ID = "SELECT recipe_id, recipe_name, recipe_image_url, recipe_url, ingredients_list, portion_calorie, portion_fat, portion_carbo, portion_protein FROM Recipe WHERE recipe_id = ?";
	private static final String SQL_SELECT_SUB_CAT_BY_ID = "SELECT C.category_id FROM RecipeCategories C, JDFCategory J WHERE C.recipe_id = ? AND C.category_id = J.category_id AND J.is_main = 0";
	private static final String SQL_SELECT_ORIGIN_BY_ID = "SELECT origin_id FROM RecipeOrigins WHERE recipe_id = ?";
	private static final String SQL_SELECT_INGREDIENTS_IDS = "SELECT ingredient_id FROM RecipeIngredients WHERE recipe_id = ?";
	private static final String SQL_SELECT_TAG_IDS = "SELECT tag_id FROM RecipeTags WHERE recipe_id = ?";

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
        return recipe;
	}

}