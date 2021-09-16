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

        final HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );


        if (response.statusCode() == 201) {
            final OrderDto orderDto = resolveDto(response);
            final String location = response.headers()
                    .firstValue("location")
                    .orElse(null);

            return Either.right(new CreatedOrderDto(orderDto, location));
        }

        final ErrorDto errorDto = objectMapper.readValue(response.body(), ErrorDto.class);
        return Either.left(errorDto);
    }

    private OrderDto resolveDto(HttpResponse<String> response) throws JsonProcessingException {
        return objectMapper.readValue(response.body(), OrderDto.class);
    }
}
