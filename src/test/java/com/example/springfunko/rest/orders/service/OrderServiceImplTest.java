package com.example.springfunko.rest.orders.service;

import com.example.springfunko.rest.funkos.exception.FunkoBadPrice;
import com.example.springfunko.rest.funkos.exception.FunkoNotFound;
import com.example.springfunko.rest.funkos.exception.FunkoNotStock;
import com.example.springfunko.rest.funkos.models.Funko;
import com.example.springfunko.rest.funkos.repositories.FunkoRepository;
import com.example.springfunko.rest.orders.exceptions.OrderNotFound;
import com.example.springfunko.rest.orders.exceptions.OrderNotItems;
import com.example.springfunko.rest.orders.models.Order;
import com.example.springfunko.rest.orders.models.OrderLine;
import com.example.springfunko.rest.orders.repository.OrderRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {
    @Mock
    private OrderRepository pedidosRepository;
    @Mock
    private FunkoRepository funkosRepository;
    @InjectMocks
    private OrderServiceImpl pedidosService;

    @Test
    void findAll() {
        List<Order> pedidos = List.of(new Order(), new Order());
        Page<Order> expectedPage = new PageImpl<>(pedidos);
        Pageable pageable = PageRequest.of(0, 10);
        when(pedidosRepository.findAll(pageable)).thenReturn(expectedPage);
        Page<Order> result = pedidosService.findAll(pageable);
        assertAll(() -> assertEquals(expectedPage, result), () -> assertEquals(expectedPage.getContent(), result.getContent()), () -> assertEquals(expectedPage.getTotalElements(), result.getTotalElements()));
        verify(pedidosRepository, times(1)).findAll(pageable);
    }

    @Test
    void testFindById() {
        ObjectId idOrder = new ObjectId();
        Order expectedOrder = new Order();
        when(pedidosRepository.findById(idOrder)).thenReturn(Optional.of(expectedOrder));
        Order resultOrder = pedidosService.findById(idOrder);
        assertEquals(expectedOrder, resultOrder);
        verify(pedidosRepository).findById(idOrder);
    }

    @Test
    void testFindByIdNotFound() {
        ObjectId idOrder = new ObjectId();
        when(pedidosRepository.findById(idOrder)).thenReturn(Optional.empty());
        assertThrows(OrderNotFound.class, () -> pedidosService.findById(idOrder));
        verify(pedidosRepository).findById(idOrder);
    }

    @Test
    void testFindByIdUser() {
        Long idUsuario = 1L;
        Pageable pageable = mock(Pageable.class);
        @SuppressWarnings("unchecked") Page<Order> expectedPage = mock(Page.class);
        when(pedidosRepository.findByIdUser(idUsuario, pageable)).thenReturn(expectedPage);
        Page<Order> resultPage = pedidosService.findByUserId(idUsuario, pageable);
        assertEquals(expectedPage, resultPage);
        verify(pedidosRepository).findByIdUser(idUsuario, pageable);
    }

    @Test
    void testSave() {
        Funko funko = Funko.builder().id(1L).nombre("Funko 1").precio(10.0).cantidad(5).imagen("https://placehold.co/600x400").categoria(null).build();
        Order pedido = new Order();
        OrderLine lineaOrder = OrderLine.builder().idFunko(1L).quantity(2).price(10.0).total(20.0).build();
        pedido.setOrderLines(List.of(lineaOrder));
        Order pedidoToSave = new Order();
        pedidoToSave.setOrderLines(List.of(lineaOrder));
        when(pedidosRepository.save(any(Order.class))).thenReturn(pedidoToSave); // Utiliza any(Order.class) para cualquier instancia de Order
        when(funkosRepository.findById(anyLong())).thenReturn(Optional.of(funko));
        Order resultOrder = pedidosService.save(pedido);
        assertAll(() -> assertEquals(pedidoToSave, resultOrder), () -> assertEquals(pedidoToSave.getOrderLines(), resultOrder.getOrderLines()), () -> assertEquals(pedidoToSave.getOrderLines().size(), resultOrder.getOrderLines().size()));
        verify(pedidosRepository).save(any(Order.class));
        verify(funkosRepository, times(2)).findById(anyLong());
    }

    @Test
    void testSaveNotItems() {
        Order pedido = new Order();
        assertThrows(OrderNotItems.class, () -> pedidosService.save(pedido));
        verify(pedidosRepository, never()).save(any(Order.class));
        verify(funkosRepository, never()).findById(anyLong());
    }

    @Test
    void testDelete() {
        ObjectId idOrder = new ObjectId();
        Order pedidoToDelete = new Order();
        when(pedidosRepository.findById(idOrder)).thenReturn(Optional.of(pedidoToDelete));
        pedidosService.delete(idOrder);
        verify(pedidosRepository).findById(idOrder);
        verify(pedidosRepository).delete(pedidoToDelete);
    }

    @Test
    void testDeleteNotFound() {
        ObjectId idOrder = new ObjectId();
        when(pedidosRepository.findById(idOrder)).thenReturn(Optional.empty());
        assertThrows(OrderNotFound.class, () -> pedidosService.delete(idOrder));
        verify(pedidosRepository).findById(idOrder);
        verify(pedidosRepository, never()).deleteById(idOrder);
    }

    @Test
    void testUpdate() {
        Funko funko = Funko.builder().id(1L).nombre("Funko 1").precio(10.0).cantidad(5).imagen("https://placehold.co/600x400").categoria(null).build();
        OrderLine lineaOrder = OrderLine.builder().idFunko(1L).quantity(2).price(10.0).total(20.0).build();
        ObjectId idOrder = new ObjectId();
        Order pedido = new Order();
        pedido.setOrderLines(List.of(lineaOrder));
        Order pedidoToUpdate = new Order();
        pedidoToUpdate.setOrderLines(List.of(lineaOrder));
        when(pedidosRepository.findById(idOrder)).thenReturn(Optional.of(pedidoToUpdate));
        when(pedidosRepository.save(any(Order.class))).thenReturn(pedidoToUpdate);
        when(funkosRepository.findById(anyLong())).thenReturn(Optional.of(funko));
        Order resultOrder = pedidosService.update(idOrder, pedido);
        assertAll(() -> assertEquals(pedidoToUpdate, resultOrder), () -> assertEquals(pedidoToUpdate.getOrderLines(), resultOrder.getOrderLines()), () -> assertEquals(pedidoToUpdate.getOrderLines().size(), resultOrder.getOrderLines().size()));
        verify(pedidosRepository).findById(idOrder);
        verify(pedidosRepository).save(any(Order.class));
        verify(funkosRepository, times(3)).findById(anyLong());
    }

    @Test
    void testUpdateNotFound() {
        ObjectId idOrder = new ObjectId();
        Order pedido = new Order();
        when(pedidosRepository.findById(idOrder)).thenReturn(Optional.empty());
        assertThrows(OrderNotFound.class, () -> pedidosService.update(idOrder, pedido));
        verify(pedidosRepository).findById(idOrder);
        verify(pedidosRepository, never()).save(any(Order.class));
        verify(funkosRepository, never()).findById(anyLong());
    }

    @Test
    void testReserveStockOrder() throws OrderNotFound {
        Order pedido = new Order();
        List<OrderLine> lineasOrder = new ArrayList<>();
        OrderLine lineaOrder = OrderLine.builder().idFunko(1L).quantity(2).price(10.0).total(20.0).build();
        lineasOrder.add(lineaOrder);
        pedido.setOrderLines(lineasOrder);
        Funko funko = Funko.builder().id(1L).nombre("Funko 1").precio(10.0).cantidad(5).imagen("https://placehold.co/600x400").categoria(null).build();
        when(funkosRepository.findById(1L)).thenReturn(Optional.of(funko));
        Order result = pedidosService.reserveStockOrder(pedido);
        assertAll(() -> assertEquals(3, funko.getCantidad()),
                () -> assertEquals(20.0, lineaOrder.getTotal()),
                () -> assertEquals(20.0, result.getTotal()),
                () -> assertEquals(2, result.getTotalItems())
        );
        verify(funkosRepository, times(1)).findById(1L);
        verify(funkosRepository, times(1)).save(funko);
    }

    @Test
    void returnStockOrderWithUpdateStock() {
        Order pedido = new Order();
        List<OrderLine> lineasOrder = new ArrayList<>();
        OrderLine lineaOrder1 = OrderLine.builder().idFunko(1L).quantity(2).price(10.0).total(20.0).build();
        lineasOrder.add(lineaOrder1);
        pedido.setOrderLines(lineasOrder);
        Funko funko = Funko.builder().id(1L).nombre("Funko 1").precio(10.0).cantidad(13).imagen("https://placehold.co/600x400").categoria(null).build();
        when(funkosRepository.findById(1L)).thenReturn(Optional.of(funko));
        when(funkosRepository.save(funko)).thenReturn(funko);
        Order result = pedidosService.returnStockOrders(pedido);
        assertEquals(15, funko.getCantidad());
        assertEquals(pedido, result);
        verify(funkosRepository, times(1)).findById(1L);
        verify(funkosRepository, times(1)).save(funko);
    }

    @Test
    void checkOrder_FunkoExistenYHayStock() {
        Order pedido = new Order();
        List<OrderLine> lineasOrder = new ArrayList<>();
        OrderLine lineaOrder1 = OrderLine.builder().idFunko(1L).quantity(2).price(10.0).total(20.0).build();
        lineasOrder.add(lineaOrder1);
        pedido.setOrderLines(lineasOrder);
        Funko funko = Funko.builder().id(1L).nombre("Funko 1").precio(10.0).cantidad(5).imagen("https://placehold.co/600x400").categoria(null).build();
        when(funkosRepository.findById(1L)).thenReturn(Optional.of(funko));
        assertDoesNotThrow(() -> pedidosService.checkOrder(pedido));
        verify(funkosRepository, times(1)).findById(1L);
    }

    @Test
    void checkOrderNotFound() {
        Order pedido = new Order();
        List<OrderLine> lineasOrder = new ArrayList<>();
        OrderLine lineaOrder1 = OrderLine.builder().idFunko(1L).quantity(2).price(10.0).total(20.0).build();
        lineasOrder.add(lineaOrder1);
        pedido.setOrderLines(lineasOrder);
        when(funkosRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(FunkoNotFound.class, () -> pedidosService.checkOrder(pedido));
        verify(funkosRepository, times(1)).findById(1L);
    }

    @Test
    void checkOrderNotEnoughtStock() {
        Order pedido = new Order();
        List<OrderLine> lineasOrder = new ArrayList<>();
        OrderLine lineaOrder1 = OrderLine.builder().idFunko(1L).quantity(2).price(10.0).total(20.0).build();
        lineaOrder1.setIdFunko(1L);
        lineaOrder1.setQuantity(10);
        lineasOrder.add(lineaOrder1);
        pedido.setOrderLines(lineasOrder);
        Funko funko = Funko.builder().id(1L).nombre("Funko 1").precio(10.0).cantidad(5).imagen("https://placehold.co/600x400").categoria(null).build();
        when(funkosRepository.findById(1L)).thenReturn(Optional.of(funko));
        assertThrows(FunkoNotStock.class, () -> pedidosService.checkOrder(pedido));
        verify(funkosRepository, times(1)).findById(1L);
    }

    @Test
    void checkOrderBadPrice() {
        Order pedido = new Order();
        List<OrderLine> lineasOrder = new ArrayList<>();
        OrderLine lineaOrder1 = OrderLine.builder().idFunko(1L).quantity(2).price(10.0).total(20.0).build();
        lineaOrder1.setIdFunko(1L);
        lineaOrder1.setQuantity(2);
        lineaOrder1.setPrice(20.0);
        lineasOrder.add(lineaOrder1);
        pedido.setOrderLines(lineasOrder);
        Funko funko = Funko.builder().id(1L).nombre("Funko 1").precio(10.0).cantidad(5).imagen("https://placehold.co/600x400").categoria(null).build();
        when(funkosRepository.findById(1L)).thenReturn(Optional.of(funko));
        assertThrows(FunkoBadPrice.class, () -> pedidosService.checkOrder(pedido));
        verify(funkosRepository, times(1)).findById(1L);
    }
}