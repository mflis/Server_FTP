package pl.agh.edu.server.commands;

import lombok.extern.slf4j.Slf4j;
import pl.agh.edu.database.model.User;
import pl.agh.edu.database.utils.DatabaseOperations;
import pl.agh.edu.server.passiveTasks.PassiveTask;
import pl.agh.edu.server.passiveTasks.TypeOfTask;
import pl.agh.edu.server.session.SessionManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Optional;

@Slf4j
public class AppeCommand extends Command {
    private final File fileToRead;

    public AppeCommand(String[] args, SessionManager sessionManager) {
        super(sessionManager);
        fileToRead = getSessionManager().resolveRelativePath(args);

    }

    @Override
    public String execute() {
        if (!checkPermissions()) {
            return "450 permission denied";
        }

        try {
            OutputStream outputStream = new FileOutputStream(fileToRead, true);
            PassiveTask readTask = new PassiveTask(TypeOfTask.READ);
            readTask.setOutputStream(Optional.of(outputStream));
            getSessionManager().getBlockingQueue().put(readTask);
        } catch (InterruptedException e) {
            log.error("error while putting APPE task to Queue", e);
        } catch (FileNotFoundException ex) {
            log.error("file not found", ex);
            return "450 file not found";
        }
        return "150 Opening binary mode data connection for " + fileToRead.getName();

    }

    private boolean checkPermissions() {
        User user = getSessionManager().getLoggedUser().get();
        String path = fileToRead.getPath();
        return DatabaseOperations.getInstance().canUserWriteToFile(path, user);
    }
}