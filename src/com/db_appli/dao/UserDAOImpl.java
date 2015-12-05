package com.db_appli.dao;


import static com.db_appli.dao.DAOUtilitaire.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.db_appli.beans.User;

public class UserDAOImpl implements UserDAO {
	private DAOFactory daoFactory;
	private static final String SQL_SELECT_BY_MAIL = "SELECT id, email, nom, mot_de_passe, date_inscription FROM Utilisateur WHERE email = ?";
	private static final String SQL_SELECT_ALL = "SELECT id, email, nom, mot_de_passe, date_inscription FROM Utilisateur";
	private static final String SQL_INSERT = "INSERT INTO Utilisateur (email, mot_de_passe, nom, date_inscription) VALUES (e, e, e, NOW())";
	
	UserDAOImpl( DAOFactory daoFactory) {
		this.daoFactory = daoFactory;
	}
	
	/* Impleentation de la mehode find() deinie dans l'interface UserDao */
    @Override
    public User find( String email, String nom ) throws DAOException {
    	Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        User user = null;

        try {
            /* Reupeation d'une connexion depuis la Factory */
            connexion = daoFactory.getConnection();
            preparedStatement = initializationPreparedRequest( connexion, SQL_SELECT_BY_MAIL, false, email);
            resultSet = preparedStatement.executeQuery();
            /* Parcours de la ligne de donnes de l'eentuel ResulSet retourne*/
            if ( resultSet.next() ) {
                user = map( resultSet );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            silentClosures( resultSet, preparedStatement, connexion );
        }

        return user;
    }
    
    @Override
    public List<String> find() throws DAOException {
    	List<String> messages = new ArrayList<String>();
    	Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        User user = null; /* pas encore use psq ici je fais msg pour un System.out.println & non un return user */

        try {
            /* Reupeation d'une connexion depuis la Factory */
            connexion = daoFactory.getConnection();
            preparedStatement = initializationPreparedRequest( connexion, SQL_SELECT_ALL, false);
            resultSet = preparedStatement.executeQuery();
            /* Parcours de la ligne de donnes de l'eentuel ResulSet retourne*/
            while ( resultSet.next() ) {
                int idUser = resultSet.getInt( "id" );
                String emailUser = resultSet.getString( "email" );
                String motDePasseUser = resultSet.getString( "mot_de_passe" );
                String nomUser = resultSet.getString( "nom" );
                /* Formatage des donnes pour affichage dans la JSP finale. */
                messages.add( "Donnes retournes par la requee : id = " + idUser + ", email = " + emailUser
                        + ", motdepasse = "
                        + motDePasseUser + ", nom = " + nomUser + ". \n" );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            silentClosures( resultSet, preparedStatement, connexion );
        }

        return messages;
    }

    /* Impleentation de la mehode create() deinie dans l'interface UserDao */
    @Override
    public void create( User user ) throws IllegalArgumentException, DAOException {
    	Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet valeursAutoGenerees = null;

        try {
            /* Reupeation d'une connexion depuis la Factory */
            connexion = daoFactory.getConnection();
            preparedStatement = initializationPreparedRequest( connexion, SQL_INSERT, true, user.getEmail(), user.getMotDePasse(), user.getNom() );
            int statut = preparedStatement.executeUpdate();
            /* Analyse du statut retournepar la requee d'insertion */
            if ( statut == 0 ) {
                throw new DAOException( "Failed to create a user, no new line added to the table." );
            }
            /* Reupeation de l'id auto-geeepar la requee d'insertion */
            valeursAutoGenerees = preparedStatement.getGeneratedKeys();
            if ( valeursAutoGenerees.next() ) {
                /* Puis initialisation de la proprieeid du bean User avec sa valeur */
                user.setId( valeursAutoGenerees.getLong( 1 ) );
            } else {
                throw new DAOException( "Failed to create a user, no auto-generated ID returned." );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            silentClosures( valeursAutoGenerees, preparedStatement, connexion );
        }
    }
    
    /*
     * Simple mehode utilitaire permettant de faire la correspondance (le
     * mapping) entre une ligne issue de la table des users (un
     * ResultSet) et un bean User.
     */
    private static User map( ResultSet resultSet ) throws SQLException {
        User user = new User();
        user.setId( resultSet.getLong( "id" ) );
        user.setEmail( resultSet.getString( "email" ) );
        user.setMotDePasse( resultSet.getString( "mot_de_passe" ) );
        user.setNom( resultSet.getString( "nom" ) );
        user.setDateInscription( resultSet.getTimestamp( "date_inscription" ) );
        return user;
    }
    
}	

