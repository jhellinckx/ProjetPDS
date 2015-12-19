package dao;

import java.util.List;
import java.util.HashMap;
import items.User;
import items.Food;

public interface UserPrefDAO {
	
	void create( User user, Food food , String rank) throws DAOException;
	
	List<Food> findUserAppreciatedFood( User user ) throws DAOException;
	
	List<Food> findUserDeppreciatedFood(User user) throws DAOException;
	
	List<User> findUsersAppreciating( Food food ) throws DAOException;

	HashMap findUsersAndRankForFood(Food food) throws DAOException;

	HashMap findFoodsAndRankForUser(User user) throws DAOException;
	
	void delete (User user, Food food, String mark) throws DAOException;

}
