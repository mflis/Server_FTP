package pl.agh.edu.server;

import com.avaje.ebean.EbeanServerFactory;
import com.avaje.ebean.config.ServerConfig;
import lombok.extern.slf4j.Slf4j;
import org.avaje.agentloader.AgentLoader;


@Slf4j
public class ServerInvoker {
    public static void main(String[] args) {
        // Load the agent into the running JVM process
        if (!AgentLoader.loadAgentFromClasspath("avaje-ebeanorm-agent", "debug=1;packages=pl.agh.edu.database.model.**")) {
            log.info("avaje-ebeanorm-agent not found in classpath - not dynamically loaded");
        }
        init();

        ThreadPooledServer server = new ThreadPooledServer(2112);
        new Thread(server).start();

        try {
            Thread.sleep(60 *60 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.exit(1);
        }
        System.out.println("Stopping Server");
        server.stop();
    }

    private static void init() {
        ServerConfig config = new ServerConfig();
        config.setName("h2");

        config.loadFromProperties();
        EbeanServerFactory.create(config);
    }

}
