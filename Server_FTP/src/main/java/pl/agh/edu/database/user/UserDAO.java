package pl.agh.edu.database.user;

import pl.agh.edu.database.group.Group;

import java.util.Optional;

public interface UserDAO {
    /**
     * @param user     user for which password will be validated. Must be entity from database
     * @param password password from PASS command
     * @return true when passwords match, false otherwise
     */
    boolean validatePassword(User user, String password);

    /**
     * check whether {@code user} belongs to {@code group}
     */
    boolean isUserInGroup(Group group, User user);

    /**
     * get object representing information about user form database if exists
     *
     * @param username identifies user to be fetched from database
     * @return Optional containing user when it exists in database
     */
    Optional<User> getUserIfExists(String username);

}

