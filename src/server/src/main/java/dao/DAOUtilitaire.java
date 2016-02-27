package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class DAOUtilitaire {

    /*
     * Constructeur cache par defaut (car c'est une classe finale utilitaire,
     * contenant uniquement des methodes appelees de maniere statique)
     */
    private DAOUtilitaire() {
    }
    
    /*
     * Initialise la requete preparee basee sur la connection passee en argument,
     * avec la requete SQL et les objets donnes.
     */
    public static PreparedStatement initializationPreparedRequest( Connection connection, String sql, boolean returnGeneratedKeys, Object... objets ) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement( sql, returnGeneratedKeys ? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS );
        for ( int i = 0; i < objets.length; i++ ) {
            preparedStatement.setObject( i + 1, objets[i] );
        }
        return preparedStatement;
    }
    
    /* Fermeture silencieuse du resultset */
    public static void silentClosing( ResultSet resultSet ) {
        if ( resultSet != null ) {
            try {
                resultSet.close();
            } catch ( SQLException e ) {
                System.out.println( "Failed to close the ResultSet : " + e.getMessage() );
            }
        }
    }

    /* Fermeture silencieuse du statement */
    public static void silentClosing( Statement statement ) {
        if ( statement != null ) {
            try {
                statement.close();
            } catch ( SQLException e ) {
                System.out.println( "Failed to close the Statement : " + e.getMessage() );
            }
        }
    }

    /* Fermeture silencieuse de la connection */
    public static void silentClosing( Connection connection ) {
        if ( connection != null ) {
            try {
                connection.close();
            } catch ( SQLException e ) {
                System.out.println( "Failed to close the connection : " + e.getMessage() );
            }
        }
    }

    /* Fermetures silencieuses du statement et de la connection */
    public static void silentClosures( Statement statement, Connection connection ) {
        silentClosing( statement );
        silentClosing( connection );
    }

    /* Fermetures silencieuses du resultset, du statement et de la connection */
    public static void silentClosures( ResultSet resultSet, Statement statement, Connection connection ) {
        silentClosing( resultSet );
        silentClosing( statement );
        silentClosing( connection );
    }
}