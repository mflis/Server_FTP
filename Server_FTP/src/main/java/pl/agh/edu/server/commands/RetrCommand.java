package pl.agh.edu.server.commands;

import lombok.extern.slf4j.Slf4j;
import pl.agh.edu.database.file.FileDaoImpl;
import pl.agh.edu.database.user.User;
import pl.agh.edu.server.passiveTasks.PassiveTask;
import pl.agh.edu.server.passiveTasks.TypeOfTask;
import pl.agh.edu.server.session.SessionManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Optional;

@Slf4j
public class RetrCommand extends Command {
    private final File fileToSend;

    public RetrCommand(String[] args, SessionManager sessionManager) {
        super(sessionManager);
        fileToSend = getSessionManager().resolveRelativePath(args);


    }


    @Override
    public String execute() {
        if (!checkPermissions()) {
            return "450 permission denied";
        }
        try {
            InputStream inputStream = new FileInputStream(fileToSend);
            PassiveTask writeTask = new PassiveTask(TypeOfTask.WRITE);
            writeTask.setInputStream(Optional.of(inputStream));
            getSessionManager().getBlockingQueue().put(writeTask);
        } catch (InterruptedException e) {
            log.error("error while putting RETR task to Queue", e);
        } catch (FileNotFoundException ex) {
            log.error("file not found", ex);
            return "450 file not found";
        }
        checkPermissions();
        return "150 Opening binary mode data connection for " + fileToSend.getName();

    }

    private boolean checkPermissions() {
        User user = getSessionManager().getLoggedUser().get();
        String path = fileToSend.getPath();
        return FileDaoImpl.INSTANCE.canUserReadFromFile(path, user);
    }
}