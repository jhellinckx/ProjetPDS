package dao;

import org.calorycounter.shared.models.Recipe;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static dao.DAOUtilitaire.initializationPreparedRequest;
import static dao.DAOUtilitaire.silentClosures;

/**
 * Created by aurelien on 5/05/16.
 */
public class RecipeSimilarityDAOImpl implements RecipeSimilarityDAO {

    private static final String SQL_GET_ALL = "SELECT second_recipe_id, similarity FROM RecipeSimilarity WHERE first_recipe_id = ? ORDER BY similarity DESC LIMIT ?";

    private DAOFactory daoFactory;

    public RecipeSimilarityDAOImpl(DAOFactory factory){
        this.daoFactory = factory;
    }

    @Override
    public Map<Recipe, Float> getNearestNeighbor(long id, int k){
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Map<Recipe, Float> neighborhood = new HashMap<>();
        RecipeDAO recipeDAO = daoFactory.getRecipeDAO();

        try {
            connexion = daoFactory.getConnection();
            preparedStatement = initializationPreparedRequest( connexion, SQL_GET_ALL, false, (int) id, k);
            resultSet = preparedStatement.executeQuery();
            while ( resultSet.next() ) {
                Recipe recipe = recipeDAO.findById(resultSet.getInt("second_recipe_id"));
                neighborhood.put(recipe, resultSet.getFloat("similarity"));
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            silentClosures( resultSet, preparedStatement, connexion );
        }
        return neighborhood;
    }
}
