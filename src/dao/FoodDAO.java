package dao;

import java.util.List;

import items.Food;

public interface FoodDAO {
	Food findByName ( String productName ) throws DAOException;
	
	Food findByCode ( String code ) throws DAOException;
	
	List<String> findAll() throws DAOException; /* retourne une simple list de tous les user pour pouvoir print, utilise pr les test */
}
