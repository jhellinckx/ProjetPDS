package dao;

import java.util.List;

public interface SportsDAO{

	List<String> findSportsNames() throws DAOException;		// Get all the names in the db.

	Float findJouleByNameAndWeight(String name, int weight) throws DAOException;
}