package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.domain.valueobject.*;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.domain.dto.create.OrderAddress;
import com.food.ordering.system.order.service.domain.dto.create.OrderItem;
import com.food.ordering.system.order.service.domain.entity.Customer;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.entity.Product;
import com.food.ordering.system.order.service.domain.entity.Restaurant;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.food.ordering.system.order.service.domain.mapper.OrderDataMapper;
import com.food.ordering.system.order.service.domain.ports.input.service.OrderApplicationService;
import com.food.ordering.system.order.service.domain.ports.output.repository.CustomerRepository;
import com.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import com.food.ordering.system.order.service.domain.ports.output.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = OrderTestConfiguration.class)
public class OrderApplicationServiceTest {

    @Autowired
    private OrderApplicationService orderApplicationService;
    @Autowired
    private OrderDataMapper orderDataMapper;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;

    private CreateOrderCommand createOrderCommand;
    private CreateOrderCommand createOrderCommandWrongPrice;
    private CreateOrderCommand createOrderCommandWrongProductPrice;
    private CreateOrderCommand createOrderCommandInactiveRestaurant;

    private final UUID CUSTOMER_ID = UUID.fromString("5dd397aa-8d44-41b8-b41a-14e9a847c7fb");
    private final UUID RESTAURANT_ID = UUID.fromString("539a56cd-6a86-4175-a4d7-8ccd45bad24e");
    private final UUID INACTIVE_RESTAURANT_ID = UUID.fromString("67dcb158-53ec-49eb-8e04-e4839366edf1");
    private final UUID PRODUCT_ID_1 = UUID.fromString("7b8d25ad-3772-437b-a7cd-4f918512c275");
    private final UUID PRODUCT_ID_2 = UUID.fromString("f84ddd0d-9c9e-4eb7-bfbc-5c20ecb3d4fe");
    private final UUID ORDER_ID = UUID.fromString("4fa427b2-c043-4293-a10d-fecbf3642e25");

    private final BigDecimal PRICE = new BigDecimal("200.00");

    @BeforeAll
    public void init() {
        correctInput();
        wrongTotalPrice();
        wrongProductPrice();
        inactiveRestaurant();

        configureMockito();
    }

    @Test
    public void testCreateOrder() {

        var createOrderResponse = orderApplicationService.createOrder(createOrderCommand);

        assertEquals(OrderStatus.PENDING, createOrderResponse.orderStatus());
        assertEquals("Order is created successfully.", createOrderResponse.message());
        assertNotNull(createOrderResponse.orderTrackingId());
    }

    @Test
    public void testCreateOrderWithWrongPrice() {
        var exception = assertThrows(OrderDomainException.class,
                () -> orderApplicationService.createOrder(createOrderCommandWrongPrice));

        assertEquals("Total price: %s is not equal to Order items total: %s."
                        .formatted(createOrderCommandWrongPrice.price(), "200.00"),
                exception.getMessage());
    }

    @Test
    public void testCreateOrderWithWrongProductPrice() {
        var exception = assertThrows(OrderDomainException.class,
                () -> orderApplicationService.createOrder(createOrderCommandWrongProductPrice));

        assertEquals("Order item price: %s is not valid for product %s"
                        .formatted("60.00", PRODUCT_ID_1),
                exception.getMessage());
    }

    @Test
    public void testCreateOrderWithInactiveRestaurant() {

        var exception = assertThrows(OrderDomainException.class,
                () -> orderApplicationService.createOrder(createOrderCommandInactiveRestaurant));

        assertEquals("Restaurant with id %s is currently not active"
                        .formatted(INACTIVE_RESTAURANT_ID),
                exception.getMessage());

    }

    private void configureMockito() {
        Customer customer = new Customer();
        customer.setId(new CustomerId(CUSTOMER_ID));

        Product product1 = new Product(new ProductId(PRODUCT_ID_1), "product-1", new Money(new BigDecimal("50.00")));
        Product product2 = new Product(new ProductId(PRODUCT_ID_2), "product-2", new Money(new BigDecimal("50.00")));

        Restaurant restaurant = Restaurant.builder()
                .id(new RestaurantId(RESTAURANT_ID))
                .products(List.of(product1, product2))
                .active(true)
                .build();

        Restaurant inactiveRestaurant = Restaurant.builder()
                .id(new RestaurantId(INACTIVE_RESTAURANT_ID))
                .products(List.of(product1, product2))
                .active(false)
                .build();

        Order order = orderDataMapper.createOrderCommandToOrder(createOrderCommand);
        order.setId(new OrderId(ORDER_ID));

        //Configure mockito
        Restaurant orderCommandToRestaurant = orderDataMapper.createOrderCommandToRestaurant(createOrderCommand);
        Restaurant orderCommandToInactiveRestaurant = orderDataMapper.createOrderCommandToRestaurant(createOrderCommandInactiveRestaurant);

        when(customerRepository.findCustomer(CUSTOMER_ID)).thenReturn(Optional.of(customer));
        when(restaurantRepository.findRestaurantInformation(orderCommandToRestaurant)).thenReturn(Optional.of(restaurant));
        when(restaurantRepository.findRestaurantInformation(orderCommandToInactiveRestaurant)).thenReturn(Optional.of(inactiveRestaurant));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
    }

    private void correctInput() {
        var orderItems = List.of(
                new OrderItem(PRODUCT_ID_1, 1, new BigDecimal("50.00"), new BigDecimal("50.00")),
                new OrderItem(PRODUCT_ID_2, 3, new BigDecimal("50.00"), new BigDecimal("150.00"))
        );
        var orderAddress = new OrderAddress("street_1", "1000AB", "Paris");
        createOrderCommand = new CreateOrderCommand(CUSTOMER_ID, RESTAURANT_ID, PRICE, orderItems, orderAddress);
    }

    private void wrongTotalPrice() {
        var orderItems = List.of(
                new OrderItem(PRODUCT_ID_1, 1, new BigDecimal("50.00"), new BigDecimal("50.00")),
                new OrderItem(PRODUCT_ID_2, 3, new BigDecimal("50.00"), new BigDecimal("150.00"))
        );
        var orderAddress = new OrderAddress("street_1", "1000AB", "Paris");

        createOrderCommandWrongPrice = new CreateOrderCommand(CUSTOMER_ID, RESTAURANT_ID,
                new BigDecimal("250.0"), orderItems, orderAddress);
    }

    private void wrongProductPrice() {
        var orderItems = List.of(
                new OrderItem(PRODUCT_ID_1, 1, new BigDecimal("60.00"), new BigDecimal("60.00")),
                new OrderItem(PRODUCT_ID_2, 3, new BigDecimal("50.00"), new BigDecimal("150.00"))
        );
        var orderAddress = new OrderAddress("street_1", "1000AB", "Paris");
        createOrderCommandWrongProductPrice = new CreateOrderCommand(CUSTOMER_ID, RESTAURANT_ID,
                new BigDecimal("210.0"), orderItems, orderAddress);
    }

    private void inactiveRestaurant() {
        var orderItems = List.of(
                new OrderItem(PRODUCT_ID_1, 1, new BigDecimal("50.00"), new BigDecimal("50.00")),
                new OrderItem(PRODUCT_ID_2, 3, new BigDecimal("50.00"), new BigDecimal("150.00"))
        );
        var orderAddress = new OrderAddress("street_1", "1000AB", "Paris");
        createOrderCommandInactiveRestaurant = new CreateOrderCommand(CUSTOMER_ID, INACTIVE_RESTAURANT_ID, PRICE, orderItems, orderAddress);
    }
}
