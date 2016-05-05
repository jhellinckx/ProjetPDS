package dao;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
import org.calorycounter.shared.Constants;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class DAOFactory {

    private static String FILE_PROPERTIES          = "";
    private static final String PROPERTY_URL             = "url";
    private static final String PROPERTY_DRIVER          = "driver";
    private static final String PROPERTY_USERNAME        = "username";
    private static final String PROPERTY_PASSWORD        = "password";

    BoneCP connectionPool = null;

    DAOFactory( BoneCP connectionPool ) {
        this.connectionPool = connectionPool;
    }


    public static void getPropertiesFile(){
        FILE_PROPERTIES = "dao.properties";
        //String path =  System.getProperty("user.dir");
        //FILE_PROPERTIES = path.substring(0, path.indexOf("src")) + "src/server/src/main/java/dao/dao.properties";


    }
    /*
     * Methode chargee de recuperer les informations de connexion a la base de
     * donnees, charger le driver JDBC et retourner une instance de la Factory
     */
    public static DAOFactory getInstance() throws DAOConfigurationException {
        Properties properties = new Properties();
        String url;
        String driver;
        String userName;
        String passWord;
        BoneCP connectionPool = null;

        if(FILE_PROPERTIES.isEmpty())
            getPropertiesFile();

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
             * Creation d'une configuration de pool de connexions via l'objet
             * BoneCPConfig et les differents setters associes.
             */
            BoneCPConfig config = new BoneCPConfig();
            /* Mise en place de l'URL, du nom et du mot de passe */
            config.setJdbcUrl( url );
            config.setUsername( userName );
            config.setPassword( passWord );
            /* Parametrage de la taille du pool */
            config.setMinConnectionsPerPartition( 5 );
            config.setMaxConnectionsPerPartition( 10 );
            config.setPartitionCount( 4 );
            /* Creation du pool a partir de la configuration, via l'objet BoneCP */
            connectionPool = new BoneCP( config );
        } catch ( SQLException e ) {
            e.printStackTrace();
            throw new DAOConfigurationException( "Erreur de configuration du pool de connexions.", e );
        }
        /*
         * Enregistrement du pool cree dans une variable d'instance via un appel
         * au constructeur de DAOFactory
         */
        DAOFactory instance = new DAOFactory( connectionPool );
        return instance;
    }
    
    /* Methode chargee de fournir une connexion a la base de donnees */
    Connection getConnection() throws SQLException {
    return connectionPool.getConnection();
    }

    /*
     * Methodes de recuperation de l'implementation des differents DAO
     */
    public UserDAO getUserDAO() {
        return new UserDAOImpl( this );
    }
    
    public FoodDAO getFoodDAO() {
    	return new FoodDAOImpl( this );
    }
    
    public UserPrefDAO getUserPrefDAO() {
    	return new UserPrefDAOImpl ( this );
    }

    public SportsDAO getSportsDAO(){
        return new SportsDAOImpl( this );
    }

    public CategoryRatingDAO getCategoryRatingDAO(){
        return new CategoryRatingDAOImpl( this );
    }

    public UserHistoryDAO getUserHistoryDAO() {
        return new UserHistoryDAOImpl( this );
    }

    public AllCategoriesDAO getAllCategoriesDAO() {
        return new AllCategoriesDAOImpl( this );
    }

    public RecipeDAO getRecipeDAO() {
        return new RecipeDAOImpl(this);
    }

    public CBUserPredictionsDAO getCBPredictionsDAO(){
        return new CBUserPredictionsDAOImpl(this);
    }

    public RecipeSimilarityDAO getRecipeSimilarityDAO() { return new RecipeSimilarityDAOImpl(this);}
}