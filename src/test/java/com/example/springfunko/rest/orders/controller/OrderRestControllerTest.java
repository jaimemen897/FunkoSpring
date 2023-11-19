package com.example.springfunko.rest.orders.controller;

import com.example.springfunko.rest.funkos.exception.FunkoBadPrice;
import com.example.springfunko.rest.funkos.exception.FunkoNotFound;
import com.example.springfunko.rest.funkos.exception.FunkoNotStock;
import com.example.springfunko.rest.orders.exceptions.OrderNotFound;
import com.example.springfunko.rest.orders.exceptions.OrderNotItems;
import com.example.springfunko.rest.orders.models.Client;
import com.example.springfunko.rest.orders.models.Direction;
import com.example.springfunko.rest.orders.models.Order;
import com.example.springfunko.rest.orders.models.OrderLine;
import com.example.springfunko.rest.orders.service.OrderService;
import com.example.springfunko.utils.pagination.PageResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class OrdersRestControllerTest {
    private final String myEndpoint = "/api/pedidos";
    private final ObjectMapper mapper = new ObjectMapper();
    private final Order pedido1 = Order.builder()
            .id(new ObjectId("5f9f1a3b9d6b6d2e3c1d6f1a"))
            .idUser(1L)
            .client(
                    new Client("JoseLuisGS", "joseluisgs@soydev.dev", "1234567890",
                            new Direction("Calle", "1", "Ciudad", "Provincia", "Pais", "12345")
                    )
            )
            .orderLines(List.of(OrderLine.builder()
                    .idFunko(1L)
                    .quantity(2)
                    .price(10.0)
                    .build()))
            .build();
    @Autowired
    MockMvc mockMvc;
    @MockBean
    private OrderService orderService;

    @Autowired
    public OrdersRestControllerTest(OrderService orderService) {
        this.orderService = orderService;
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    void getAllOrders() throws Exception {
        var pedidosList = List.of(pedido1);
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(pedidosList);

        // Arrange
        when(orderService.findAll(pageable)).thenReturn(page);

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        get(myEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        PageResponse<Order> res = mapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });

        // Assert
        assertAll("findall",
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(1, res.content().size())
        );

        // Verify
        verify(orderService, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void getOrderById() throws Exception {
        // Arrange
        var myLocalEndpoint = myEndpoint + "/5f9f1a3b9d6b6d2e3c1d6f1a";

        // Arrange
        when(orderService.findById(any(ObjectId.class))).thenReturn(pedido1);

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        get(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Order res = mapper.readValue(response.getContentAsString(), Order.class);

        // Assert
        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(pedido1, res)
        );

        // Verify
        verify(orderService, times(1)).findById(any(ObjectId.class));
    }

    @Test
    void getOrderByIdNoFound() throws Exception {
        // Arrange
        var myLocalEndpoint = myEndpoint + "/5f9f1a3b9d6b6d2e3c1d6f1a";

        // Arrange
        when(orderService.findById(any(ObjectId.class)))
                .thenThrow(new OrderNotFound("5f9f1a3b9d6b6d2e3c1d6f1a"));

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        get(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Assert
        assertAll(
                () -> assertEquals(404, response.getStatus())
        );

        // Verify
        verify(orderService, times(1)).findById(any(ObjectId.class));
    }

    @Test
    void getOrdersByUsuario() throws Exception {
        // Arrange
        var myLocalEndpoint = myEndpoint + "/user/1";
        var pedidosList = List.of(pedido1);
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(pedidosList);

        // Arrange
        when(orderService.findByUserId(anyLong(), any(Pageable.class))).thenReturn(page);

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        get(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        PageResponse<Order> res = mapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });

        // Assert
        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(1, res.content().size())
        );

        // Verify
        verify(orderService, times(1)).findByUserId(anyLong(), any(Pageable.class));
    }

    @Test
    void createOrder() throws Exception {
        // Arrange
        when(orderService.save(any(Order.class))).thenReturn(pedido1);

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        post(myEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                // Le paso el body
                                .content(mapper.writeValueAsString(pedido1)))
                .andReturn().getResponse();

        Order res = mapper.readValue(response.getContentAsString(), Order.class);

        // Assert
        assertAll(
                () -> assertEquals(201, response.getStatus()),
                () -> assertEquals(pedido1, res)
        );

        // Verify
        verify(orderService, times(1)).save(any(Order.class));
    }

    @Test
    void createOrderNoItemsBadRequest() throws Exception {
        // Arrange
        when(orderService.save(any(Order.class))).thenThrow(new OrderNotItems("5f9f1a3b9d6b6d2e3c1d6f1a"));

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        post(myEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                // Le paso el body
                                .content(mapper.writeValueAsString(pedido1)))
                .andReturn().getResponse();

        // Assert
        assertAll(
                () -> assertEquals(400, response.getStatus())
        );

        // Verify
        verify(orderService).save(any(Order.class));
    }

    @Test
    void createOrderFunkoBadPriceBadRequest() throws Exception {
        // Arrange
        when(orderService.save(any(Order.class))).thenThrow(new FunkoBadPrice("Bad price"));

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        post(myEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                // Le paso el body
                                .content(mapper.writeValueAsString(pedido1)))
                .andReturn().getResponse();

        // Assert
        assertAll(
                () -> assertEquals(400, response.getStatus())
        );

        // Verify
        verify(orderService).save(any(Order.class));
    }

    @Test
    void getOrdersFunkoNotFound() throws Exception {
        // Arrange
        when(orderService.save(any(Order.class))).thenThrow(new FunkoNotFound("Funko not found"));

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        post(myEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                // Le paso el body
                                .content(mapper.writeValueAsString(pedido1)))
                .andReturn().getResponse();

        // Assert
        assertAll(
                () -> assertEquals(404, response.getStatus())
        );

        // Verify
        verify(orderService).save(any(Order.class));
    }

    @Test
    void getOrdersFunkoNotStockBadRequest() throws Exception {
        // Arrange
        when(orderService.save(any(Order.class))).thenThrow(new FunkoNotStock("Funko not stock"));

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        post(myEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                // Le paso el body
                                .content(mapper.writeValueAsString(pedido1)))
                .andReturn().getResponse();

        // Assert
        assertAll(
                () -> assertEquals(400, response.getStatus())
        );

        // Verify
        verify(orderService).save(any(Order.class));
    }

    @Test
    void updateProduct() throws Exception {
        var myLocalEndpoint = myEndpoint + "/5f9f1a3b9d6b6d2e3c1d6f1a";

        // Arrange
        when(orderService.update(any(ObjectId.class), any(Order.class))).thenReturn(pedido1);

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        put(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                // Le paso el body
                                .content(mapper.writeValueAsString(pedido1)))
                .andReturn().getResponse();

        Order res = mapper.readValue(response.getContentAsString(), Order.class);

        // Assert
        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(pedido1, res)
        );

        // Verify
        verify(orderService, times(1)).update(any(ObjectId.class), any(Order.class));
    }

    @Test
    void updateOrderNoFound() throws Exception {
        var myLocalEndpoint = myEndpoint + "/5f9f1a3b9d6b6d2e3c1d6f1a";

        // Arrange
        when(orderService.update(any(ObjectId.class), any(Order.class)))
                .thenThrow(new OrderNotFound("5f9f1a3b9d6b6d2e3c1d6f1a"));

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        put(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                // Le paso el body
                                .content(mapper.writeValueAsString(pedido1)))
                .andReturn().getResponse();

        // Assert
        assertAll(
                () -> assertEquals(404, response.getStatus())
        );

        // Verify
        verify(orderService, times(1)).update(any(ObjectId.class), any(Order.class));
    }

    // Habría que testear casi lo mismo en el save con el update

    @Test
    void deleteOrder() throws Exception {
        var myLocalEndpoint = myEndpoint + "/5f9f1a3b9d6b6d2e3c1d6f1a";

        // Arrange
        doNothing().when(orderService).delete(any(ObjectId.class));

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        delete(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Assert
        assertAll(
                () -> assertEquals(204, response.getStatus())
        );

        // Verify
        verify(orderService, times(1)).delete(any(ObjectId.class));
    }

    @Test
    void deleteOrderNoFound() throws Exception {
        var myLocalEndpoint = myEndpoint + "/5f9f1a3b9d6b6d2e3c1d6f1a";

        // Arrange
        doThrow(new OrderNotFound("5f9f1a3b9d6b6d2e3c1d6f1a")).when(orderService).delete(any(ObjectId.class));

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        delete(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Assert
        assertAll(
                () -> assertEquals(404, response.getStatus())
        );

        // Verify
        verify(orderService, times(1)).delete(any(ObjectId.class));
    }
}