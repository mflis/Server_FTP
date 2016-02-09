package pl.agh.edu.AdminPanel;

import com.avaje.ebean.EbeanServerFactory;
import com.avaje.ebean.config.ServerConfig;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.avaje.agentloader.AgentLoader;

import java.io.IOException;

@Slf4j
public class Main extends Application {

    public static void main(String[] args) {
        initialzeDatabase();
        launch(args);
    }

    private static void initialzeDatabase() {
        // Load the agent into the running JVM process
        if (!AgentLoader.loadAgentFromClasspath("avaje-ebeanorm-agent", "debug=1;packages=pl.agh.edu.AdminPanel.model.**")) {
            log.info("avaje-ebeanorm-agent not found in classpath - not dynamically loaded");
        }
        ServerConfig config = new ServerConfig();
        config.setName("h2");

        config.loadFromProperties();
        EbeanServerFactory.create(config);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        AnchorPane page = FXMLLoader.load(Main.class.getResource("/main.fxml"));
        Scene scene = new Scene(page);
        primaryStage.setScene(scene);

        primaryStage.setTitle("Admin Panel");
        primaryStage.show();
    }
}
