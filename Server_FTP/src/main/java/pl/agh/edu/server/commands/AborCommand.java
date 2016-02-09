package pl.agh.edu.server.commands;

import pl.agh.edu.server.PassiveConnection;
import pl.agh.edu.server.session.SessionManager;

import java.util.Optional;

public class AborCommand extends Command {
    public AborCommand(String[] args, SessionManager sessionManager) {
        super(sessionManager);
        if (args.length != 0) {
            throw new IllegalArgumentException("501 Syntax error in parameters or arguments.");
        }
    }

    @Override
    public String execute() {
        Optional<PassiveConnection> connectionOptional = getSessionManager().getPassiveConnection();
        if (connectionOptional.isPresent()) {
            connectionOptional.get().abort();
        }
        return " execute successful";
    }
}