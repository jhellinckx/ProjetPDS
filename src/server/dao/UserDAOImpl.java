package dao;


import static dao.DAOUtilitaire.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import java.util.Iterator;
import java.util.Map;

import items.User;
import items.Food;
import items.Random_user_generator;

public class UserDAOImpl implements UserDAO {
	private DAOFactory daoFactory;
	private static final String SQL_SELECT_BY_USERNAME = "SELECT id_user, username, gender, weight FROM User WHERE username = ?";
	private static final String SQL_SELECT_BY_ID = "SELECT id_user, username, gender, weight FROM User WHERE id_user = ?";
	private static final String SQL_SELECT_ALL = "SELECT id_user, username, gender, weight FROM User";
	private static final String SQL_INSERT = "INSERT INTO User (username, gender, weight) VALUES (?, ?, ?)";
	private static final String SQL_DELETE = "DELETE FROM User WHERE username = ?";
    private static final String SQL_UPDATE_WEIGHT = "UPDATE User SET weight = ? WHERE id_user = ?";
    private static final String SQL_UPDATE_GENDER = "UPDATE User SET gender = ? WHERE id_user = ?";
	
	UserDAOImpl( DAOFactory daoFactory) {
		this.daoFactory = daoFactory;
	}
	
	/* Implementation de la methode findByUsername() definie dans l'interface UserDao */
    @Override
    public User findByUsername( String username ) throws DAOException {
    	Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        User user = null;
        UserPrefDAO userPrefDao = null;

        try {
            /* Recuperation d'une connexion depuis la Factory */
            connexion = daoFactory.getConnection();
            preparedStatement = initializationPreparedRequest( connexion, SQL_SELECT_BY_USERNAME, false, username );
            resultSet = preparedStatement.executeQuery();
            /* Parcours de la ligne de donnees de l'eventuel ResulSet retourne */
            if ( resultSet.next() ) {
                user = map( resultSet );
                userPrefDao = this.daoFactory.getUserPrefDAO();

                user.setRankedFoods(userPrefDao.findFoodsAndRankForUser(user));
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            silentClosures( resultSet, preparedStatement, connexion );
        }

        return user;
    }
    
    @Override
    public User findById( Long id ) throws DAOException {
    	Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        User user = null;
        UserPrefDAO userPrefDao = null;

        try {
            /* Recuperation d'une connexion depuis la Factory */
            connexion = daoFactory.getConnection();
            preparedStatement = initializationPreparedRequest( connexion, SQL_SELECT_BY_ID, false, id );
            resultSet = preparedStatement.executeQuery();
            /* Parcours de la ligne de donnees de l'eventuel ResulSet retourne */
            if ( resultSet.next() ) {
                user = map( resultSet );
                userPrefDao = this.daoFactory.getUserPrefDAO();

                user.setRankedFoods(userPrefDao.findFoodsAndRankForUser(user));
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            silentClosures( resultSet, preparedStatement, connexion );
        }

        return user;
    }
    
    @Override
    public List<String> findAll() throws DAOException {
    	List<String> messages = new ArrayList<String>();
    	Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        User user = null; /* pas encore use psq ici je fais msg pour un System.out.println & non un return user */

        try {
            /* Recuperation d'une connexion depuis la Factory */
            connexion = daoFactory.getConnection();
            preparedStatement = initializationPreparedRequest( connexion, SQL_SELECT_ALL, false);
            resultSet = preparedStatement.executeQuery();
            /* Parcours de la ligne de donnees de l'eventuel ResulSet retourne */
            while ( resultSet.next() ) {
                int idUser = resultSet.getInt( "id_user" );
                String usernameUser = resultSet.getString( "username" );
                String genderUser = resultSet.getString( "gender" );
                /* Formatage des donnees pour affichage dans la JSP finale. */
                messages.add( "Donnees retournees par la requ?e : id = " + idUser + ", username = " + usernameUser
                        + ", gender = "
                        + genderUser + ". \n" );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            silentClosures( resultSet, preparedStatement, connexion );
        }

        return messages;
    }

    /* Implementation de la methode create() definie dans l'interface UserDao */
    @Override
    public boolean create( User user ) throws IllegalArgumentException, DAOException {
    	Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet valeursAutoGenerees = null;
        UserPrefDAO userPrefDao = null;
        boolean error = false;
        try {
            /* Recuperation d'une connexion depuis la Factory */
            connexion = daoFactory.getConnection();
            preparedStatement = initializationPreparedRequest( connexion, SQL_INSERT, true, user.getUsername(), user.getGender(), user.getWeight());
            int statut = preparedStatement.executeUpdate();
            /* Analyse du statut retourne par la requetee d'insertion */
            if ( statut == 0 ) {
                throw new DAOException( "Failed to create a user, no new line added to the table." );
            }
            /* Recuperation de l'id auto-genere par la requetee d'insertion */
            valeursAutoGenerees = preparedStatement.getGeneratedKeys();
            if ( valeursAutoGenerees.next() ) {
                /* Puis initialisation de la propriete id du item User avec sa valeur */
                user.setId( valeursAutoGenerees.getLong( 1 ) );
                /* cree dans la table User_preferences les liaisons de l'User avec les food qu'il aime bien/aime pas */
                userPrefDao = this.daoFactory.getUserPrefDAO();
                addUserPrefsInTable(userPrefDao, user);
                }
             else {
                throw new DAOException( "Failed to create a user, no auto-generated ID returned." );
            }
        } 
        catch (MySQLIntegrityConstraintViolationException e){
            System.out.print("Failed to create a user, username already exists\n");
            error = true;
        }
        catch ( SQLException e ) {
            throw new DAOException( e );
        } 
        finally {
            silentClosures( valeursAutoGenerees, preparedStatement, connexion );
        }
        return error;
    }
    
    @Override
    public void delete( User user ) throws IllegalArgumentException, DAOException {
    	Connection connexion = null;
        PreparedStatement preparedStatement = null;

        try {
            /* Recuperation d'une connexion depuis la Factory */
        	connexion = daoFactory.getConnection();
            preparedStatement = initializationPreparedRequest( connexion, SQL_DELETE, false, user.getUsername());

            int statut = preparedStatement.executeUpdate();
            /* Analyse du statut retourne par la requete d'insertion */
            if ( statut == 0 ) {
                throw new DAOException( "Failed to delete the user, no modifications to the table." );  
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            silentClosures( preparedStatement, connexion );
        }
    }
 
    
    private void addUserPrefsInTable(UserPrefDAO userPrefDao, User user ) { 
    	try {
            Iterator it = user.getRankedFoods().entrySet().iterator();
            while(it.hasNext()){
                Map.Entry entry = (Map.Entry)it.next();
                userPrefDao.create(user, (Food)entry.getKey(), (float)entry.getValue());
            }
    	}  catch (NullPointerException e) {
	    }
    }
    

    
    /*
     * Simple methode utilitaire permettant de faire la correspondance (le
     * mapping) entre une ligne issue de la table des users (un
     * ResultSet) et un bean User.
     */
    private static User map( ResultSet resultSet ) throws SQLException {
        User user = new User();
        user.setId( resultSet.getLong( "id_user" ) );
        user.setUsername( resultSet.getString( "username" ) );
        user.setGender( resultSet.getString( "gender" ) );
        user.setWeight( resultSet.getFloat( "weight" ) );
        return user;
    }

    
    @Override
    public void createRandomUsers(int quantity) throws DAOException{
        FoodDAO foodDao = this.daoFactory.getFoodDAO();
        Random_user_generator r = new Random_user_generator(quantity, foodDao, false);
        ArrayList<User> randUserList = r.get_random_users_list();
        
        for (int i = 0 ; i<randUserList.size() ; ++i){
             create(randUserList.get(i));
        }
    }

    @Override
    public void quick_createRandomUsers(int quantity) throws DAOException{
        //Avoid creation of Food Objects for inserting into User_preferences
        FoodDAO foodDao = this.daoFactory.getFoodDAO();
        Random_user_generator r = new Random_user_generator(quantity, foodDao, true);
        ArrayList<User> randUserList = r.get_random_users_list();
        
        
        for (int i = 0 ; i<randUserList.size() ; ++i){
             create(randUserList.get(i));
        }
        
        UserPrefDAO userPrefDao = this.daoFactory.getUserPrefDAO();
        List<Long> Ids = r.generateFoodIds();
        for (int i = 0 ; i<randUserList.size() ; ++i){
            Ids = r.generateFoodIds();
            for(int j = 0 ; j<Ids.size() ; ++j){
                float rank  = r.generateRandomRank();
                userPrefDao.create(randUserList.get(i).getId(),Ids.get(j),rank);
            }
        }
    }

    @Override
    public void updateUserWeight(User user, float weight) throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;

        try {
            /* Recuperation d'une connexion depuis la Factory */
            connexion = daoFactory.getConnection();
            preparedStatement = initializationPreparedRequest( connexion, SQL_UPDATE_WEIGHT, false, weight, user.getId() );
            int statut = preparedStatement.executeUpdate();
            /* Analyse du statut retourne par la requetee d'insertion */
            if ( statut == 0 ) {
                throw new DAOException( "Failed to create a user, no new line added to the table." );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            silentClosures( preparedStatement, connexion );
        }
    }

    @Override
    public void updateUserGender(User user, String gender) throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;

        try {
            /* Recuperation d'une connexion depuis la Factory */
            connexion = daoFactory.getConnection();
            preparedStatement = initializationPreparedRequest( connexion, SQL_UPDATE_GENDER, false, gender, user.getId() );
            int statut = preparedStatement.executeUpdate();
            /* Analyse du statut retourne par la requetee d'insertion */
            if ( statut == 0 ) {
                throw new DAOException( "Failed to create a user, no new line added to the table." );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            silentClosures( preparedStatement, connexion );
        }
    } 
}	

