package org.goodstorage.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.RequiredArgsConstructor;
import org.goodstorage.auth.AuthenticationManager;
import org.goodstorage.exceptions.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.goodstorage.server.ResponseSender.*;


@RequiredArgsConstructor
public class LoginHandler implements HttpHandler {

    private final Gson gson = new Gson();
    private final AuthenticationManager authenticationManager;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        if ("POST".equals(requestMethod)) {
            InputStream in = exchange.getRequestBody();
            String requestBody = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            in.close();

            LoginRequest loginRequest = gson.fromJson(requestBody, LoginRequest.class);
            try {
                String token = authenticationManager.authenticate(loginRequest.login(), loginRequest.password());

                String response = "{\n\"token\": \"" + token + "\"\n}";
                exchange.getResponseHeaders().set("Authorization", "Bearer " + token);
                sendResponse(exchange, 200, response);
            } catch (ResponseStatusException ex) {
                sendError(exchange, ex);
            }
        } else if (requestMethod.equalsIgnoreCase("OPTIONS")) {
            sendOptionResponse(exchange);
        } else if (requestMethod.equalsIgnoreCase("HEAD")) {
            sendHeadResponse(exchange);
        } else {
            sendError(exchange, 405, "Endpoint not found");
        }
    }


    private record LoginRequest(
            String login,
            String password
    ) {
    }
}
