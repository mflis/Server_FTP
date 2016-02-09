package pl.agh.edu.database.utils;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.EbeanServer;
import lombok.extern.slf4j.Slf4j;
import pl.agh.edu.database.model.File;
import pl.agh.edu.database.model.Group;
import pl.agh.edu.database.model.User;
import pl.agh.edu.server.commands.ChmodCommand;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Optional;

@Slf4j
public class DatabaseOperations {
    private static final DatabaseOperations ourInstance = new DatabaseOperations();
    private final EbeanServer ebeanServer = Ebean.getDefaultServer();

    private DatabaseOperations() {
    }

    public static DatabaseOperations getInstance() {
        return ourInstance;
    }

    public boolean insertNewFileIfNotExists(String pathToFile, User loggedUser) {
        if (getFileIfExists(pathToFile).isPresent()) {
            log.info("Can't insert file, because already exists : " + pathToFile);
            return false;
        }

        File file = new File();
        file.setPathToFile(pathToFile)
                .setOwner(loggedUser)
                .setGroup(loggedUser.getGroupIfExists(loggedUser.getName()).get())
                .setUserRead(true)
                .setUserWrite(true)
                .setGroupRead(false)
                .setGroupWrite(false);

        ebeanServer.save(file);
        return true;

    }

    public boolean deleteFileIfExists(String pathToFile) {
        Optional<File> optionalFile = getFileIfExists(pathToFile);
        if (!optionalFile.isPresent()) {
            log.info("Can't delete file, because not exists : " + pathToFile);
            return false;
        }

        ebeanServer.delete(optionalFile.get());
        return true;
    }

    /**
     * @param user     user for which password will be validated. Must be entity from database
     * @param password password from PASS command
     */
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


    public void changePermissions(String filename, ChmodCommand.Permissions newPermissions) {
        Optional<File> fileOptional = getFileIfExists(filename);
        if (!fileOptional.isPresent()) {
            log.info("Such file doesn't exist: [{}] in changePermissions()", filename);
        }
        File file = fileOptional.get();
        file.setUserRead(newPermissions.ownerRead)
                .setUserWrite(newPermissions.ownerWrite)
                .setGroupRead(newPermissions.groupRead)
                .setGroupWrite(newPermissions.groupWrite);

        ebeanServer.save(file);
    }

    public boolean canUserReadFromFile(String filename, User user) {
        Optional<File> fileOptional = getFileIfExists(filename);
        if (!fileOptional.isPresent()) {
            log.info("Such file doesn't exist: [{}] in canUserReadFromFile()", filename);
            return false;
        }
        File file = fileOptional.get();

        return user.getName().equals("admin")
                || (isUserInGroup(file.getGroup(), user) && file.isGroupRead())
                || (file.isUserRead() && file.getOwner().getUserId() == user.getUserId());
    }

    private boolean isUserInGroup(Group group, User user) {
        for (Group userGroup : user.getGroups()) {
            if (group.getGroupId() == userGroup.getGroupId()) return true;
        }
        return false;
    }

    public boolean canUserWriteToFile(String filename, User user) {
        Optional<File> fileOptional = getFileIfExists(filename);
        if (!fileOptional.isPresent()) {
            log.info("Such file doesn't exist: [{}] in canUserWriteToFile()", filename);
            return false;
        }

        File file = fileOptional.get();

        return user.getName().equals("admin")
                || (file.isGroupWrite() && isUserInGroup(file.getGroup(), user))
                || (file.isUserWrite() && file.getOwner().getUserId() == user.getUserId());

    }

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


    private Optional<File> getFileIfExists(String pathToFile) {
        File file = ebeanServer.find(File.class)
                .where()
                .eq("pathToFile", pathToFile)
                .findUnique();

        if (file == null) {
            log.info("{} already exists", pathToFile);
            return Optional.empty();
        } else {
            return Optional.of(file);
        }
    }
}
