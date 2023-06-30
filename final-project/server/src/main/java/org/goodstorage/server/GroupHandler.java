package org.goodstorage.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.RequiredArgsConstructor;
import org.goodstorage.auth.AuthenticationManager;
import org.goodstorage.exceptions.ResponseStatusException;
import org.goodstorage.exceptions.ValidationException;
import org.goodstorage.service.GroupService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.goodstorage.server.ResponseSender.*;
import static org.goodstorage.util.UuidUtil.UUID_REGEX;

@RequiredArgsConstructor
public class GroupHandler implements HttpHandler {


    private final GroupService groupService;
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

            authenticationManager.validateAuthentication(exchange);

            if (requestMethod.equals("GET") && requestURI.matches("/api/group")) {
                // GET /api/group
                handleGetAllGroups(exchange);
            } else if (requestMethod.equals("GET") && requestURI.matches("^/api/group/search/\\w+$")) {
                // GET /api/group
                handleSearchByExpression(exchange, getId(requestURI));
            } else if (requestMethod.equals("GET") && requestURI.matches("^/api/group/" + UUID_REGEX + "$")) {
                // GET /api/group/{id}
                handleGetGroupById(exchange, getId(requestURI));
            } else if (requestMethod.equals("POST") && requestURI.equals("/api/group")) {
                // POST /api/group
                handlePostNewGroup(exchange);
            } else if (requestMethod.equals("PUT") && requestURI.matches("^/api/group/" + UUID_REGEX + "$")) {
                // PUT /api/group/{id}
                handlePutExistingGroup(exchange, getId(requestURI));
            } else if (requestMethod.equals("DELETE") && requestURI.matches("^/api/group/" + UUID_REGEX + "$")) {
                // DELETE /api/group/{id}
                handleDeleteGroup(exchange, getId(requestURI));
            } else {
                sendError(exchange, 405, "Endpoint not found");
            }
        } catch (ResponseStatusException e) {
            sendError(exchange, e);
        }
    }

    private void handleGetAllGroups(HttpExchange exchange) throws IOException {
        List<GroupService.GroupResponse> groups = groupService.getGroupResponses();
        String responseJson = gson.toJson(groups);
        sendResponse(exchange, 200, responseJson);
    }

    private void handleSearchByExpression(HttpExchange exchange, String expression) throws IOException {
        List<GroupService.GroupResponse> groups = groupService.searchGroupResponses(expression);
        String responseJson = gson.toJson(groups);
        sendResponse(exchange, 200, responseJson);
    }

    private void handleGetGroupById(HttpExchange exchange, String id) throws IOException {
        GroupService.GroupResponse group = groupService.getGroupResponseById(id);
        String responseJson = gson.toJson(group);
        sendResponse(exchange, 200, responseJson);
    }

    private void handlePostNewGroup(HttpExchange exchange) throws IOException {
        GroupService.GroupRequest request = getValidGroupRequestFromBody(exchange);
        GroupService.GroupResponse createdGroup = groupService.create(request);
        sendResponse(exchange, 201, gson.toJson(createdGroup));
    }

    private GroupService.GroupRequest getValidGroupRequestFromBody(HttpExchange exchange) throws IOException {
        InputStream in = exchange.getRequestBody();
        String requestBody = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        in.close();

        GroupService.GroupRequest request = gson.fromJson(requestBody, GroupService.GroupRequest.class);
        validateGroupRequest(request);
        return request;
    }

    private void handlePutExistingGroup(HttpExchange exchange, String id) throws IOException {
        GroupService.GroupRequest request = getValidGroupRequestFromBody(exchange);
        GroupService.GroupResponse updatedGroup = groupService.update(id, request);
        sendResponse(exchange, 200, gson.toJson(updatedGroup));
    }

    private void handleDeleteGroup(HttpExchange exchange, String id) throws IOException {
        groupService.delete(id);
        sendResponse(exchange, 204, "");
    }


    private void validateGroupRequest(GroupService.GroupRequest request) {
        if (request.getName() == null || request.getName().trim().length() == 0)
            throw new ValidationException("'name' must not be omitted or empty");
        if (request.getDescription() == null || request.getDescription().trim().length() == 0)
            throw new ValidationException("'description' must not be omitted or empty");
    }


}
