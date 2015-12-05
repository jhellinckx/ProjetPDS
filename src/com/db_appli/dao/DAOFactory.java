package com.db_appli.dao;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;

public class DAOFactory {

    private static final String FILE_PROPERTIES          = "com/db_appli/dao/dao.properties";
    private static final String PROPERTY_URL             = "url";
    private static final String PROPERTY_DRIVER          = "driver";
    private static final String PROPERTY_USERNAME        = "username";
    private static final String PROPERTY_PASSWORD        = "password";

    BoneCP connectionPool = null;

    DAOFactory( BoneCP connectionPool ) {
        this.connectionPool = connectionPool;
    }

    /*
     * Mehode charge de reupeer les informations de connexion ela base de
     * donnes, charger le driver JDBC et retourner une instance de la Factory
     */
    public static DAOFactory getInstance() throws DAOConfigurationException {
        Properties properties = new Properties();
        String url;
        String driver;
        String userName;
        String passWord;
        BoneCP connectionPool = null;

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream fichierProperties = classLoader.getResourceAsStream( FILE_PROPERTIES );

        if ( fichierProperties == null ) {
            throw new DAOConfigurationException( "The properties file " + FILE_PROPERTIES + " cannot be found." );
        }

        try {
            properties.load( fichierProperties );
            url = properties.getProperty( PROPERTY_URL );
            driver = properties.getProperty( PROPERTY_DRIVER );
            userName = properties.getProperty( PROPERTY_USERNAME );
            passWord = properties.getProperty( PROPERTY_PASSWORD );
        } catch ( IOException e ) {
            throw new DAOConfigurationException( "Unable to load the properties files " + FILE_PROPERTIES, e );
        }
        
        try {
            Class.forName( "com.mysql.jdbc.Driver" );
        } catch ( ClassNotFoundException e ) {
            throw new DAOConfigurationException( "The driver cannot be found in the classpath.", e );
        }
        try {
            /*
             * Cretion d'une configuration de pool de connexions via l'objet
             * BoneCPConfig et les diffeents setters associe.
             */
            BoneCPConfig config = new BoneCPConfig();
            /* Mise en place de l'URL, du nom et du mot de passe */
            config.setJdbcUrl( url );
            config.setUsername( userName );
            config.setPassword( passWord );
            /* Paramerage de la taille du pool */
            config.setMinConnectionsPerPartition( 5 );
            config.setMaxConnectionsPerPartition( 10 );
            config.setPartitionCount( 2 );
            /* Cretion du pool epartir de la configuration, via l'objet BoneCP */
            connectionPool = new BoneCP( config );
        } catch ( SQLException e ) {
            e.printStackTrace();
            throw new DAOConfigurationException( "Erreur de configuration du pool de connexions.", e );
        }
        /*
         * Enregistrement du pool cre dans une variable d'instance via un appel
         * au constructeur de DAOFactory
         */
        DAOFactory instance = new DAOFactory( connectionPool );
        return instance;
    }
    
    /* Mehode charge de fournir une connexion ela base de donnes */
    Connection getConnection() throws SQLException {
    return connectionPool.getConnection();
    }

    /*
     * Mehodes de reupeation de l'impleentation des diffeents DAO (un seul
     * pour le moment)
     */
    public UserDAO getUtilisateurDao() {
        return new UserDAOImpl( this );
    }
}