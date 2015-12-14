package dao;


import static dao.DAOUtilitaire.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import items.User;
import items.Food;

public class UserDAOImpl implements UserDAO {
	private DAOFactory daoFactory;
	private static final String SQL_SELECT_BY_USERNAME = "SELECT id_user, username, gender FROM User WHERE username = ?";
	private static final String SQL_SELECT_BY_ID = "SELECT id_user, username, gender FROM User WHERE id_user = ?";
	private static final String SQL_SELECT_ALL = "SELECT id_user, username, gender FROM User";
	private static final String SQL_INSERT = "INSERT INTO User (username, gender) VALUES (?, ?)";
	private static final String SQL_DELETE = "DELETE FROM User WHERE username = ?";
	
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
                userPrefDao = this.daoFactory.getUserPrefDao();
                user.setApprecitedFood(userPrefDao.findUserAppreciatedFood(user)); 
                user.setDepreciatedFood(userPrefDao.findUserDeppreciatedFood(user));
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
                userPrefDao = this.daoFactory.getUserPrefDao();
                user.setApprecitedFood(userPrefDao.findUserAppreciatedFood(user)); 
                user.setDepreciatedFood(userPrefDao.findUserDeppreciatedFood(user));
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
    public void create( User user ) throws IllegalArgumentException, DAOException {
    	Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet valeursAutoGenerees = null;
        UserPrefDAO userPrefDao = null;

        try {
            /* Recuperation d'une connexion depuis la Factory */
            connexion = daoFactory.getConnection();
            preparedStatement = initializationPreparedRequest( connexion, SQL_INSERT, true, user.getUsername(), user.getGender());
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
                userPrefDao = this.daoFactory.getUserPrefDao();
                addUserPrefsInTable(userPrefDao, user);
                }
             else {
                throw new DAOException( "Failed to create a user, no auto-generated ID returned." );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            silentClosures( valeursAutoGenerees, preparedStatement, connexion );
        }
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
	    	for ( Food food : user.getAppreciatedFood() ) {
	    		userPrefDao.create(user, food, "+");
	    	}
	    	for (Food food : user.getDepreciatedFood()) {
	    		userPrefDao.create(user, food, "-");
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
        return user;
    }
    
}	

