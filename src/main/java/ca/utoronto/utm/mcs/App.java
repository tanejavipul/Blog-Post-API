package ca.utoronto.utm.mcs;

import java.net.URI;
import java.net.URISyntaxException;
import com.sun.net.httpserver.HttpServer;

import javax.inject.Inject;
import dagger.ObjectGraph;

public class App implements Runnable
{
    @Inject HttpServer server;
    @Inject Config config;
    @Inject Post post;
    //@Inject MongoClient mongoClient;

    public void run()
    {
        server.setExecutor(null);
        server.start();
        System.out.printf("Server started on port %d...\n", config.port);
        server.createContext("/api/v1/post", post);
    }

    public static void main(String[] args) throws URISyntaxException
    {
        ObjectGraph objectGraph = ObjectGraph.create(new DaggerModule(new Config()));
        App app = objectGraph.get(App.class);
        app.run();
    }
}
