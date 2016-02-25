package dao;

import static dao.DAOUtilitaire.initializationPreparedRequest;
import static dao.DAOUtilitaire.silentClosures;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.lang.Math;


public class SportsDAOImpl implements SportsDAO{

	private static final float LOW_WEIGHT = 60;
	private static final float MID_WEIGHT = 70;
	private static final float HIGH_WEIGHT = 85;
	private static final String KEY_NAME = "joule_";
	private static final String WEIGHT_UNIT = "kg";

	private static final String SQL_SELECT_ALL_NAMES = "SELECT name FROM Sports";
	private static final String SQL_SELECT_JOULE_BY_NAME_AND_WEIGHT = "SELECT * FROM Sports WHERE name = ?";

	private DAOFactory daoFactory;

	public SportsDAOImpl(DAOFactory dfactory){
		daoFactory = dfactory;
	}


	@Override
	public List<String> findSportsNames() throws DAOException{
		Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ArrayList<String> names = new ArrayList<String>();

        try {
            // Recuperation d'une connexion depuis la Factory 
            connexion = daoFactory.getConnection();
            preparedStatement = initializationPreparedRequest( connexion, SQL_SELECT_ALL_NAMES, false);
            resultSet = preparedStatement.executeQuery();
            // Parcours de la ligne de donnees de l'eventuel ResulSet retourne 
            while ( resultSet.next() ) {
                names.add(resultSet.getString("name"));
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            silentClosures( resultSet, preparedStatement, connexion );
        }
        return names;
	}


	/*
	*   This method takes a weight and return one of the 3 constants defined above.
	*/

	private float getClosestWeight(float weight){

		if (weight <= LOW_WEIGHT) return LOW_WEIGHT;
		else if (weight >= HIGH_WEIGHT) return HIGH_WEIGHT;
		
		else {
			if (weight < MID_WEIGHT){
				if ((weight-LOW_WEIGHT) <= (MID_WEIGHT-weight)) return LOW_WEIGHT;		// If weight is closer to LOW_WEIGHT than to MID_WEIGHT
				else return MID_WEIGHT;
			}
			else{
				if ((weight-MID_WEIGHT) <= (HIGH_WEIGHT-weight)) return MID_WEIGHT;		// If weight is closer to MID_WEIGHT thant to HIGH_WEIGHT
			}
		}
		
		return HIGH_WEIGHT;
	}

	private String weightToKey(float weight){
		String key = KEY_NAME;
		key += Integer.toString(Math.round(weight)) + WEIGHT_UNIT;
		return key;
	}

	@Override
	public Float findJouleByNameAndWeight(String name, float weight) throws DAOException{
		
		Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
		String key = weightToKey(getClosestWeight(weight));
		String joule = null;


        try {
            // Recuperation d'une connexion depuis la Factory 
            connexion = daoFactory.getConnection();
            preparedStatement = initializationPreparedRequest( connexion, SQL_SELECT_JOULE_BY_NAME_AND_WEIGHT, false, name);
            resultSet = preparedStatement.executeQuery();
            // Parcours de la ligne de donnees de l'eventuel ResulSet retourne 
            if ( resultSet.next() ) {
                joule = resultSet.getString(key);
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            silentClosures( resultSet, preparedStatement, connexion );
        }

        return Float.parseFloat(joule);

	}
}