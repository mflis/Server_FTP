package pl.agh.edu.server.commands;

import pl.agh.edu.server.session.SessionManager;

public class CommandCreator {

    public static Command parseCommand(String command, String[] args, SessionManager sessionManager) {
        switch (command) {
            case "ABOR":
                return new AborCommand(args, sessionManager);
            case "APPE":
                return new AppeCommand(args, sessionManager);
            case "USER":
                return new UserCommand(args, sessionManager);
            case "CHMOD":
                return new ChmodCommand(args, sessionManager);
            case "CWD":
                return new CwdCommand(args, sessionManager);
            case "DELE":
                return new DeleCommand(args, sessionManager);
            case "LIST":
                return new ListCommand(args, sessionManager);
            case "MKD":
                return new MkdCommand(args, sessionManager);
            case "NOOP":
                return new NoopCommand(args, sessionManager);
            case "PASS":
                return new PassCommand(args, sessionManager);
            case "PASV":
                return new PasvCommand(args, sessionManager);
            case "PWD":
                return new PwdCommand(args, sessionManager);
            case "QUIT":
                return new QuitCommand(args, sessionManager);
            case "RETR":
                return new RetrCommand(args, sessionManager);
            case "RMD":
                return new RmdCommand(args, sessionManager);
            case "STOR":
                return new StorCommand(args, sessionManager);
            case "TYPE":
                return new TypeCommand(sessionManager);

            default:
                throw new NoSuchCommandException();
        }
    }
}
