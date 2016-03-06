package dao;

import static dao.DAOUtilitaire.initializationPreparedRequest;
import static dao.DAOUtilitaire.silentClosures;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.calorycounter.shared.models.Food;
import util.ImageConverter;

public class FoodDAOImpl implements FoodDAO {
	private DAOFactory daoFactory;
	private static final String SQL_SELECT_BY_NAME = "SELECT id_food, url, quantity, code, product_name, image_url, image_pic, energy_100g, fat_100g, proteins_100g, saturated_fat_100g, carbohydrates_100g, sugars_100g, salt_100g FROM Food WHERE product_name = ?";
	private static final String SQL_SELECT_BY_CODE = "SELECT id_food, url, quantity, code, product_name, image_url, image_pic, energy_100g, fat_100g, proteins_100g, saturated_fat_100g, carbohydrates_100g, sugars_100g, salt_100g FROM Food WHERE code = ?";
	private static final String SQL_SELECT_BY_ID = "SELECT id_food, url, quantity, code, product_name, image_url, image_pic, energy_100g, fat_100g, proteins_100g, saturated_fat_100g, carbohydrates_100g, sugars_100g, salt_100g FROM Food WHERE id_food = ?";
    private static final String SQL_SELECT_BY_URL = "SELECT id_food, url, quantity, code, product_name, image_url, image_pic, energy_100g, fat_100g, proteins_100g, saturated_fat_100g, carbohydrates_100g, sugars_100g, salt_100g FROM Food WHERE image_url = ?";
	private static final String SQL_SELECT_ALL = "SELECT id_food, url, quantity, code, product_name, image_url, image_pic, energy_100g, fat_100g, proteins_100g, saturated_fat_100g, carbohydrates_100g, sugars_100g, salt_100g FROM Food";
    private static final String SQL_SELECT_LESS_THAN_LEVELS = "SELECT id_food, url, quantity, code, product_name, image_url, image_pic, energy_100g, fat_100g, proteins_100g, saturated_fat_100g, carbohydrates_100g, sugars_100g, salt_100g FROM Food where energy_100g BETWEEN 0 AND ? AND fat_100g <= ? AND proteins_100g <= ? AND saturated_fat_100g <= ? AND carbohydrates_100g <= ? AND sugars_100g <= ? AND salt_100g <= ? ORDER BY energy_100g DESC";
    private static final String SQL_SELECT_LESS_THAN_LEVELS_AND_CATEGORY = "SELECT id_food, url, quantity, code, product_name, image_url, image_pic, energy_100g, fat_100g, proteins_100g, saturated_fat_100g, carbohydrates_100g, sugars_100g, salt_100g FROM Food where energy_100g BETWEEN 0 AND ? AND fat_100g <= ? AND proteins_100g <= ? AND saturated_fat_100g <= ? AND carbohydrates_100g <= ? AND sugars_100g <= ? AND salt_100g <= ? AND categories LIKE ? ORDER BY energy_100g DESC";
    private static final String SQL_SELECT_ALL_BY_CATEGORY = "SELECT id_food, url, quantity, code, product_name, image_url, image_pic, energy_100g, fat_100g, proteins_100g, saturated_fat_100g, carbohydrates_100g, sugars_100g, salt_100g FROM Food where categories like ?";


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
    public List<Food> findFoodWithLessThanLevels(float energy, float fat, float proteins, float saturatedFat, float carbohydrates, float sugars, float salt, String category) throws DAOException {
        List<Food> foods = new ArrayList<Food>();
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connexion = daoFactory.getConnection();
            if(category.equals("None")){
                preparedStatement = initializationPreparedRequest( connexion, SQL_SELECT_LESS_THAN_LEVELS, false, energy, fat, proteins, saturatedFat, carbohydrates, sugars, salt);
            }else{
                category=category+'%';
                preparedStatement = initializationPreparedRequest( connexion, SQL_SELECT_LESS_THAN_LEVELS_AND_CATEGORY, false, energy, fat, proteins, saturatedFat, carbohydrates, sugars, salt, category);            }
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
        food.setTotalEnergy(resultSet.getFloat( "energy_100g" ) );
        food.setTotalFat(resultSet.getFloat( "fat_100g" ) ) ;
        food.setTotalProteins(resultSet.getFloat( "proteins_100g" ) )  ;
        food.setTotalSaturatedFat(resultSet.getFloat( "saturated_fat_100g" ) ) ;
        food.setTotalCarbohydrates(resultSet.getFloat( "carbohydrates_100g" )  );
        food.setTotalSugars(resultSet.getFloat( "sugars_100g" )  );
        food.setTotalSalt(resultSet.getFloat( "salt_100g" )  );
        food.setQuantity(resultSet.getString( "quantity" ));
        String filename = resultSet.getString( "image_pic" );
        food.setImagePic(ImageConverter.getBufferedImageFromFile(filename));
        return food;
    }
}
