package pl.agh.edu.server.commands;

import pl.agh.edu.server.session.SessionManager;

public class CwdCommand extends Command {
    private final String newDirectory;

    public CwdCommand(String[] args, SessionManager sessionManager) {
        super(sessionManager);

        newDirectory = String.join(" ", args);
    }

    @Override
    public String execute() {
        getSessionManager().setCurrentDirectory(newDirectory);
        return "250 CWD was successful";
    }
}
