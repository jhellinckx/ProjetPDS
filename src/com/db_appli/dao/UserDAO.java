package com.db_appli.dao;

import java.util.List;

import com.db_appli.beans.User;

public interface UserDAO {
	void create( User user ) throws DAOException;

    User find ( String email ) throws DAOException;
    
    List<String> find() throws DAOException;
}
