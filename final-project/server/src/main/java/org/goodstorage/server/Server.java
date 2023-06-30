package org.goodstorage.server;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.goodstorage.auth.AuthenticationManager;
import org.goodstorage.connections.ConnectionFactory;
import org.goodstorage.initilizers.TableInitializer;
import org.goodstorage.repository.*;
import org.goodstorage.service.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class Server {

    private final HttpServer server;

    public Server() throws IOException {
        log.info("Starting server initialization...");
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        server = HttpServer.create(new InetSocketAddress(ServerProperties.PORT), 0);
        server.setExecutor(executorService);

        TableInitializer tableInitializer = new TableInitializer(ConnectionFactory.getConnection());
        tableInitializer.initialize();

        GoodRepository goodRepository = new GoodRepositoryImpl(ConnectionFactory.getConnection());
        GroupRepository groupRepository = new GroupRepositoryImpl(ConnectionFactory.getConnection());
        UserRepository userRepository = new UserRepositoryImpl(ConnectionFactory.getConnection());
        log.info("Repositories have initialized");

        GroupService groupService = new GroupServiceImpl(groupRepository, goodRepository);
        GoodService goodService = new GoodServiceImpl(goodRepository, groupService);
        UserService userService = new UserServiceImpl(userRepository);
        TokenService tokenService = new TokenServiceImpl();
        AuthenticationManager authenticationManager = new AuthenticationManager(userRepository, tokenService);
        userService.create(new UserService.CreateUserRequest("admin", "admin", DigestUtils.md5Hex("password").toUpperCase(), "ADMIN"));
        log.info("Services have initialized");

        GroupHandler groupHandler = new GroupHandler(groupService, authenticationManager);
        GoodsHandler goodsHandler = new GoodsHandler(goodService, authenticationManager);
        UserHandler userHandler = new UserHandler(userService, authenticationManager);
        LoginHandler loginHandler = new LoginHandler(authenticationManager);
        log.info("Handlers have initialized");

        HttpContext loginContext = server.createContext("/login");
        loginContext.setHandler(loginHandler);
        HttpContext groupsContext = server.createContext("/api/group");
        groupsContext.setHandler(groupHandler);
        HttpContext goodsContext = server.createContext("/api/good");
        goodsContext.setHandler(goodsHandler);
        HttpContext userContext = server.createContext("/api/user");
        userContext.setHandler(userHandler);
        log.info("Contexts and mappings have initialized");
    }

    public void run() {
        server.start();
        log.info("Server is running on port " + ServerProperties.PORT);
    }

    public void stop() {
        log.info("Server is shutting down");
        server.stop(1000);
    }

}
