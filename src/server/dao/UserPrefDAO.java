package dao;

import java.util.List;
import java.util.HashMap;
import items.User;
import items.Food;

public interface UserPrefDAO {
	
	void create( User user, Food food , float rank) throws DAOException;

	void create(Long id_user, Long id_food, float rank) throws IllegalArgumentException, DAOException;
	
	List<Food> findUserAppreciatedFood( User user ) throws DAOException;
	
	List<Food> findUserDeppreciatedFood(User user) throws DAOException;
	
	List<User> findUsersAppreciating( Food food ) throws DAOException;

	List<Float> findRankForFood(Food food) throws DAOException;

	HashMap findFoodsAndRankForUser(User user) throws DAOException;
	
	void delete (User user, Food food, float mark) throws DAOException;

}
