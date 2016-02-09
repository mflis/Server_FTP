package pl.agh.edu.server.commands;

import pl.agh.edu.server.session.SessionManager;

public class TypeCommand extends Command {
    public TypeCommand(SessionManager sessionManager) {
        super(sessionManager);

    }

    @Override
    public String execute() {
        return "200 ok";
    }
}