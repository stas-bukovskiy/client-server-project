package edu.clientserver.pr5.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.clientserver.pr5.authentication.AuthenticationManager;
import edu.clientserver.pr5.domain.Good;
import edu.clientserver.pr5.exception.AuthenticationException;
import edu.clientserver.pr5.exception.ValidationException;
import edu.clientserver.pr5.repository.GoodRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;

@RequiredArgsConstructor
public class GoodsHandler implements HttpHandler {

    private final GoodRepository goodRepository;
    private final AuthenticationManager authenticationManager;
    private final Gson gson = new Gson();

    private static Good toDomain(GoodDto dto) {
        return Good.builder()
                .name(dto.name)
                .quantity(dto.quantity)
                .price(BigDecimal.valueOf(dto.price))
                .build();
    }

    private static Long getId(String uri) {
        return Long.parseLong(uri.substring(uri.lastIndexOf('/') + 1));
    }

    private static String toJsonErrorMessage(String errorMessage) {
        return "{\n\"message\": \"" + errorMessage + "\"\n}";
    }

    @SneakyThrows({SQLException.class})
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        String requestURI = exchange.getRequestURI().toString();
        exchange.getResponseHeaders().set("Content-Type", "application/json");

        List<String> authorization = exchange.getRequestHeaders().get("Authorization");
        if (authorization == null || authorization.size() == 0) {
            sendResponse(exchange, 401, toJsonErrorMessage("Header does not contain 'Authorization'"));
            return;
        }
        try {
            authenticationManager.validate(authorization.get(0).replace("Bearer ", ""));
        } catch (AuthenticationException ex) {
            sendResponse(exchange, 401, toJsonErrorMessage(ex.getMessage()));
        }

        if (requestMethod.equals("GET") && requestURI.matches("^/api/good/\\d+$")) {
            // GET /api/good/{id}
            handleGet(exchange, getId(requestURI));
        } else if (requestMethod.equals("PUT") && requestURI.equals("/api/good")) {
            // PUT /api/good
            handlePut(exchange);
        } else if (requestMethod.equals("POST") && requestURI.matches("^/api/good/\\d+$")) {
            // POST /api/good/{id}
            handlePost(exchange, getId(requestURI));
        } else if (requestMethod.equals("DELETE") && requestURI.matches("^/api/good/\\d+$")) {
            // DELETE /api/good/{id}
            handleDelete(exchange, getId(requestURI));
        } else {
            sendResponse(exchange, 405, toJsonErrorMessage("Endpoint not found"));
        }
    }

    private void handleGet(HttpExchange exchange, long id) throws IOException, SQLException {
        Good good = goodRepository.read(id);

        if (good != null) {
            String responseJson = gson.toJson(good);
            sendResponse(exchange, 200, responseJson);
        } else {
            sendResponse(exchange, 404, toJsonErrorMessage("Good not found"));
        }
    }

    private void handlePut(HttpExchange exchange) throws IOException, SQLException {
        GoodDto dto = getValidDtoFromBody(exchange);
        Good createdGood = goodRepository.create(toDomain(dto));
        sendResponse(exchange, 201, gson.toJson(createdGood));
    }

    private GoodDto getValidDtoFromBody(HttpExchange exchange) throws IOException {
        InputStream in = exchange.getRequestBody();
        String requestBody = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        in.close();

        GoodDto dto = gson.fromJson(requestBody, GoodDto.class);
        try {
            validateGoodDto(dto);
        } catch (ValidationException e) {
            sendResponse(exchange, 409, toJsonErrorMessage(e.getMessage()));
        }
        return dto;
    }

    private void handlePost(HttpExchange exchange, long id) throws SQLException, IOException {
        Good good = goodRepository.read(id);

        if (good != null) {
            GoodDto dto = getValidDtoFromBody(exchange);

            goodRepository.update(id, toDomain(dto));
            sendResponse(exchange, 204, "");
        } else {
            sendResponse(exchange, 404, toJsonErrorMessage("Good not found"));
        }
    }

    private void handleDelete(HttpExchange exchange, long id) throws SQLException, IOException {
        Good good = goodRepository.read(id);

        if (good != null) {
            goodRepository.delete(id);
            sendResponse(exchange, 204, "");
        } else {
            sendResponse(exchange, 404, toJsonErrorMessage("Good not found"));
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(response.getBytes());
        outputStream.close();
    }

    private void validateGoodDto(GoodDto dto) {
        if (dto.name() == null || dto.name().trim().length() == 0)
            throw new ValidationException("'name' must not be omitted or empty");
        if (dto.quantity() == null)
            throw new ValidationException("'quantity' must not be omitted");
        if (dto.quantity() < 0)
            throw new ValidationException("'quantity' must be greater than or equals to 0");
        if (dto.price() == null)
            throw new ValidationException("'price' must not be omitted");
        if (dto.price() <= 0)
            throw new ValidationException("'price' must be greater than 0");
    }

    private record GoodDto(
            String name,
            Integer quantity,
            Double price
    ) {
    }
}
