package eu.hexgate.blog.uglyorder;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.hexgate.blog.uglyorder.dto.OrderDto;
import eu.hexgate.blog.uglyorder.forms.OrderForm;
import eu.hexgate.blog.uglyorder.forms.OrderPositionForm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderTest {

    private static final String VIP_USER_ID = "1";
    private static final String STANDARD_USER_ID = "2";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderService orderService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void shouldCreateNewVipOrder() throws Exception {
        postNewOrder(VIP_USER_ID)
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.status", is("VIP")))
                .andExpect(jsonPath("$.basePrice", is("800.00")))
                .andExpect(jsonPath("$.estimateTotalPrice", is("975.00")))
                .andExpect(jsonPath("$.confirmedTotalPrice", nullValue()))
                .andExpect(jsonPath("$.positions[0].product.id", notNullValue()))
                .andExpect(jsonPath("$.positions[0].product.name", is("Dress")))
                .andExpect(jsonPath("$.positions[0].product.price", is("500.00")))
                .andExpect(jsonPath("$.positions[0].quantity", is(1)))
                .andExpect(jsonPath("$.positions[0].total", is("500.00")))

                .andExpect(jsonPath("$.positions[1].product.id", notNullValue()))
                .andExpect(jsonPath("$.positions[1].product.name", is("Shoes")))
                .andExpect(jsonPath("$.positions[1].product.price", is("150.00")))
                .andExpect(jsonPath("$.positions[1].quantity", is(2)))
                .andExpect(jsonPath("$.positions[1].total", is("300.00")))
        ;
    }

    @Test
    public void shouldCreateNewDraftOrder() throws Exception {
        postNewOrder(STANDARD_USER_ID)
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.status", is("DRAFT")))
        ;
    }

    @Test
    public void shouldFindCreatedOrder() throws Exception {
        final String id = postNewOrderAngGetId(VIP_USER_ID);

        findOrder(id)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id)))
                .andExpect(jsonPath("$.status", is("VIP")))
                .andExpect(jsonPath("$.basePrice", is("800.00")))
                .andExpect(jsonPath("$.estimateTotalPrice", is("975.00")))
                .andExpect(jsonPath("$.confirmedTotalPrice", nullValue()))
                .andExpect(jsonPath("$.positions[0].product.id", notNullValue()))
                .andExpect(jsonPath("$.positions[0].product.name", is("Dress")))
                .andExpect(jsonPath("$.positions[0].product.price", is("500.00")))
                .andExpect(jsonPath("$.positions[0].quantity", is(1)))
                .andExpect(jsonPath("$.positions[0].total", is("500.00")))

                .andExpect(jsonPath("$.positions[1].product.id", notNullValue()))
                .andExpect(jsonPath("$.positions[1].product.name", is("Shoes")))
                .andExpect(jsonPath("$.positions[1].product.price", is("150.00")))
                .andExpect(jsonPath("$.positions[1].quantity", is(2)))
                .andExpect(jsonPath("$.positions[1].total", is("300.00")))
        ;
    }

    @Test
    public void shouldUpdateProducts() throws Exception {
        final String id = postNewOrderAngGetId(VIP_USER_ID);

        updatePositions(id, updatedOrderForm())
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.status", is("VIP")))
                .andExpect(jsonPath("$.basePrice", is("1275.00")))
                .andExpect(jsonPath("$.estimateTotalPrice", is("1545.00")))
                .andExpect(jsonPath("$.confirmedTotalPrice", nullValue()))
                .andExpect(jsonPath("$.positions[0].product.id", notNullValue()))
                .andExpect(jsonPath("$.positions[0].product.name", is("Gloves")))
                .andExpect(jsonPath("$.positions[0].product.price", is("300.00")))
                .andExpect(jsonPath("$.positions[0].quantity", is(3)))
                .andExpect(jsonPath("$.positions[0].total", is("900.00")))

                .andExpect(jsonPath("$.positions[1].product.id", notNullValue()))
                .andExpect(jsonPath("$.positions[1].product.name", is("T-Shirt")))
                .andExpect(jsonPath("$.positions[1].product.price", is("75.00")))
                .andExpect(jsonPath("$.positions[1].quantity", is(5)))
                .andExpect(jsonPath("$.positions[1].total", is("375.00")))
        ;
    }

    @Test
    public void shouldAcceptOrder() throws Exception {
        final String id = postNewOrderAngGetId(STANDARD_USER_ID);

        mockMvc.perform(
                patch(String.format("/orders/%s/accept", id))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id)))
                .andExpect(jsonPath("$.status", is("ACCEPTED")));
    }

    @Test
    public void shouldNotChangeStatusFromAcceptedToDraftIfProductsAreTheSame() throws Exception {
        final String id = postNewOrderAngGetId(STANDARD_USER_ID);

        mockMvc.perform(
                patch(String.format("/orders/%s/accept", id))
                        .contentType(MediaType.APPLICATION_JSON));

        updatePositions(id, splittedSampleOrderForm())
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id)))
                .andExpect(jsonPath("$.status", is("ACCEPTED")));
    }

    @Test
    public void shouldChangeStatusFromAcceptedToDraftIfProductsAreNotTheSame() throws Exception {
        final String id = postNewOrderAngGetId(STANDARD_USER_ID);

        mockMvc.perform(
                patch(String.format("/orders/%s/accept", id))
                        .contentType(MediaType.APPLICATION_JSON));

        updatePositions(id, updatedOrderForm())
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id)))
                .andExpect(jsonPath("$.status", is("DRAFT")));
    }

    @Test
    public void shouldConfirmOffer() throws Exception {
        final String id = postNewOrderAngGetId(VIP_USER_ID);

        mockMvc.perform(
                patch(String.format("/orders/%s/confirm", id))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id)))
                .andExpect(jsonPath("$.status", is("CONFIRMED")));
    }

    @Test
    public void shouldDeclineOffer() throws Exception {
        final String id = postNewOrderAngGetId(STANDARD_USER_ID);

        mockMvc.perform(
                patch(String.format("/orders/%s/accept", id))
                        .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(
                patch(String.format("/orders/%s/decline", id))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id)))
                .andExpect(jsonPath("$.status", is("DRAFT")));
    }

    @Test
    public void shouldNotAcceptOrderIfIsNotDraft() throws Exception {
        final String id = postNewOrderAngGetId(VIP_USER_ID);

        mockMvc.perform(
                patch(String.format("/orders/%s/accept", id))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Your order is not draft.")))
                .andExpect(jsonPath("$.resourceId", is(id)));
    }

    @Test
    public void shouldNotConfirmOrderIfIsDraft() throws Exception {
        final String id = postNewOrderAngGetId(STANDARD_USER_ID);

        mockMvc.perform(
                patch(String.format("/orders/%s/confirm", id))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Your order is neither accepted nor vip.")))
                .andExpect(jsonPath("$.resourceId", is(id)));
    }

    @Test
    public void shouldNotFoundOrder() throws Exception {
        mockMvc.perform(
                get(String.format("/orders/%s", "someId"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    private ResultActions postNewOrder(String userId) throws Exception {
        final OrderForm orderForm = sampleOrderForm();

        return mockMvc.perform(
                post("/orders")
                        .content(objectMapper.writeValueAsString(orderForm))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-USER-ID", userId)
        );
    }

    private String postNewOrderAngGetId(String userId) throws Exception {
        final String createdOrder = postNewOrder(userId)
                .andReturn()
                .getResponse()
                .getContentAsString();

        final OrderDto result = objectMapper.readValue(createdOrder, OrderDto.class);

        return result.getId();
    }

    private ResultActions updatePositions(String orderId, OrderForm orderForm) throws Exception {
        return mockMvc.perform(
                patch(String.format("/orders/%s/update-positions", orderId))
                        .content(objectMapper.writeValueAsString(orderForm))
                        .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions findOrder(String orderId) throws Exception {
        return mockMvc.perform(
                get(String.format("/orders/%s", orderId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-USER-ID", "1")
        );
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
