package org.goodstorage.server;

import com.sun.net.httpserver.HttpExchange;
import org.goodstorage.exceptions.ResponseStatusException;

import java.io.IOException;
import java.io.OutputStream;

public final class ResponseSender {

    private ResponseSender() {

    }

    public static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        setDefaultHeaders(exchange);
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(response.getBytes());
        outputStream.close();
    }

    public static void sendError(HttpExchange exchange, ResponseStatusException e) throws IOException {
        setDefaultHeaders(exchange);
        sendResponse(exchange, e.getStatusCode(), toJsonErrorMessage(e.getMessage()));
    }

    public static void sendError(HttpExchange exchange, int statusCode, String response) throws IOException {
        setDefaultHeaders(exchange);
        sendResponse(exchange, statusCode, toJsonErrorMessage(response));
    }

    public static void sendOptionResponse(HttpExchange exchange) throws IOException {
        setDefaultHeaders(exchange);
        sendResponse(exchange, 200, "");
    }

    public static void sendHeadResponse(HttpExchange exchange) throws IOException {
        setDefaultHeaders(exchange);
        sendResponse(exchange, 200, "");
    }

    private static String toJsonErrorMessage(String errorMessage) {
        return "{\n\"message\": \"" + errorMessage + "\"\n}";
    }

    private static void setDefaultHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, PUT, POST, DELETE, PATCH, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Access-Control-Allow-Headers, Access-Control-Request-Method, Access-Control-Request-Headers, Authorization");
        exchange.getResponseHeaders().set("Access-Control-Max-Age", "86400");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
    }

}
