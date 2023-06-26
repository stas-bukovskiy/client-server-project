package org.goodstorage.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.RequiredArgsConstructor;
import org.goodstorage.auth.AuthenticationManager;
import org.goodstorage.domain.Good;
import org.goodstorage.exceptions.ResponseStatusException;
import org.goodstorage.exceptions.ValidationException;
import org.goodstorage.service.GoodService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.goodstorage.server.ResponseSender.*;
import static org.goodstorage.util.UuidUtil.UUID_PATTERN;
import static org.goodstorage.util.UuidUtil.UUID_REGEX;

@RequiredArgsConstructor
public class GoodsHandler implements HttpHandler {


    private final GoodService goodService;
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

            if (requestMethod.equals("GET") && requestURI.matches("/api/good")) {
                // GET /api/good
                handleGetAllGoods(exchange);
            } else if (requestMethod.equals("GET") && requestURI.matches("^/api/good/search/\\w*$")) {
                // GET /api/good/{id}
                handleSearchGoodsByExpression(exchange, getId(requestURI));
            } else if (requestMethod.equals("GET") && requestURI.matches("^/api/good/" + UUID_REGEX + "$")) {
                // GET /api/good/{id}
                handleGetGoodById(exchange, getId(requestURI));
            } else if (requestMethod.equals("POST") && requestURI.equals("/api/good")) {
                // POST /api/good
                handlePostNewGood(exchange);
            } else if (requestMethod.equals("PUT") && requestURI.matches("^/api/good/" + UUID_REGEX + "$")) {
                // PUT /api/good/{id}
                handlePutExistingGood(exchange, getId(requestURI));
            } else if (requestMethod.equals("GET") && requestURI.matches("^/api/good/by/group/" + UUID_REGEX + "$")) {
                // PUT /api/good/{id}
                handleGetAllGoodsByGroupId(exchange, getId(requestURI));
            } else if (requestMethod.equals("DELETE") && requestURI.matches("^/api/good/" + UUID_REGEX + "$")) {
                // DELETE /api/good/{id}
                handleDeleteGood(exchange, getId(requestURI));
            } else if (requestMethod.equals("POST") && requestURI.matches("^/api/good/add/" + UUID_REGEX + "$")) {
                // POST /api/good/{id}/add
                handleAddGoods(exchange, getId(requestURI));
            } else if (requestMethod.equals("POST") && requestURI.matches("^/api/good/write-off/" + UUID_REGEX + "$")) {
                // POST /api/good/{id}/write-off
                handleWriteOffGoods(exchange, getId(requestURI));
            } else {
                sendError(exchange, 405, "Endpoint not found");
            }
        } catch (ResponseStatusException e) {
            sendError(exchange, e);
        }
    }

    private void handleGetAllGoods(HttpExchange exchange) throws IOException {
        List<Good> goods = goodService.getGoods();
        String responseJson = gson.toJson(goods);
        sendResponse(exchange, 200, responseJson);
    }

    private void handleSearchGoodsByExpression(HttpExchange exchange, String expression) throws IOException {
        List<Good> goods = goodService.searchGoods(expression);
        String responseJson = gson.toJson(goods);
        sendResponse(exchange, 200, responseJson);
    }

    private void handleGetAllGoodsByGroupId(HttpExchange exchange, String groupID) throws IOException {
        List<Good> goods = goodService.getGoodsByGroupId(groupID);
        String responseJson = gson.toJson(goods);
        sendResponse(exchange, 200, responseJson);
    }

    private void handleGetGoodById(HttpExchange exchange, String id) throws IOException {
        Good good = goodService.getById(id);
        String responseJson = gson.toJson(good);
        sendResponse(exchange, 200, responseJson);
    }

    private void handlePostNewGood(HttpExchange exchange) throws IOException {
        GoodService.GoodRequest request = getValidGoodRequestFromBody(exchange);
        Good createdGood = goodService.create(request);
        sendResponse(exchange, 201, gson.toJson(createdGood));
    }

    private GoodService.GoodRequest getValidGoodRequestFromBody(HttpExchange exchange) throws IOException {
        InputStream in = exchange.getRequestBody();
        String requestBody = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        in.close();

        GoodService.GoodRequest request = gson.fromJson(requestBody, GoodService.GoodRequest.class);
        validateGoodRequest(request);
        return request;
    }

    private void handlePutExistingGood(HttpExchange exchange, String id) throws IOException {
        GoodService.GoodRequest request = getValidGoodRequestFromBody(exchange);
        Good updatedGood = goodService.update(id, request);
        sendResponse(exchange, 200, gson.toJson(updatedGood));
    }

    private void handleDeleteGood(HttpExchange exchange, String id) throws IOException {
        goodService.delete(id);
        sendResponse(exchange, 204, "");
    }

    private void handleAddGoods(HttpExchange exchange, String id) throws IOException {
        ChangeQuantityRequest request = getValidChangeQuantityRequestFromBody(exchange);
        Good createdGood = goodService.addQuantity(id, request.dQuantity);
        sendResponse(exchange, 200, gson.toJson(createdGood));
    }

    private void handleWriteOffGoods(HttpExchange exchange, String id) throws IOException {
        ChangeQuantityRequest request = getValidChangeQuantityRequestFromBody(exchange);
        Good createdGood = goodService.writeOffQuantity(id, request.dQuantity);
        sendResponse(exchange, 200, gson.toJson(createdGood));
    }

    private ChangeQuantityRequest getValidChangeQuantityRequestFromBody(HttpExchange exchange) throws IOException {
        InputStream in = exchange.getRequestBody();
        String requestBody = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        in.close();

        ChangeQuantityRequest request = gson.fromJson(requestBody, ChangeQuantityRequest.class);
        validateChangeQuantityRequest(request);
        return request;
    }

    private void validateGoodRequest(GoodService.GoodRequest request) {
        if (request.getName() == null || request.getName().trim().length() == 0)
            throw new ValidationException("'name' must not be omitted or empty");
        if (request.getDescription() == null || request.getDescription().trim().length() == 0)
            throw new ValidationException("'description' must not be omitted or empty");
        if (request.getProducer() == null || request.getProducer().trim().length() == 0)
            throw new ValidationException("'producer' must not be omitted or empty");
        if (request.getQuantity() == null)
            throw new ValidationException("'quantity' must not be omitted");
        if (request.getQuantity() < 0)
            throw new ValidationException("'quantity' must be greater than or equals to 0");
        if (request.getPrice() == null)
            throw new ValidationException("'price' must not be omitted");
        if (request.getPrice() <= 0)
            throw new ValidationException("'price' must be greater than 0.00");
        if (request.getGroupId() == null || request.getGroupId().trim().length() == 0)
            throw new ValidationException("'groupId' must not be omitted or empty");
        if (!UUID_PATTERN.matcher(request.getGroupId()).matches()) {
            throw new ValidationException("'groupId' must be valid UUID");
        }
    }

    private void validateChangeQuantityRequest(ChangeQuantityRequest request) {
        if (request.dQuantity() == null)
            throw new ValidationException("'dQuantity' must not be omitted");
        if (request.dQuantity() < 0)
            throw new ValidationException("'dQuantity' must be greater than or equals to 0");
    }

    private record ChangeQuantityRequest(
            Integer dQuantity
    ) {

    }

}
