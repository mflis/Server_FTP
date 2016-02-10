package pl.agh.edu.database.file;

import pl.agh.edu.database.user.User;
import pl.agh.edu.server.commands.ChmodCommand;

import java.util.Optional;

public interface FileDAO {
    /**
     * insert information about new file into database if it does not exist there already
     *
     * @param pathToFile absolute path of file to be inserted to database
     * @param loggedUser user creating new file(currently logged user). Object must be entity from database
     * @return true when insertion succeeded, false otherwise
     */
    boolean insertNewFileIfNotExists(String pathToFile, User loggedUser);

    /**
     * delete information about file from database if  exists
     *
     * @param pathToFile absolute path of file to be deleted from database
     * @return true when deletion succeeded, false otherwise
     */
    boolean deleteFileIfExists(String pathToFile);

    /**
     * change permissions to file
     *
     * @param filename       absolute path to file for which permissions are changed
     * @param newPermissions new set of permissions to file
     */
    void changePermissions(String filename, ChmodCommand.Permissions newPermissions);

    /**
     * get object representing information about file form database if exists
     *
     * @param pathToFile absolute path of file to fetch from database
     * @return Optional containing file when it exists in database
     */
    Optional<File> getFileIfExists(String pathToFile);

    /**
     * checks whether user has read permissions to file
     *
     * @param filename absolute path of file being checked
     * @param user     user being checked(currently logged user)
     */
    boolean canUserReadFromFile(String filename, User user);

    /**
     * checks whether user has write permissions to file
     *
     * @param filename absolute path of file being checked
     * @param user     user being checked(currently logged user)
     */
    boolean canUserWriteToFile(String filename, User user);

}
