package pl.agh.edu.database.file;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.EbeanServer;
import lombok.extern.slf4j.Slf4j;
import pl.agh.edu.database.user.User;
import pl.agh.edu.database.user.UserDaoImpl;
import pl.agh.edu.server.commands.ChmodCommand;

import java.util.Optional;

@Slf4j
public enum FileDaoImpl implements FileDAO {
    INSTANCE;

    private static final EbeanServer ebeanServer = Ebean.getDefaultServer();

    @Override
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

    @Override
    public boolean deleteFileIfExists(String pathToFile) {
        Optional<File> optionalFile = getFileIfExists(pathToFile);
        if (!optionalFile.isPresent()) {
            log.info("Can't delete file, because not exists : " + pathToFile);
            return false;
        }

        ebeanServer.delete(optionalFile.get());
        return true;
    }

    @Override
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

    @Override
    public Optional<File> getFileIfExists(String pathToFile) {
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


    @Override
    public boolean canUserReadFromFile(String filename, User user) {
        Optional<File> fileOptional = getFileIfExists(filename);
        if (!fileOptional.isPresent()) {
            log.info("Such file doesn't exist: [{}] in canUserReadFromFile()", filename);
            return false;
        }
        File file = fileOptional.get();

        return user.getName().equals("admin")
                || (UserDaoImpl.INSTANCE.isUserInGroup(file.getGroup(), user) && file.isGroupRead())
                || (file.isUserRead() && file.getOwner().getUserId() == user.getUserId());

    }

    @Override
    public boolean canUserWriteToFile(String filename, User user) {
        Optional<File> fileOptional = getFileIfExists(filename);
        if (!fileOptional.isPresent()) {
            log.info("Such file doesn't exist: [{}] in canUserWriteToFile()", filename);
            return false;
        }

        File file = fileOptional.get();

        return user.getName().equals("admin")
                || (file.isGroupWrite() && UserDaoImpl.INSTANCE.isUserInGroup(file.getGroup(), user))
                || (file.isUserWrite() && file.getOwner().getUserId() == user.getUserId());

    }

}
