package pl.agh.edu.server.commands;

import pl.agh.edu.server.session.SessionManager;

public class NoopCommand extends Command {
    public NoopCommand(String[] args, SessionManager sessionManager) {
        super(sessionManager);
        if (args.length != 0) {
            throw new IllegalArgumentException("501 Syntax error in parameters or arguments.");
        }

        if (!getSessionManager().getLoggedUser().isPresent()) {
            throw new IllegalArgumentException("503 Bad sequence of commands");
        }
    }

    @Override
    public String execute() {
        return "200 Command successful";
    }
}