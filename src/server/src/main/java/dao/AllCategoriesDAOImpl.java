package dao;

import static dao.DAOUtilitaire.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;


public class AllCategoriesDAOImpl implements AllCategoriesDAO {
	private DAOFactory daoFactory;
	private static final String SQL_SELECT_CATEGORIES = "SELECT category_name FROM All_categories";
    private static final String SQL_SELECT_RECIPE_CATEGORIES = "SELECT category_name FROM JDFCategory WHERE is_main = 1";

	AllCategoriesDAOImpl( DAOFactory daoFactory ) {
		this.daoFactory = daoFactory;
	}


	@Override
	public List<String> getAllFoodCategories() throws DAOException {
		return getAllCategories(SQL_SELECT_CATEGORIES);
	}

	@Override
	public List<String> getAllRecipeCategories() throws DAOException {
		return getAllCategories(SQL_SELECT_RECIPE_CATEGORIES);
	}
	
	private List<String> getAllCategories(String sqlQuery){
		List<String> categories = new ArrayList<String>();
		Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            /* Recuperation d'une connexion depuis la Factory */
            connexion = daoFactory.getConnection();
            preparedStatement = initializationPreparedRequest( connexion, sqlQuery, false);
            resultSet = preparedStatement.executeQuery();
            /* Parcours de la ligne de donnees de l'eventuel ResulSet retourne */
            while ( resultSet.next() ) {
                categories.add(resultSet.getString("category_name"));
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            silentClosures( resultSet, preparedStatement, connexion );
        }
        for(String s : categories){
        }
        return categories;
	}
}