package dao;

import org.calorycounter.shared.models.Recipe;

import java.util.Map;

/**
 * Created by aurelien on 5/05/16.
 */
public interface RecipeSimilarityDAO {

    public Map<Recipe, Float> getNearestNeighbor(long id, int k) throws DAOException;
}
