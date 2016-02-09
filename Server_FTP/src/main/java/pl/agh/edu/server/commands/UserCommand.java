package pl.agh.edu.server.commands;

import lombok.Getter;
import pl.agh.edu.server.session.SessionManager;

public class UserCommand extends Command {
    @Getter
    private String username;

    public UserCommand(String[] args, SessionManager sessionManager) throws IllegalArgumentException {
        super(sessionManager);
        if (args.length != 1) {
            throw new IllegalArgumentException("501 Syntax error in parameters or arguments.");
        }
        if (!isFirstCommand()) {
            throw new IllegalArgumentException("503 Bad sequence of commands");
        }
        username = args[0];


    }

    @Override
    public String execute() {
        return "331 Password required";
    }

    private boolean isFirstCommand() {
        return getSessionManager().getCommandHistory().size() == 0;
    }
}
