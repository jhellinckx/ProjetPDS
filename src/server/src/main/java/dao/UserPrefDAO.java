package dao;

import java.util.List;
import java.util.HashMap;
import org.calorycounter.shared.models.User;
import org.calorycounter.shared.models.Food;

public interface UserPrefDAO {
	
	void create( User user, Food food , float rank) throws DAOException;

	void create(Long id_user, Long id_food, float rank) throws IllegalArgumentException, DAOException;
	
	List<Food> findFoodsForUser(User user);
	
	List<User> findUsersAppreciating( Food food ) throws DAOException;

	List<Float> findRankForFood(Food food) throws DAOException;

	List<Food> findFoodsForUserAndRank( User user, float rank ) throws DAOException;

	HashMap findFoodsAndRankForUser(User user) throws DAOException;
	
	void delete (User user, Food food, float mark) throws DAOException;

}
