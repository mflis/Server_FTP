package pl.agh.edu.AdminPanel.utils;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.EbeanServer;
import lombok.extern.slf4j.Slf4j;
import pl.agh.edu.AdminPanel.model.Group;
import pl.agh.edu.AdminPanel.model.User;

import java.util.Optional;

@Slf4j
public class DatabaseOperations {
    private static final DatabaseOperations ourInstance = new DatabaseOperations();
    private final EbeanServer ebeanServer = Ebean.getDefaultServer();

    private DatabaseOperations() {
    }

    public static DatabaseOperations getInstance() {
        return ourInstance;
    }

    public boolean insertNewUserIfNotExists(String name, String password) {
        if (getUserIfExists(name).isPresent()) {
            log.info("Can't insert user, because already exists : " + name);
            return false;
        }
        User newUser = new User();
        newUser.setName(name)
                .setPassword(password);
        Group newGroup = new Group();
        newGroup.setName(name)
                .addUser(newUser);

        //saving just group is enough. There's no need to save newUser also
        ebeanServer.save(newGroup);
        log.info("Succesfully created user : " + newUser.getName());
        return true;
    }


    public boolean insertNewGroupIfNotExists(String groupName) {
        if (getGroupIfExists(groupName).isPresent()) {
            log.info("Can't insert group, because already exists : " + groupName);
            return false;
        }

        Group newGroup = new Group();
        newGroup.setName(groupName);

        ebeanServer.save(newGroup);
        return true;

    }


    public boolean addUserToGroup(String userName, String groupName) {
        Optional<Group> groupOptional = getGroupIfExists(groupName);
        Optional<User> userOptional = getUserIfExists(userName);

        if (!userOptional.isPresent() || !groupOptional.isPresent()) {
            return false;
        }

        Group group = groupOptional.get();
        group.addUser(userOptional.get());
        ebeanServer.save(group);
        return true;
    }


    private Optional<User> getUserIfExists(String username) {

        User user = ebeanServer.find(User.class)
                .where()
                .eq("name", username)
                .findUnique();

        if (user == null) {
            return Optional.empty();
        } else {
            return Optional.of(user);
        }

    }

    private Optional<Group> getGroupIfExists(String groupName) {

        Group user = ebeanServer.find(Group.class)
                .where()
                .eq("name", groupName)
                .findUnique();

        if (user == null) {
            return Optional.empty();
        } else {
            return Optional.of(user);
        }

    }


}
