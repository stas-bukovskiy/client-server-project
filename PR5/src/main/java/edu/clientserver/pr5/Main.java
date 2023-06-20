package edu.clientserver.pr5;

import edu.clientserver.pr5.authentication.AuthenticationManager;
import edu.clientserver.pr5.connection.ConnectionFactory;
import edu.clientserver.pr5.domain.User;
import edu.clientserver.pr5.repository.DatabaseInitializer;
import edu.clientserver.pr5.repository.GoodRepository;
import edu.clientserver.pr5.repository.UserRepository;
import edu.clientserver.pr5.server.Server;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) throws IOException, SQLException {
        DatabaseInitializer.createTables();
        UserRepository userRepository = new UserRepository(ConnectionFactory.getConnection());
        userRepository.save(User.builder()
                .username("user")
                .password(DigestUtils.md5Hex("password").toUpperCase())
                .build());

        AuthenticationManager authenticationManager = new AuthenticationManager(userRepository);
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        Server server = new Server(executorService, authenticationManager, new GoodRepository());
        server.run();
    }
}