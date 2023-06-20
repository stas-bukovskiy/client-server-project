package edu.clientserver.pr5.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.clientserver.pr5.authentication.AuthenticationManager;
import edu.clientserver.pr5.exception.AuthenticationException;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;


@RequiredArgsConstructor
public class LoginHandler implements HttpHandler {


    private final AuthenticationManager authenticationManager;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            InputStream in = exchange.getRequestBody();
            OutputStream out = exchange.getResponseBody();
            String requestBody = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            in.close();

            Gson gson = new Gson();
            LoginRequest loginRequest = gson.fromJson(requestBody, LoginRequest.class);

            try {

                String token = authenticationManager.authenticate(loginRequest.login(), loginRequest.password());

                String response = "{\n\"token\": \"" + token + "\"\n}";

                exchange.getResponseHeaders().set("Authorization", "Bearer " + token);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
                out.write(response.getBytes());
                out.flush();
                out.close();
            } catch (AuthenticationException ex) {
                byte[] messageBytes = ex.getMessage().getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(401, messageBytes.length);
                out.write(messageBytes);
                out.flush();
            }
        } else {
            exchange.sendResponseHeaders(405, 0);
        }
    }


    private record LoginRequest(
            String login,
            String password
    ) {
    }
}
