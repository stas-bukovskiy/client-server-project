package edu.clientserver.pr5.server;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import edu.clientserver.pr5.authentication.AuthenticationManager;
import edu.clientserver.pr5.repository.GoodRepository;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;

@Slf4j
public class Server {

    private final HttpServer server;

    public Server(ExecutorService executorService, AuthenticationManager authenticationManager, GoodRepository goodRepository) throws IOException {
        server = HttpServer.create(new InetSocketAddress(ServerProperties.PORT), 0);
        server.setExecutor(executorService);
        createMappings(authenticationManager, goodRepository);
    }

    public void run() {
        server.start();
        log.info("Server is running on port " + ServerProperties.PORT);
    }

    public void stop() {
        log.info("Server is shutting down");
        server.stop(1000);
    }

    private void createMappings(AuthenticationManager authenticationManager, GoodRepository goodRepository) {
        HttpContext loginContext = server.createContext("/login");
        loginContext.setHandler(new LoginHandler(authenticationManager));

        HttpContext goodsContext = server.createContext("/api/good");
        goodsContext.setHandler(new GoodsHandler(goodRepository, authenticationManager));
    }
}
