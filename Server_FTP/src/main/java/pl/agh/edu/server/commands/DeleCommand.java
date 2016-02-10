package pl.agh.edu.server.commands;

import lombok.extern.slf4j.Slf4j;
import pl.agh.edu.database.user.User;
import pl.agh.edu.database.utils.DatabaseOperations;
import pl.agh.edu.server.session.SessionManager;

import java.io.File;

@Slf4j
public class DeleCommand extends Command {
    private final File fileToDelete;

    public DeleCommand(String[] args, SessionManager sessionManager) {
        super(sessionManager);
        fileToDelete = getSessionManager().resolveRelativePath(args);
    }

    @Override
    public String execute() {
        if (!checkPermissions()) {
            return "450 permission denied";
        }
        if (fileToDelete.isDirectory() && fileToDelete.listFiles() != null) {
            return "450 non empty directory";
        }


        if (!fileToDelete.delete() || !DatabaseOperations.getInstance().deleteFileIfExists(fileToDelete.getPath())) {
            log.trace("450 unsuccessful delete");
            return "450 unsuccessful delete";
        }
        return "250 DELE was successful";
    }

    private boolean checkPermissions() {
        User user = getSessionManager().getLoggedUser().get();
        String path = fileToDelete.getPath();
        return DatabaseOperations.getInstance().canUserWriteToFile(path, user);
    }
}
