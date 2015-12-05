package dao;

import java.util.List;

import items.User;

public interface UserDAO {
	void create( User user ) throws DAOException;

    User findByUsername ( String username ) throws DAOException;
    
    List<String> findAll() throws DAOException; /* retourne une simple list de tous les user pour pouvoir print, utilise pr les test */
    
    void delete( User user ) throws DAOException;
}
