package pl.agh.edu.server.session;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import pl.agh.edu.database.user.User;
import pl.agh.edu.server.PassiveConnection;
import pl.agh.edu.server.SingleServerThread;
import pl.agh.edu.server.commands.Command;
import pl.agh.edu.server.passiveTasks.PassiveTask;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Getter
@Setter

public class SessionManager {
    private SingleServerThread currentThread;
    private Optional<User> loggedUser = Optional.empty();

    @Setter(AccessLevel.NONE)
    private List<Command> commandHistory = new ArrayList<>();

    private Path currentDirectory = Paths.get("FTP");
    private Optional<PassiveConnection> passiveConnection = Optional.empty();
    private BlockingQueue<PassiveTask> blockingQueue = new ArrayBlockingQueue<>(1);

    /**
     * @return representation of current working directory to the outside world ( starting with "/")
     */
    public String getPathRepresentation() {
        if (currentDirectory.getNameCount() < 2) {
            return "/";
        } else {
            String subPath = currentDirectory.subpath(1, currentDirectory.getNameCount()).toString();
            return "/" + subPath;
        }
    }

    /**
     * set current working directory using absolute path
     *
     * @param directory path to be set. Must start with "/"
     */
    public void setCurrentDirectory(String directory) {
        currentDirectory = Paths.get("FTP" + directory);

    }

    /**
     * @param args args provided by MKD,RMD,CWD itp commands. Represents relative
     *             directory. File/directory names may contain spaces
     * @return abstract file object pointing to this directory
     */
    public File resolveRelativePath(String[] args) {
        String relativeDirectory = String.join(" ", args);
        return Paths.get(currentDirectory.toString(), relativeDirectory).toFile();

    }

}
