package dao;

import java.util.List;

import org.calorycounter.shared.models.Food;

public interface FoodDAO {
	Food findByUrl ( String url ) throws DAOException;

	Food findByName ( String productName ) throws DAOException;
	
	Food findByCode ( String code ) throws DAOException;
	
	Food findById (Long id ) throws DAOException;

	List<Food> findByIds(List<Long> ids) throws DAOException;

	List<Food> findFoodWithLessThanLevels(float energy, float fat, float proteins, float saturatedFat, float carbohydrates, float sugars, float salt, String category) throws DAOException;
	
	List<String> findAll() throws DAOException; /* retourne une simple list de tous les user pour pouvoir print, utilise pr les test */
}
