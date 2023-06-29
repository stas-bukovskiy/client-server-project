package org.goodstorage.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.RequiredArgsConstructor;
import org.goodstorage.auth.AuthenticationManager;
import org.goodstorage.domain.User;
import org.goodstorage.exceptions.ResponseStatusException;
import org.goodstorage.exceptions.ValidationException;
import org.goodstorage.service.UserService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.goodstorage.server.ResponseSender.*;
import static org.goodstorage.util.UuidUtil.UUID_REGEX;

@RequiredArgsConstructor
public class UserHandler implements HttpHandler {


    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final Gson gson = new Gson();


    private static String getId(String uri) {
        return uri.substring(uri.lastIndexOf('/') + 1);
    }


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        String requestURI = exchange.getRequestURI().toString();

        try {
            if (requestMethod.equalsIgnoreCase("OPTIONS")) {
                sendOptionResponse(exchange);
                return;
            } else if (requestMethod.equalsIgnoreCase("HEAD")) {
                sendHeadResponse(exchange);
                return;
            }

            User currentUser = authenticationManager.validateAuthenticationAndGetUser(exchange);

            if (requestMethod.equals("GET") && requestURI.matches("/api/user")) {
                // GET /api/user
                handleGetAllUsers(exchange, currentUser);
            } else if (requestMethod.equals("GET") && requestURI.matches("^/api/user/" + UUID_REGEX + "$")) {
                // GET /api/user/{id}
                handleGetUserById(exchange, getId(requestURI), currentUser);
            } else if (requestMethod.equals("POST") && requestURI.equals("/api/user")) {
                // POST /api/user
                handlePostNewUser(exchange, currentUser);
            } else if (requestMethod.equals("PUT") && requestURI.matches("^/api/user/" + UUID_REGEX + "$")) {
                // PUT /api/user/{id}
                handlePutExistingUser(exchange, getId(requestURI), currentUser);
            } else if (requestMethod.equals("DELETE") && requestURI.matches("^/api/user/" + UUID_REGEX + "$")) {
                // DELETE /api/user/{id}
                handleDeleteUser(exchange, getId(requestURI), currentUser);
            } else {
                sendError(exchange, 405, "Endpoint not found");
            }
        } catch (ResponseStatusException e) {
            sendError(exchange, e);
        }
    }

    private void handleGetAllUsers(HttpExchange exchange, User currentUser) throws IOException {
        if (!currentUser.getRole().equals("ADMIN"))
            throw new ResponseStatusException(403, "Only admin can get all users");

        List<User> users = userService.getUsers();
        String responseJson = gson.toJson(users);
        sendResponse(exchange, 200, responseJson);
    }

    private void handleGetUserById(HttpExchange exchange, String id, User currentUser) throws IOException {
        if (!currentUser.getRole().equals("ADMIN") || currentUser.getId().equals(id))
            throw new ResponseStatusException(403, "Only admin can get information about user");

        User user = userService.getById(id);
        String responseJson = gson.toJson(user);
        sendResponse(exchange, 200, responseJson);
    }

    private void handlePostNewUser(HttpExchange exchange, User currentUser) throws IOException {
        if (!currentUser.getRole().equals("ADMIN"))
            throw new ResponseStatusException(403, "Only admin can create new users");

        UserService.CreateUserRequest request = getValidCreateUserRequestFromBody(exchange);
        User createdUser = userService.create(request);
        sendResponse(exchange, 201, gson.toJson(createdUser));
    }

    private UserService.CreateUserRequest getValidCreateUserRequestFromBody(HttpExchange exchange) throws IOException {
        InputStream in = exchange.getRequestBody();
        String requestBody = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        in.close();

        UserService.CreateUserRequest request = gson.fromJson(requestBody, UserService.CreateUserRequest.class);
        validateUserRequest(request);
        return request;
    }

    private void handlePutExistingUser(HttpExchange exchange, String id, User currentUser) throws IOException {
        if (!currentUser.getRole().equals("ADMIN"))
            throw new ResponseStatusException(403, "Only admin can update users");

        UserService.UpdateUserRequest request = getValidUpdateUserRequestFromBody(exchange);
        User updatedUser = userService.update(id, request);
        sendResponse(exchange, 200, gson.toJson(updatedUser));
    }

    private UserService.UpdateUserRequest getValidUpdateUserRequestFromBody(HttpExchange exchange) throws IOException {
        InputStream in = exchange.getRequestBody();
        String requestBody = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        in.close();

        UserService.UpdateUserRequest request = gson.fromJson(requestBody, UserService.UpdateUserRequest.class);
        validateUserRequest(request);
        return request;
    }

    private void handleDeleteUser(HttpExchange exchange, String id, User currentUser) throws IOException {
        if (!currentUser.getRole().equals("ADMIN"))
            throw new ResponseStatusException(403, "Only admin can delete users");

        userService.delete(id);
        sendResponse(exchange, 204, "");
    }


    private void validateUserRequest(UserService.CreateUserRequest request) {
        if (request.getFullName() == null || request.getFullName().trim().length() == 0)
            throw new ValidationException("'fullName' must not be omitted or empty");
        if (request.getUsername() == null || request.getUsername().trim().length() == 0)
            throw new ValidationException("'username' must not be omitted or empty");
        if (request.getPassword() == null || request.getPassword().trim().length() == 0)
            throw new ValidationException("'password' must not be omitted or empty");
        if (request.getRole() == null || request.getRole().trim().length() == 0)
            throw new ValidationException("'role' must not be omitted or empty");
        if (!(request.getRole().equalsIgnoreCase("ADMIN") || request.getRole().equalsIgnoreCase("USER")))
            throw new ValidationException("'role' can be 'ADMIN' or 'USER'");
    }

    private void validateUserRequest(UserService.UpdateUserRequest request) {
        if (request.getFullName() == null || request.getFullName().trim().length() == 0)
            throw new ValidationException("'fullName' must not be omitted or empty");
        if (request.getUsername() == null || request.getUsername().trim().length() == 0)
            throw new ValidationException("'username' must not be omitted or empty");
        if (request.getPassword() == null || request.getPassword().trim().length() == 0)
            throw new ValidationException("'password' must not be omitted or empty");
    }


}
