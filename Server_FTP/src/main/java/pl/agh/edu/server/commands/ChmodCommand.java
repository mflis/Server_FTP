package pl.agh.edu.server.commands;

import pl.agh.edu.database.user.User;
import pl.agh.edu.database.utils.DatabaseOperations;
import pl.agh.edu.server.session.SessionManager;

import java.io.File;
import java.util.Arrays;

public class ChmodCommand extends Command {
    private final File fileToChangePermissions;
    private final String newPermissions;

    public ChmodCommand(String[] args, SessionManager sessionManager) {
        super(sessionManager);
        String[] filename = Arrays.copyOfRange(args, 0, args.length - 1);
        fileToChangePermissions = getSessionManager().resolveRelativePath(filename);
        newPermissions = args[args.length - 1];

        if (newPermissions.length() != 2 || !newPermissions.matches("[0-3]2")) {
            throw new IllegalArgumentException("501 Syntax error in parameters or arguments.");
        }
    }

    @Override
    public String execute() {
        if (!checkPermissions()) {
            return "450 permission denied";
        }
        DatabaseOperations.getInstance().changePermissions(
                fileToChangePermissions.getPath(), decodePermissions(newPermissions));

        return "permissions changed";
    }

    private boolean checkPermissions() {
        User user = getSessionManager().getLoggedUser().get();
        String path = fileToChangePermissions.getPath();
        return DatabaseOperations.getInstance().canUserWriteToFile(path, user);
    }

    private Permissions decodePermissions(String newPermissions) {
        int ownerEncoded = Integer.parseInt(newPermissions.substring(0, 1));
        int groupEncoded = Integer.parseInt(newPermissions.substring(1, 2));
        Permissions result = new Permissions();

        if (ownerEncoded >= 2) { //contains 2
            result.ownerWrite = true;
        }
        if (ownerEncoded % 2 != 0) {//contains 1
            result.ownerRead = true;
        }
        if (groupEncoded >= 2) { //contains 2
            result.groupWrite = true;
        }
        if (groupEncoded % 2 != 0) {//contains 1
            result.groupRead = true;
        }

        return result;
    }

    public static class Permissions {
        public boolean ownerRead = false;
        public boolean ownerWrite = false;
        public boolean groupRead = false;
        public boolean groupWrite = false;
    }
}