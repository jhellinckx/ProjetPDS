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
	private static final String SQL_SELECT_BY_NAME = "SELECT id, url, code, productName FROM OpenFoodFact WHERE productName = ?";
	private static final String SQL_SELECT_BY_CODE = "SELECT id, url, code, productName FROM OpenFoodFact WHERE code = ?";
	private static final String SQL_SELECT_ALL = "SELECT id, url, code, productName FROM Food";

	FoodDAOImpl( DAOFactory daoFactory ) {
		this.daoFactory = daoFactory;
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
	/* Juse utilisee pr les test at the moment */
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
                int idFood = resultSet.getInt( "id" );
                String urlFood = resultSet.getString( "url" );
                String codeFood = resultSet.getString( "code" );
                String productNameFood = resultSet.getString( "productName" );
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

	private Food find( String sqlQuery, String parameter ){
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
        food.setId( resultSet.getLong( "id" ) );
        food.setUrl( resultSet.getString( "url" ) );
        food.setCode( resultSet.getString( "code" ) );
        food.setProductName( resultSet.getString( "productName" ) );
        return food;
    }
}
