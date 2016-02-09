package pl.agh.edu.server.commands;

import lombok.extern.slf4j.Slf4j;
import pl.agh.edu.server.passiveTasks.PassiveTask;
import pl.agh.edu.server.passiveTasks.TypeOfTask;
import pl.agh.edu.server.session.SessionManager;

@Slf4j
public class QuitCommand extends Command {
    public QuitCommand(String[] args, SessionManager sessionManager) {
        super(sessionManager);
        if (args.length != 0) {
            throw new IllegalArgumentException("501 Syntax error in parameters or arguments.");
        }

    }

    @Override
    public String execute() {
        closePassiveConnectionIfOpened();

        getSessionManager().getCurrentThread().setRunning(false);
        return "221 Bye";
    }

    private void closePassiveConnectionIfOpened() {
        if (getSessionManager().getPassiveConnection().isPresent()) {
            PassiveTask quitTask = new PassiveTask(TypeOfTask.QUIT);
            try {
                getSessionManager().getBlockingQueue().put(quitTask);
            } catch (InterruptedException e) {
                log.error("error putting QUI task to blocking queue. Queu");
            }
        }
    }
}