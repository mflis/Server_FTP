package pl.agh.edu.server.commands;

import pl.agh.edu.database.model.User;
import pl.agh.edu.database.utils.DatabaseOperations;
import pl.agh.edu.server.session.SessionManager;

import java.util.Optional;

public class PassCommand extends Command {
    private String password;
    private String userName;

    public PassCommand(String[] args, SessionManager sessionManager) {
        super(sessionManager);
        if (args.length != 1) {
            throw new IllegalArgumentException("501 Syntax error in parameters or arguments.");
        }
        if (!isAfterUserCommand()) {
            throw new IllegalArgumentException("503 Bad sequence of commands");
        }
        password = args[0];

    }

    @Override
    public String execute() {
        Optional<User> userOptional = DatabaseOperations.getInstance()
                .getUserIfExists(userName);
        if (!userOptional.isPresent()) {
            return "430 Invalid username or password";
        }

        if (DatabaseOperations.getInstance().validatePassword(userOptional.get(), password)) {
            getSessionManager().setLoggedUser(userOptional);
            return "230 User logged in";
        } else {
            return "430 Invalid username or password";
        }
    }

    private boolean isAfterUserCommand() {
        if (getSessionManager().getCommandHistory().isEmpty()) {
            return false;
        }

        Command nextToLastCommand = getSessionManager().getCommandHistory().get(0);
        if (nextToLastCommand.getClass() == UserCommand.class) {
            userName = ((UserCommand) nextToLastCommand).getUsername();
            return true;
        } else {
            return false;
        }
    }


}
