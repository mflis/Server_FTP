package pl.agh.edu.database.user;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.EbeanServer;
import lombok.extern.slf4j.Slf4j;
import pl.agh.edu.database.group.Group;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Optional;

/**
 * Singleton class for database operations concerning user
 */
@Slf4j
public enum UserDaoImpl implements UserDAO {
    INSTANCE;

    private static final EbeanServer ebeanServer = Ebean.getDefaultServer();


    @Override
    public boolean validatePassword(User user, String password) {
        if (user.getName().equals("anonymous")) {
            return true;
        }

        boolean isCorrectPassword = false;
        try {
            isCorrectPassword = PasswordHash.validatePassword(password, user.getPassword());
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.error("Error when validating password : ", e);
        }

        return isCorrectPassword;
    }

    @Override
    public boolean isUserInGroup(Group group, User user) {
        for (Group userGroup : user.getGroups()) {
            if (group.getGroupId() == userGroup.getGroupId()) return true;
        }
        return false;
    }

    @Override
    public Optional<User> getUserIfExists(String username) {
        User user = ebeanServer.find(User.class)
                .where()
                .eq("name", username)
                .findUnique();

        if (user == null) {
            return Optional.empty();
        } else {
            return Optional.of(user);
        }
    }
}
