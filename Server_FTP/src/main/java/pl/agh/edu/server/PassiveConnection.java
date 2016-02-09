package pl.agh.edu.server;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.Charsets;
import pl.agh.edu.server.passiveTasks.PassiveTask;
import pl.agh.edu.server.session.SessionManager;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
@RequiredArgsConstructor
public class PassiveConnection implements Runnable {
    private final SessionManager sessionManager;
    private final ServerSocket serverSocket;
    private final int serverPort;
    private Socket clientSocket;
    private volatile boolean isAborted = false;

    @Override
    public void run() {
//        synchronized (this) {
//            this.runningThread = Thread.currentThread();
//        }

        try {
            clientSocket = serverSocket.accept();
            log.trace("connection accepted on passive connection port : " + serverPort);

            PassiveTask passiveTask = sessionManager.getBlockingQueue().take();
            executeTask(passiveTask);
        } catch (IOException ex) {
            log.error("error accepting connection in passive mode");
        } catch (InterruptedException ex) {
            log.error("error taking task from blocking Queue");
        } finally {
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                log.error("error closing sockets in passive mode", e);
            }
        }
    }


    private void executeTask(PassiveTask passiveTask) {

        try {
            //because sometimes transfer is complete before message "150 Opening ... comes to client
            Thread.sleep(500);
            switch (passiveTask.getTypeOfTask()) {
                case WRITE:
                    log.trace("started sending data to client");
                    //IOUtils.copy(passiveTask.getInputStream().get(), clientSocket.getOutputStream());
                    copy(passiveTask.getInputStream().get(), clientSocket.getOutputStream());
                    break;
                case READ:
                    log.trace("started receiving data from client");
//                    IOUtils.copy(clientSocket.getInputStream(), passiveTask.getOutputStream().get());
                    copy(clientSocket.getInputStream(), passiveTask.getOutputStream().get());
                    break;
                case QUIT:
                    // do nothing
                    break;
            }

            closeStreamsFromPassiveTask(passiveTask);
            signalEndOfOperation();
        } catch (IOException ex) {
            log.error("error sending/receiving data in passive mode", ex);
        } catch (InterruptedException e) {
            log.error("error while Thread.sleep()   in passive mode", e);
        }
    }

    private void closeStreamsFromPassiveTask(PassiveTask passiveTask) throws IOException {
        if (passiveTask.getInputStream().isPresent()) {
            passiveTask.getInputStream().get().close();
        }
        if (passiveTask.getOutputStream().isPresent()) {
            passiveTask.getOutputStream().get().close();
        }
    }

    private void signalEndOfOperation() {
        try {
            OutputStream outputStream = sessionManager.getCurrentThread().clientSocket.getOutputStream();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, Charsets.UTF_8);
            PrintWriter out = new PrintWriter(outputStreamWriter, true);
            out.println("226 Transfer complete");
            log.trace("226 Transfer complete");
        } catch (IOException e) {
            log.error("error writing to command port form passive Connection");
        }
    }

    private void copy(InputStream in, OutputStream out)
            throws IOException {
        // Read bytes and write to destination until eof

        byte[] buf = new byte[1024];
        int len;
        while (!isAborted() && (len = in.read(buf)) >= 0) {
            out.write(buf, 0, len);
        }

    }


    private synchronized boolean isAborted() {
        return this.isAborted;
    }

    public synchronized void abort() {
        this.isAborted = true;
    }


}
