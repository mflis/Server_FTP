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
public class StorCommand extends Command {
    private final File fileToRead;

    public StorCommand(String[] args, SessionManager sessionManager) {
        super(sessionManager);
        fileToRead = getSessionManager().resolveRelativePath(args);
    }

    @Override
    public String execute() {

        try {
            OutputStream outputStream = new FileOutputStream(fileToRead);
            PassiveTask readTask = new PassiveTask(TypeOfTask.READ);
            readTask.setOutputStream(Optional.of(outputStream));
            getSessionManager().getBlockingQueue().put(readTask);
        } catch (InterruptedException e) {
            log.error("error while putting STOR task to Queue", e);
        } catch (FileNotFoundException ex) {
            log.error("450 error storing file", ex);
            return "450 error storing file";
        }

        if (saveToDatabase()) {
            return "150 FILE " + fileToRead.getName();
        } else {
            return "450 file already exists";
        }


    }

    private boolean saveToDatabase() {
        User user = getSessionManager().getLoggedUser().get();
        String path = fileToRead.getPath();
        return DatabaseOperations.getInstance().insertNewFileIfNotExists(path, user);
    }
}