package pl.agh.edu.server.commands;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import pl.agh.edu.server.passiveTasks.PassiveTask;
import pl.agh.edu.server.passiveTasks.TypeOfTask;
import pl.agh.edu.server.session.SessionManager;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

@Slf4j
public class ListCommand extends Command {

    public ListCommand(String[] args, SessionManager sessionManager) {
        super(sessionManager);
        if (args.length != 0) {
            throw new IllegalArgumentException("501 Syntax error in parameters or arguments.");
        }

    }

    @Override
    public String execute() {
        InputStream inputStream = IOUtils.toInputStream(getListOfFiles());
        PassiveTask writeTask = new PassiveTask(TypeOfTask.WRITE);
        writeTask.setInputStream(Optional.of(inputStream));
        try {
            getSessionManager().getBlockingQueue().put(writeTask);
        } catch (InterruptedException e) {
            log.error("error while putting LIST task to Queue", e);
        }
        return "150 Opening ASCII mode data connection for " + getRelativePath();

    }

    private String getRelativePath() {
        Path pathAbsolute = getSessionManager().getCurrentDirectory();
        Path pathBase = Paths.get(".");
        return pathBase.relativize(pathAbsolute).toString();
    }

    private String getListOfFiles() {
        File directory = getSessionManager().getCurrentDirectory().toFile();
        StringBuilder stringBuilder = new StringBuilder();
        appendCurrentDirAndParentDir(stringBuilder);
        File[] listOfFiles = directory.listFiles();
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                appendDrwxAndOwners(stringBuilder, file);
                appendFileSize(stringBuilder, file);
                appendTimeStamp(stringBuilder, file);
                stringBuilder.append("\t");
                stringBuilder.append(file.getName());
                stringBuilder.append("\n");
            }
        }

        return stringBuilder.toString();
    }


    private void appendTimeStamp(StringBuilder stringBuilder, File file) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM\td\tyyyy");
        Date date = new Date(file.lastModified());
        String formattedDate = sdf.format(date);
        stringBuilder.append("\t");
        stringBuilder.append(formattedDate);
    }


    private void appendFileSize(StringBuilder stringBuilder, File file) {
        //because output form file.length on directory is undefined
        if (file.isDirectory()) {
            stringBuilder.append("\t4096");
        } else {
            stringBuilder.append("\t");
            stringBuilder.append(file.length());
        }
    }

    private void appendDrwxAndOwners(StringBuilder stringBuilder, File file) {
        if (file.isDirectory()) {
            stringBuilder.append("d");
        } else {
            stringBuilder.append("-");
        }
        stringBuilder.append("rw-rw-rw-\t1 ftp\tftp");
    }

    private void appendCurrentDirAndParentDir(StringBuilder stringBuilder) {
        stringBuilder.append("drwxrwxr-x\t4 ftp\t ftp\t 4096 Jan  9 18:35 .\n");
        stringBuilder.append("drwxrwxr-x\t4 ftp\t ftp\t 4096 Jan  9 18:35 ..\n");
    }

}