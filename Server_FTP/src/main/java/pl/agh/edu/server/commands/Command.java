package pl.agh.edu.server.commands;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.agh.edu.server.session.SessionManager;

@RequiredArgsConstructor
public abstract class Command {
    @Getter
    private final SessionManager sessionManager;


    public abstract String execute();
}
