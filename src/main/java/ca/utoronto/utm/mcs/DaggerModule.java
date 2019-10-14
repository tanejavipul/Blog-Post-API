package ca.utoronto.utm.mcs;

import java.io.IOException;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpServer;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@Module (injects = {App.class, DB.class}, library = true) //TODO: Add in any new classes here
class DaggerModule {
    Config config;

    DaggerModule(Config cfg) {
        config = cfg;
    }

    @Provides @Singleton
    MongoClient provideMongoClient() {
        System.out.println("Creating MongoClient...");
        return MongoClients.create();
    }

    @Provides
    HttpServer provideHttpServer() {
        try
        {
            System.out.println("Creating HttpServer...");
            return  HttpServer.create(new InetSocketAddress(config.ip, config.port), 0);

        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
