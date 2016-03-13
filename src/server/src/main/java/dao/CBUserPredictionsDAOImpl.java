package dao;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import recommender.RecipePrediction;
import static dao.DAOUtilitaire.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

public class CBUserPredictionsDAOImpl implements CBUserPredictionsDAO{
	public static final String SQL_DELETE_PREDICTION = "DELETE FROM CBUserPredictions WHERE recipe_id=?";
	public static final String SQL_SELECT_ALL_PREDICTIONS_FOR_USER = "SELECT prediction_id, user_id, recipe_id, prediction FROM CBUserPredictions WHERE user_id=?";
	public static final String SQL_UPDATE_PREDICTION = 
	"INSERT INTO CBUserPredictions (prediction_id, user_id, recipe_id, prediction) VALUES (?, ?, ?, ?)"+
	" ON DUPLICATE KEY UPDATE prediction=VALUES(prediction)";
	public static final String SQL_SELECT_K_NEAREST_NEIGHBOURS_IN_USER_PROFILE =
	"SELECT rank, similarity FROM User_Preferences "+
	"JOIN RecipeSimilarity ON "+
	"(RecipeSimilarity.first_recipe_id=User_Preferences.numRecipe AND RecipeSimilarity.second_recipe_id=?) "+
	"OR (RecipeSimilarity.second_recipe_id=User_Preferences.numRecipe AND RecipeSimilarity.first_recipe_id=?) "+
	"WHERE User_Preferences.numUser=? "+
	"ORDER BY RecipeSimilarity.similarity DESC"+
	"LIMIT ?";
	private DAOFactory _daoFactory;

	CBUserPredictionsDAOImpl(DAOFactory fac){
		_daoFactory = fac;
	}

    private void deletePrediction(Long recipe_id) throws IllegalArgumentException, DAOException {
    	Connection connexion = null;
        PreparedStatement preparedStatement = null;
        try {
        	connexion = _daoFactory.getConnection();
            preparedStatement = initializationPreparedRequest(connexion, SQL_DELETE_PREDICTION, false, recipe_id);
            int statut = preparedStatement.executeUpdate();
            if ( statut == 0 ) {
                throw new DAOException( "Failed to delete prediction, no modifications to the table." );  
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            silentClosures( preparedStatement, connexion );
        }
    }

	public void removePredictions(List<Long> recipes_ids){
		for(Long id : recipes_ids){
			deletePrediction(id);
		}
	}

	public List<RecipePrediction> getAllPredictionsForUser(Long userID){
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<RecipePrediction> recipesPredictions = new ArrayList<RecipePrediction>();
		try {
        	connexion = _daoFactory.getConnection();
            preparedStatement = initializationPreparedRequest(connexion, SQL_SELECT_ALL_PREDICTIONS_FOR_USER, false, userID);
           	resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
            	recipesPredictions.add(mapPrediction(resultSet));
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            silentClosures( preparedStatement, connexion );
        }
		return recipesPredictions;
	}

	public void updatePredictions(List<RecipePrediction> preds){
		Connection connexion = null;
        PreparedStatement preparedStatement = null;
        try {
        	connexion = _daoFactory.getConnection();
        	for(RecipePrediction pred : preds){
	            preparedStatement = initializationPreparedRequest(connexion, SQL_UPDATE_PREDICTION, false, pred.getPredictionID(), pred.getUserID(), pred.getRecipeID(), pred.getPrediction());
	            int statut = preparedStatement.executeUpdate();
	            if ( statut == 0 ) {
	                throw new DAOException( "Failed to delete prediction, no modifications to the table." );  
	            }
	        }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            silentClosures( preparedStatement, connexion );
        }
	}

	public List<Map.Entry<Float, Float>> getNeighboursInUserProfileLimitK(Long recipe_id, Long, user_id, int k){
		return new ArrayList<Map.Entry<Float, Float>>();
	}

	private RecipePrediction mapPrediction(ResultSet result)throws SQLException{
		return new RecipePrediction(new Long(result.getInt("prediction_id")), new Long(result.getInt("recipe_id")), new Long(result.getInt("user_id")), result.getFloat("prediction"));
	}
}