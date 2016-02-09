package pl.agh.edu.server;


import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.Charsets;
import pl.agh.edu.server.commands.Command;
import pl.agh.edu.server.commands.CommandCreator;
import pl.agh.edu.server.session.SessionManager;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Arrays;

@Slf4j
public class SingleServerThread implements Runnable {

    Socket clientSocket = null;
    @Setter
    private volatile boolean isRunning = true;
    private SessionManager sessionManager;


    public SingleServerThread(Socket clientSocket) throws SocketException {
        this.clientSocket = clientSocket;
        clientSocket.setSoTimeout(60 * 1000);
        sessionManager = new SessionManager();
    }

    public void run() {
        try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(clientSocket.getOutputStream(), Charsets.UTF_8);
             PrintWriter out = new PrintWriter(outputStreamWriter, true);
             InputStreamReader inputStreamReader = new InputStreamReader(clientSocket.getInputStream(), Charsets.UTF_8);
             BufferedReader in = new BufferedReader(inputStreamReader)) {

            sessionManager.setCurrentThread(this);
            String inputLine;

            // Initial message
            out.println("220 Hello");

            while ((inputLine = in.readLine()) != null) {
                log.trace("inputLine: " + inputLine);
                out.println(processInput(inputLine));
                if (!isRunning) {
                    return;
                }
            }

        } catch (SocketTimeoutException ex) {
            log.debug("Timeout");
        } catch (IOException e) {
            //report exception somewhere.
            log.debug("IOException : ", e);
        } finally {
            try {
                clientSocket.close();
                if (sessionManager.getPassiveConnection().isPresent()) {
                    sessionManager.getPassiveConnection().get().abort();
                }
            } catch (IOException e) {
                log.error("error while closing clientSocket", e);
            }
        }
    }

    private String processInput(String input) {
        //trim leading and trailing whitespaces, use one or more spaces as separator
        String[] words = input.trim().split("\\s+");
        String commandType = words[0];

        // get array without first element
        String[] args = Arrays.copyOfRange(words, 1, words.length);
        Command command;
        try {
            command = CommandCreator.parseCommand(commandType, args, sessionManager);
        } catch (IllegalArgumentException ex) {
            log.error(ex.getMessage() + " input : " + input);
            return ex.getMessage();
        }


        sessionManager.getCommandHistory().add(command);
        String responseMessage = command.execute();
        log.trace(responseMessage);
        return responseMessage;
    }


}