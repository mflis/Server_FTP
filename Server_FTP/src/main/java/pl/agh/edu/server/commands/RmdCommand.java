package pl.agh.edu.server.commands;

import pl.agh.edu.server.session.SessionManager;

import java.io.File;

public class RmdCommand extends Command {
    private final File directoryToDelete;
    public RmdCommand(String[] args, SessionManager sessionManager) {
        super(sessionManager);

        directoryToDelete = getSessionManager().resolveRelativePath(args);
    }

    @Override
    public String execute() {
        if (directoryToDelete.delete()) {
            return "250 RMD was successful";
        } else {
            return "450 non empty directory";
        }

    }
}