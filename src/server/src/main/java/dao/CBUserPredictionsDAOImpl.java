package dao;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.AbstractMap;
import recommender.NearestNeighboursPrediction;
import static dao.DAOUtilitaire.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

public class CBUserPredictionsDAOImpl implements CBUserPredictionsDAO{
	public static final String SQL_COUNT_NEEDED_PREDICTIONS_FOR_USER = "SELECT COUNT(*) AS total FROM CBUserPredictions WHERE user_id=?";
	public static final String SQL_DELETE_PREDICTION = "DELETE FROM CBUserPredictions WHERE recipe_id=?";
	public static final String SQL_SELECT_ALL_PREDICTIONS_FOR_USER = "SELECT prediction_id, user_id, recipe_id, prediction FROM CBUserPredictions WHERE user_id=?";
	public static final String SQL_UPDATE_PREDICTION = 
	"INSERT INTO CBUserPredictions (prediction_id, user_id, recipe_id, prediction) VALUES (?, ?, ?, ?)"+
	" ON DUPLICATE KEY UPDATE prediction=VALUES(prediction)";
	public static final String SQL_SELECT_ALL_PREDICTION_RATING_PAIRS_WITH_SIMILARITY =
	"SELECT prediction_id, similarity, rank, recipe_id, numRecipe from CBUserPredictions "+
	"JOIN User_Preferences ON numUser=CBUserPredictions.user_id " +
	"JOIN RecipeSimilarity ON first_recipe_id=CBUserPredictions.recipe_id "+
	"AND second_recipe_id=User_preferences.numRecipe "+
	"WHERE CBUserPredictions.user_id=? ORDER BY recipe_id, similarity DESC";

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

	public void updatePredictions(List<NearestNeighboursPrediction> preds){
		Connection connexion = null;
        PreparedStatement preparedStatement = null;
        try {
        	connexion = _daoFactory.getConnection();
        	for(NearestNeighboursPrediction pred : preds){
	            preparedStatement = initializationPreparedRequest(connexion, SQL_UPDATE_PREDICTION, false, pred.getPredictionID(), pred.getUserID(), pred.getRecipeID(), pred.getPrediction());
	            int statut = preparedStatement.executeUpdate();
	            if ( statut == 0 ) {
	                throw new DAOException( "Failed to update prediction, no modifications to the table." );  
	            }
	        }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            silentClosures( preparedStatement, connexion );
        }
	}

	public int getNumberOfNeededPredictionsForUser(Long user_id){
		Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        int count = -1;
        try {
        	connexion = _daoFactory.getConnection();
            preparedStatement = initializationPreparedRequest(connexion, SQL_COUNT_NEEDED_PREDICTIONS_FOR_USER, false, user_id);
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
            	count = resultSet.getInt("total");
            }
            if(count == -1){
            	throw new DAOException("Could not get count of predictions for user");
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            silentClosures( preparedStatement, connexion );
        }
        return count;
	}

	public List<NearestNeighboursPrediction> getAllNearestNeighboursPredictionsForUser(Long user_id){
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<NearestNeighboursPrediction> predictions = 
		new ArrayList<NearestNeighboursPrediction>(getNumberOfNeededPredictionsForUser(user_id));
		System.out.println("NEEDED PREDICTIONS = " + Integer.toString(getNumberOfNeededPredictionsForUser(user_id)));
		try {
        	connexion = _daoFactory.getConnection();
            preparedStatement = initializationPreparedRequest(connexion, SQL_SELECT_ALL_PREDICTION_RATING_PAIRS_WITH_SIMILARITY, false, user_id);
           	resultSet = preparedStatement.executeQuery();
            Integer previous = -1;
            Integer current;
            NearestNeighboursPrediction currentPrediction = new NearestNeighboursPrediction(0, 0, 0);
            while(resultSet.next()){
            	current = resultSet.getInt("recipe_id");
            	if(!current.equals(previous)){
            		currentPrediction = new NearestNeighboursPrediction(new Long(resultSet.getInt("prediction_id")), new Long(current), user_id); 
            		predictions.add(currentPrediction);
            		previous = current;
            	}
            	if(!currentPrediction.hasMaxNeighbours()){
            		currentPrediction.addNeighbour(resultSet.getFloat("rank"), resultSet.getFloat("similarity"));
            	}
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            silentClosures( preparedStatement, connexion );
        }
		return predictions;
	}
}