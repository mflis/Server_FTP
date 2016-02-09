package pl.agh.edu.server.commands;


import pl.agh.edu.server.session.SessionManager;

import java.io.File;

/**
 * FileZilla uses relative paths and filenames can contain spaces
 */
public class MkdCommand extends Command {
    private final File newDirectory;

    public MkdCommand(String[] args, SessionManager sessionManager) {
        super(sessionManager);

        newDirectory = getSessionManager().resolveRelativePath(args);
    }

    @Override
    public String execute() {
        if (newDirectory.mkdirs()) {
            return "257 pathname was created";
        } else {
            return "450 directory already exists";
        }
    }
}