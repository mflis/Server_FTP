package pl.agh.edu.server.commands;

import lombok.extern.slf4j.Slf4j;
import pl.agh.edu.server.PassiveConnection;
import pl.agh.edu.server.session.SessionManager;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.Optional;

@Slf4j
public class PasvCommand extends Command {
    private int passivePort;

    public PasvCommand(String[] args, SessionManager sessionManager) {
        super(sessionManager);
        if (args.length != 0) {
            throw new IllegalArgumentException("501 Syntax error in parameters or arguments.");
        }

        //for debug time commented
//        if (!getSessionManager().getLoggedUser().isPresent()) {
//            throw new IllegalArgumentException("503 Bad sequence of commands");
//        }
    }

    @Override
    public String execute() {
        String address = "";
        try {
            PassiveConnection passiveConnection = new PassiveConnection(
                    getSessionManager(), createServerSocket(), passivePort);
            getSessionManager().setPassiveConnection(Optional.of(passiveConnection));
            new Thread(passiveConnection).start();
            address = InetAddress.getLocalHost().getHostAddress().replace('.', ',');
        } catch (UnknownHostException e) {
            log.error("Error getting local IP", e);
        } catch (IOException ex) {
            log.error("PASV command: no free port found");
        }
        return "227 Entering Passive Mode (" + address + ",4,"
                + passivePort % 256 + ")";

    }


    private ServerSocket createServerSocket() throws IOException {
        for (passivePort = 1025; passivePort < 65000; passivePort++) {
            try {
                return new ServerSocket(passivePort);
            } catch (IOException ex) {
                // try next port
            }
        }
        // if the program gets here, no port in the range was found
        throw new IOException();
    }
}