package eu.hexgate.blog;

import eu.hexgate.blog.order.dto.ErrorDto;
import eu.hexgate.blog.order.dto.OrderDto;
import eu.hexgate.blog.order.forms.OrderForm;
import eu.hexgate.blog.order.forms.OrderPositionForm;
import eu.hexgate.blog.order.usecase.process.OrderStatus;
import eu.hexgate.blog.order.web.client.CreatedOrderDto;
import eu.hexgate.blog.order.web.client.OrderClient;
import io.vavr.control.Either;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.List;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class OrderTest {

    private static final String VIP_USER_ID = "1";
    private static final String STANDARD_USER_ID = "2";

    private static final String URL = "http://localhost";

    @LocalServerPort
    private int port;

    private OrderClient orderClient;

    @BeforeEach
    public void setup() {
        orderClient = new OrderClient(URL, port);
    }

    @Test
    public void shouldCreateNewVipOrder() throws InterruptedException, IOException, URISyntaxException {
        final Either<ErrorDto, CreatedOrderDto> result = orderClient.createOrder(sampleOrderForm(), VIP_USER_ID);

        CustomAssertions.assertIsRight(result, right -> {
            final OrderDto order = right.getCreatedOrder();
            assertAll(
                    () -> assertThat(order.getId()).isNotNull(),
                    () -> assertThat(order.getStatus()).isEqualTo(OrderStatus.VIP),
                    () -> assertThat(order.getBasePrice()).isEqualTo(new BigDecimal("800.00")),
                    () -> assertThat(order.getEstimatedTotalPrice()).isEqualTo(new BigDecimal("975.00")),
                    () -> assertThat(order.getConfirmedTotalPrice()).isNull(),
                    () -> assertThat(order.getPositions()).hasSize(2),

                    () -> assertThat(order.getPositions().get(0).getProduct().getId()).isNotNull(),
                    () -> assertThat(order.getPositions().get(0).getProduct().getName()).isEqualTo("Dress"),
                    () -> assertThat(order.getPositions().get(0).getProduct().getPrice()).isEqualTo(new BigDecimal("500.00")),
                    () -> assertThat(order.getPositions().get(0).getQuantity()).isEqualTo(1),
                    () -> assertThat(order.getPositions().get(0).getTotal()).isEqualTo(new BigDecimal("500.00")),

                    () -> assertThat(order.getPositions().get(1).getProduct().getId()).isNotNull(),
                    () -> assertThat(order.getPositions().get(1).getProduct().getName()).isEqualTo("Shoes"),
                    () -> assertThat(order.getPositions().get(1).getProduct().getPrice()).isEqualTo(new BigDecimal("150.00")),
                    () -> assertThat(order.getPositions().get(1).getQuantity()).isEqualTo(2),
                    () -> assertThat(order.getPositions().get(1).getTotal()).isEqualTo(new BigDecimal("300.00")),
                    () -> assertThat(right.getLocation()).isNotNull()
            );
        });
    }

    @Test
    public void shouldCreateNewDraftOrder() throws Exception {
        final Either<ErrorDto, CreatedOrderDto> result = orderClient.createOrder(sampleOrderForm(), STANDARD_USER_ID);

        CustomAssertions.assertIsRight(result, right -> {
            final OrderDto order = right.getCreatedOrder();
            assertAll(
                    () -> assertThat(order.getId()).isNotNull(),
                    () -> assertThat(order.getStatus()).isEqualTo(OrderStatus.DRAFT),
                    () -> assertThat(right.getLocation()).isNotNull()
            );
        });
    }

    @Test
    public void shouldFindCreatedOrder() throws Exception {
        final String createdOrderId = resolveOrderId(orderClient.createOrder(sampleOrderForm(), VIP_USER_ID));

        final Either<ErrorDto, OrderDto> result = orderClient.findOrder(createdOrderId);

        CustomAssertions.assertIsRight(result, order -> assertAll(
                () -> assertThat(order.getId()).isEqualTo(createdOrderId),
                () -> assertThat(order.getStatus()).isEqualTo(OrderStatus.VIP),
                () -> assertThat(order.getBasePrice()).isEqualTo(new BigDecimal("800.00")),
                () -> assertThat(order.getEstimatedTotalPrice()).isEqualTo(new BigDecimal("975.00")),
                () -> assertThat(order.getConfirmedTotalPrice()).isNull(),
                () -> assertThat(order.getPositions()).hasSize(2),

                () -> assertThat(order.getPositions().get(0).getProduct().getId()).isNotNull(),
                () -> assertThat(order.getPositions().get(0).getProduct().getName()).isEqualTo("Dress"),
                () -> assertThat(order.getPositions().get(0).getProduct().getPrice()).isEqualTo(new BigDecimal("500.00")),
                () -> assertThat(order.getPositions().get(0).getQuantity()).isEqualTo(1),
                () -> assertThat(order.getPositions().get(0).getTotal()).isEqualTo(new BigDecimal("500.00")),

                () -> assertThat(order.getPositions().get(1).getProduct().getId()).isNotNull(),
                () -> assertThat(order.getPositions().get(1).getProduct().getName()).isEqualTo("Shoes"),
                () -> assertThat(order.getPositions().get(1).getProduct().getPrice()).isEqualTo(new BigDecimal("150.00")),
                () -> assertThat(order.getPositions().get(1).getQuantity()).isEqualTo(2),
                () -> assertThat(order.getPositions().get(1).getTotal()).isEqualTo(new BigDecimal("300.00"))
        ));
    }

    @Test
    public void shouldUpdateProducts() throws Exception {
        final String createdOrderId = resolveOrderId(orderClient.createOrder(sampleOrderForm(), VIP_USER_ID));

        final Either<ErrorDto, OrderDto> result = orderClient.updateOrderPositions(createdOrderId, updatedOrderForm());

        CustomAssertions.assertIsRight(result, order -> assertAll(
                () -> assertThat(order.getId()).isEqualTo(createdOrderId),
                () -> assertThat(order.getStatus()).isEqualTo(OrderStatus.VIP),
                () -> assertThat(order.getBasePrice()).isEqualTo(new BigDecimal("1275.00")),
                () -> assertThat(order.getEstimatedTotalPrice()).isEqualTo(new BigDecimal("1545.00")),
                () -> assertThat(order.getConfirmedTotalPrice()).isNull(),
                () -> assertThat(order.getPositions()).hasSize(2),

                () -> assertThat(order.getPositions().get(0).getProduct().getId()).isNotNull(),
                () -> assertThat(order.getPositions().get(0).getProduct().getName()).isEqualTo("Gloves"),
                () -> assertThat(order.getPositions().get(0).getProduct().getPrice()).isEqualTo(new BigDecimal("300.00")),
                () -> assertThat(order.getPositions().get(0).getQuantity()).isEqualTo(3),
                () -> assertThat(order.getPositions().get(0).getTotal()).isEqualTo(new BigDecimal("900.00")),

                () -> assertThat(order.getPositions().get(1).getProduct().getId()).isNotNull(),
                () -> assertThat(order.getPositions().get(1).getProduct().getName()).isEqualTo("T-Shirt"),
                () -> assertThat(order.getPositions().get(1).getProduct().getPrice()).isEqualTo(new BigDecimal("75.00")),
                () -> assertThat(order.getPositions().get(1).getQuantity()).isEqualTo(5),
                () -> assertThat(order.getPositions().get(1).getTotal()).isEqualTo(new BigDecimal("375.00"))
        ));
    }

    @Test
    public void shouldAcceptOrder() throws Exception {
        final String createdOrderId = resolveOrderId(orderClient.createOrder(sampleOrderForm(), STANDARD_USER_ID));

        final Either<ErrorDto, OrderDto> result = orderClient.acceptOrder(createdOrderId);

        CustomAssertions.assertIsRight(result, order -> assertAll(
                () -> assertThat(order.getId()).isEqualTo(createdOrderId),
                () -> assertThat(order.getStatus()).isEqualTo(OrderStatus.ACCEPTED)
        ));
    }

    @Test
    public void shouldNotChangeStatusFromAcceptedToDraftIfProductsAreTheSame() throws Exception {
        final String createdOrderId = resolveOrderId(orderClient.createOrder(sampleOrderForm(), STANDARD_USER_ID));

        orderClient.acceptOrder(createdOrderId);

        final Either<ErrorDto, OrderDto> result = orderClient.updateOrderPositions(createdOrderId, splittedSampleOrderForm());

        CustomAssertions.assertIsRight(result, order -> assertAll(
                () -> assertThat(order.getId()).isEqualTo(createdOrderId),
                () -> assertThat(order.getStatus()).isEqualTo(OrderStatus.ACCEPTED)
        ));
    }

    @Test
    public void shouldChangeStatusFromAcceptedToDraftIfProductsAreNotTheSame() throws Exception {
        final String createdOrderId = resolveOrderId(orderClient.createOrder(sampleOrderForm(), STANDARD_USER_ID));

        orderClient.acceptOrder(createdOrderId);

        final Either<ErrorDto, OrderDto> result = orderClient.updateOrderPositions(createdOrderId, updatedOrderForm());

        CustomAssertions.assertIsRight(result, order -> assertAll(
                () -> assertThat(order.getId()).isEqualTo(createdOrderId),
                () -> assertThat(order.getStatus()).isEqualTo(OrderStatus.DRAFT)
        ));
    }

    @Test
    public void shouldConfirmOffer() throws Exception {
        final String createdOrderId = resolveOrderId(orderClient.createOrder(sampleOrderForm(), VIP_USER_ID));

        final Either<ErrorDto, OrderDto> result = orderClient.confirmOrder(createdOrderId);

        CustomAssertions.assertIsRight(result, order -> assertAll(
                () -> assertThat(order.getId()).isEqualTo(createdOrderId),
                () -> assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRMED)
        ));
    }

    @Test
    public void shouldDeclineOffer() throws Exception {
        final String createdOrderId = resolveOrderId(orderClient.createOrder(sampleOrderForm(), STANDARD_USER_ID));

        orderClient.acceptOrder(createdOrderId);

        final Either<ErrorDto, OrderDto> result = orderClient.declineOrder(createdOrderId);

        CustomAssertions.assertIsRight(result, order -> assertAll(
                () -> assertThat(order.getId()).isEqualTo(createdOrderId),
                () -> assertThat(order.getStatus()).isEqualTo(OrderStatus.DRAFT)
        ));
    }

    @Test
    public void shouldNotAcceptOrderIfIsNotDraft() throws Exception {
        final String createdOrderId = resolveOrderId(orderClient.createOrder(sampleOrderForm(), VIP_USER_ID));

        final Either<ErrorDto, OrderDto> result = orderClient.acceptOrder(createdOrderId);

        CustomAssertions.assertIsLeft(result, error -> assertAll(
                () -> assertThat(error.getMessage()).isEqualTo("Your order is not draft."),
                () -> assertThat(error.getResourceId()).isEqualTo(createdOrderId)
        ));
    }

    @Test
    public void shouldNotConfirmOrderIfIsDraft() throws Exception {
        final String createdOrderId = resolveOrderId(orderClient.createOrder(sampleOrderForm(), STANDARD_USER_ID));

        final Either<ErrorDto, OrderDto> result = orderClient.confirmOrder(createdOrderId);

        CustomAssertions.assertIsLeft(result, error -> assertAll(
                () -> assertThat(error.getMessage()).isEqualTo("Your order is neither accepted nor vip."),
                () -> assertThat(error.getResourceId()).isEqualTo(createdOrderId)
        ));
    }

    @Test
    public void shouldNotFoundOrder() throws Exception {
        final Either<ErrorDto, OrderDto> result = orderClient.findOrder("someId");

        CustomAssertions.assertIsLeft(result, error -> assertAll(
                () -> assertThat(error.getMessage()).isEqualTo("not found"),
                () -> assertThat(error.getResourceId()).isNull()
        ));
    }

    private String resolveOrderId(Either<ErrorDto, CreatedOrderDto> createdOrderDto) {
        return createdOrderDto.map(it -> it.getCreatedOrder().getId())
                .getOrElseThrow((Supplier<RuntimeException>) RuntimeException::new);
    }

    private OrderForm sampleOrderForm() {
        return new OrderForm(List.of(
                new OrderPositionForm("1", 2),
                new OrderPositionForm("2", 1)));
    }

    private OrderForm splittedSampleOrderForm() {
        return new OrderForm(List.of(
                new OrderPositionForm("1", 1),
                new OrderPositionForm("2", 1),
                new OrderPositionForm("1", 1))
        );
    }

    private OrderForm updatedOrderForm() {
        return new OrderForm(List.of(
                new OrderPositionForm("3", 5),
                new OrderPositionForm("4", 3))
        );
    }
}
