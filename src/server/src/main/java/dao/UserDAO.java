package dao;

import java.util.List;

import org.calorycounter.shared.models.User;

public interface UserDAO {
	boolean create( User user ) throws DAOException;

    User findByUsername ( String username ) throws DAOException;
    
    User findById (Long id ) throws DAOException;
    
    List<String> findAll() throws DAOException; /* retourne une simple list de tous les user pour pouvoir print, utilise pr les test */

    List<User> findAllUsers() throws DAOException;

    List<User> findAllUsersWithRanks() throws DAOException;
    
    void delete( User user ) throws DAOException;

    void createRandomUsers(int quantity) throws DAOException;

    void quick_createRandomUsers(int quantity) throws DAOException;

    void updateUserWeight(User user, float weight) throws DAOException;

    void updateUserGender(User user, String gender) throws DAOException;

    void updateUserHeight(User user, float height) throws DAOException;

}
