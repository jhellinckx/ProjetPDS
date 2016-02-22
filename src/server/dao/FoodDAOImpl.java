package dao;

import static dao.DAOUtilitaire.initializationPreparedRequest;
import static dao.DAOUtilitaire.silentClosures;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import items.Food;
import items.User;

public class FoodDAOImpl implements FoodDAO {
	private DAOFactory daoFactory;
	private static final String SQL_SELECT_BY_NAME = "SELECT id_food, url, code, product_name, image_url, energy_100g FROM Food WHERE product_name = ?";
	private static final String SQL_SELECT_BY_CODE = "SELECT id_food, url, code, product_name, image_url, energy_100g FROM Food WHERE code = ?";
	private static final String SQL_SELECT_BY_ID = "SELECT id_food, url, code, product_name, image_url, energy_100g FROM Food WHERE id_food = ?";
    private static final String SQL_SELECT_BY_URL = "SELECT id_food, url, code, product_name, image_url, energy_100g FROM Food WHERE image_url = ?";
	private static final String SQL_SELECT_ALL = "SELECT id_food, url, code, product_name, image_url, energy_100g FROM Food";
    private static final String SQL_SELECT_LESS_THAN_LEVELS = "SELECT id_food, url, code, product_name, image_url, energy_100g FROM Food where total_energy BETWEEN 0 AND ? AND total_fat <= ? AND total_proteins <= ? AND total_saturated_fat <= ? AND total_carbohydrates <= ? AND total_sugars <= ? AND total_sodium <= ? ORDER BY total_energy DESC";

	FoodDAOImpl( DAOFactory daoFactory ) {
		this.daoFactory = daoFactory;
	}

    @Override
    public Food findByUrl(String url) throws DAOException{
        return find( SQL_SELECT_BY_URL, url);
    }
	
	
	@Override
	public Food findByName(String productName) throws DAOException {
        return find( SQL_SELECT_BY_NAME, productName );
	}
	
	
	@Override
	public Food findByCode(String code) throws DAOException {
		return find( SQL_SELECT_BY_CODE, code );
	}
	
	@Override
	public Food findById(Long id) throws DAOException {
        return find( SQL_SELECT_BY_ID, id );
	}

    @Override
    public List<Food> findByIds(List<Long> ids) throws DAOException{
        return find(SQL_SELECT_BY_ID, ids);
    }
	
    @Override
    public List<Food> findFoodWithLessThanLevels(float energy, float fat, float proteins, float saturatedFat, float carbohydrates, float sugars, float sodium) throws DAOException {
        List<Food> foods = new ArrayList<Food>();
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connexion = daoFactory.getConnection();
            preparedStatement = initializationPreparedRequest( connexion, SQL_SELECT_LESS_THAN_LEVELS, false, energy, fat, proteins, saturatedFat, carbohydrates, sugars, sodium);
            resultSet = preparedStatement.executeQuery();
            while ( resultSet.next() ) {
                foods.add(map(resultSet));
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            silentClosures( resultSet, preparedStatement, connexion );
        }

        return foods;
    }

	@Override
	/* Juste utilisee pr les test at the moment */
    public List<String> findAll() throws DAOException {
    	List<String> messages = new ArrayList<String>();
    	Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            /* Recuperation d'une connexion depuis la Factory */
            connexion = daoFactory.getConnection();
            preparedStatement = initializationPreparedRequest( connexion, SQL_SELECT_ALL, false);
            resultSet = preparedStatement.executeQuery();
            /* Parcours de la ligne de donnees de l'eventuel ResulSet retourne */
            while ( resultSet.next() ) {
                int idFood = resultSet.getInt( "id_food" );
                String urlFood = resultSet.getString( "url" );
                String codeFood = resultSet.getString( "code" );
                String productNameFood = resultSet.getString( "product_name" );
                /* Formatage des donnees pour affichage dans la JSP finale. */
                messages.add( "Donnees retournees par la requete : id = " + idFood + ", url = " + urlFood
                        + ", code = " + codeFood + ", productName = " + productNameFood + ". \n" );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            silentClosures( resultSet, preparedStatement, connexion );
        }

        return messages;
    }



    private List<Food> find( String sqlQuery, List<Long> parameter ){
        List<Food> foodList = new ArrayList<Food>();
        for(int i = 0 ; i<parameter.size() ; ++i){
            foodList.add(find(sqlQuery, parameter.get(i)));
        }
        return foodList;
    }
    
	private Food find( String sqlQuery, String parameter ){
		Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Food food = null;

        try {
            // Recuperation d'une connexion depuis la Factory 
            connexion = daoFactory.getConnection();
            preparedStatement = initializationPreparedRequest( connexion, sqlQuery, false, parameter );
            resultSet = preparedStatement.executeQuery();
            // Parcours de la ligne de donnees de l'eventuel ResulSet retourne 
            if ( resultSet.next() ) {
                food = map( resultSet );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            silentClosures( resultSet, preparedStatement, connexion );
        }

        return food;
	}
	


	private Food find( String sqlQuery, Long parameter ){
		Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Food food = null;

        try {
            /* Recuperation d'une connexion depuis la Factory */
            connexion = daoFactory.getConnection();
            preparedStatement = initializationPreparedRequest( connexion, sqlQuery, false, parameter );
            resultSet = preparedStatement.executeQuery();
            /* Parcours de la ligne de donnees de l'eventuel ResulSet retourne */
            if ( resultSet.next() ) {
                food = map( resultSet );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            silentClosures( resultSet, preparedStatement, connexion );
        }

        return food;
	}
	
	/*
     * Simple methode utilitaire permettant de faire la correspondance (le
     * mapping) entre une ligne issue de la table des food (un
     * ResultSet) et un item Food.
     */
    private static Food map( ResultSet resultSet ) throws SQLException {
        Food food = new Food();
        food.setId( resultSet.getLong( "id_food" ) );
        food.setUrl( resultSet.getString( "url" ) );
        food.setCode( resultSet.getString( "code" ) );
        food.setProductName( resultSet.getString( "product_name" ) );
        food.setImageUrl( resultSet.getString( "image_url" ) );
        food.setEnergy100g( resultSet.getString( "energy_100g" ) );

        return food;
    }
}
