package eu.hexgate.blog.order.web.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.hexgate.blog.order.dto.ErrorDto;
import eu.hexgate.blog.order.dto.OrderDto;
import eu.hexgate.blog.order.forms.OrderForm;
import io.vavr.control.Either;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class OrderClient {

    private static final String PATH = "/orders";
    private static final String AUTH_HEADER = "X-USER-ID";

    private final HttpClient client;
    private final String baseUrl;
    private final ObjectMapper objectMapper;

    public OrderClient(String url, int port) {
        this.client = HttpClient
                .newBuilder()
                .build();

        this.baseUrl = String.format("%s:%s%s", url, port, PATH);
        this.objectMapper = new ObjectMapper();
    }

    public Either<ErrorDto, CreatedOrderDto> createOrder(OrderForm orderForm, String userId) throws URISyntaxException, IOException, InterruptedException {
        final String body = objectMapper.writeValueAsString(orderForm);
        final HttpRequest request = HttpRequest.newBuilder(new URI(baseUrl))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .header(AUTH_HEADER, userId)
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .build();

        final HttpResponse<String> response = send(request);


        if (response.statusCode() == 201) {
            final OrderDto orderDto = resolveDto(response);
            final String location = response.headers()
                    .firstValue("location")
                    .orElse(null);

            return Either.right(new CreatedOrderDto(orderDto, location));
        }

        return resolveError(response);
    }

    public Either<ErrorDto, OrderDto> findOrder(String orderId) throws URISyntaxException, IOException, InterruptedException {
        final HttpRequest request = HttpRequest.newBuilder(new URI(String.format("%s/%s", baseUrl, orderId)))
                .GET()
                .build();

        final HttpResponse<String> response = send(request);

        return resolveDefaultResponse(response);
    }

    public Either<ErrorDto, OrderDto> updateOrderPositions(String orderId, OrderForm orderForm) throws URISyntaxException, IOException, InterruptedException {
        final String body = objectMapper.writeValueAsString(orderForm);
        final HttpRequest request = HttpRequest.newBuilder(new URI(String.format("%s/%s/update-positions", baseUrl, orderId)))
                .method("PATCH", HttpRequest.BodyPublishers.ofString(body))
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .build();

        final HttpResponse<String> response = send(request);

        return resolveDefaultResponse(response);
    }

    public Either<ErrorDto, OrderDto> acceptOrder(String orderId) throws URISyntaxException, IOException, InterruptedException {
        return simpleOperationOnOrder(orderId, "accept");
    }

    public Either<ErrorDto, OrderDto> declineOrder(String orderId) throws URISyntaxException, IOException, InterruptedException {
        return simpleOperationOnOrder(orderId, "decline");
    }

    public Either<ErrorDto, OrderDto> confirmOrder(String orderId) throws URISyntaxException, IOException, InterruptedException {
        return simpleOperationOnOrder(orderId, "confirm");
    }

    private Either<ErrorDto, OrderDto> simpleOperationOnOrder(String orderId, String operation) throws URISyntaxException, IOException, InterruptedException {
        final HttpRequest request = HttpRequest.newBuilder(new URI(String.format("%s/%s/%s", baseUrl, orderId, operation)))
                .method("PATCH", HttpRequest.BodyPublishers.noBody())
                .build();

        final HttpResponse<String> response = send(request);

        return resolveDefaultResponse(response);
    }

    private Either<ErrorDto, OrderDto> resolveDefaultResponse(HttpResponse<String> response) throws JsonProcessingException {
        if (response.statusCode() == 200) {
            final OrderDto orderDto = resolveDto(response);
            return Either.right(orderDto);
        }

        return resolveError(response);
    }

    private OrderDto resolveDto(HttpResponse<String> response) throws JsonProcessingException {
        return objectMapper.readValue(response.body(), OrderDto.class);
    }

    private <R> Either<ErrorDto, R> resolveError(HttpResponse<String> response) throws JsonProcessingException {
        final ErrorDto errorDto = objectMapper.readValue(response.body(), ErrorDto.class);
        return Either.left(errorDto);
    }

    private HttpResponse<String> send(HttpRequest request) throws IOException, InterruptedException {
        return client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );
    }
}
