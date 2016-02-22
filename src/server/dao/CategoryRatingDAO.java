package dao;

import items.CategoryRating;
import items.User;
import items.Food;

import java.util.ArrayList;
import java.util.Arrays;


public interface CategoryRatingDAO {

	public CategoryRating findRatedCategory(User user, String category) throws DAOException;
	public void addRatingForCategory(User user, String category, float rating, int n) throws DAOException;
	public ArrayList<CategoryRating> findAllRatedCategoriesForUser(User user) throws DAOException;
	public ArrayList<String> findCategoriesForFood(Food food) throws DAOException;

}