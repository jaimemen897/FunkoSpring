package com.example.springfunko.Funko.services;

import com.example.springfunko.config.websockets.WebSocketConfig;
import com.example.springfunko.config.websockets.WebSocketHandler;
import com.example.springfunko.rest.category.models.Categoria;
import com.example.springfunko.rest.category.repositories.CategoryRepository;
import com.example.springfunko.rest.funkos.dto.FunkoCreateDto;
import com.example.springfunko.rest.funkos.dto.FunkoResponseDto;
import com.example.springfunko.rest.funkos.dto.FunkoUpdateDto;
import com.example.springfunko.rest.funkos.exception.FunkoNotFound;
import com.example.springfunko.rest.funkos.mapper.FunkoMapper;
import com.example.springfunko.rest.funkos.models.Funko;
import com.example.springfunko.rest.funkos.repositories.FunkoRepository;
import com.example.springfunko.rest.funkos.services.FunkoServiceImpl;
import com.example.springfunko.rest.storage.services.StorageService;
import com.example.springfunko.websockets.notifications.mapper.FunkoNotificationMapper;
import com.example.springfunko.websockets.notifications.models.Notification;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FunkoServiceImplTest {
    private final Categoria categoria1 = Categoria.builder().id(null).name("Disney").build();
    private final Categoria categoria2 = Categoria.builder().id(null).name("Serie").build();

    private final Funko funko1 = Funko.builder()
            .id(1L)
            .nombre("nombre4")
            .precio(70.89)
            .cantidad(3)
            .imagen("rutaImagen4")
            .categoria(categoria1)
            .build();

    private final Funko funko2 = Funko.builder()
            .id(2L)
            .nombre("nombre5")
            .precio(54.52)
            .cantidad(1)
            .imagen("rutaImagen5")
            .categoria(categoria2)
            .build();

    WebSocketHandler webSocketHandlerMock = mock(WebSocketHandler.class);
    @Mock
    private FunkoRepository funkoRepository;
    @Mock
    private StorageService storageService;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private FunkoMapper funkoMapper;
    @Mock
    private WebSocketConfig webSocketConfig;
    @Mock
    private FunkoNotificationMapper funkoNotificationMapper;
    @InjectMocks
    private FunkoServiceImpl funkoServiceImpl;
    @Captor
    private ArgumentCaptor<Funko> funkoArgumentCaptor;

    @BeforeEach
    void setUp(){
        funkoServiceImpl.setWebSocketService(webSocketHandlerMock);
    }

    @Test
    void findAll() {
        List<Funko> expectedFunkos = Arrays.asList(funko1, funko2);
        when(funkoRepository.findAll()).thenReturn(expectedFunkos);
        List<Funko> actualFunkos = funkoServiceImpl.findAll(null, null);
        assertIterableEquals(expectedFunkos, actualFunkos);
        verify(funkoRepository, times(1)).findAll();
    }

    @Test
    void findAllByCategoria() {
        String categoria = "Disney";
        List<Funko> expectedFunkos = List.of(funko1);
        when(funkoRepository.findAllByCategoriaName(categoria)).thenReturn(expectedFunkos);
        List<Funko> actualFunkos = funkoServiceImpl.findAll(null, categoria);
        assertIterableEquals(expectedFunkos, actualFunkos);
        verify(funkoRepository, times(1)).findAllByCategoriaName(categoria);
    }

    @Test
    void findAllByNombre() {
        String nombre = "nombre4";
        List<Funko> expectedFunkos = List.of(funko1);
        when(funkoRepository.findAllByNombre(nombre)).thenReturn(expectedFunkos);
        List<Funko> actualFunkos = funkoServiceImpl.findAll(nombre, null);
        assertIterableEquals(expectedFunkos, actualFunkos);
        verify(funkoRepository, times(1)).findAllByNombre(nombre);
    }

    @Test
    void findAllByNombreAndCategoria() {
        String nombre = "nombre4";
        String categoria = "dc";
        List<Funko> expectedFunkos = List.of(funko1);
        when(funkoRepository.findAllByNombreAndCategoriaName(nombre, categoria)).thenReturn(expectedFunkos);
        List<Funko> actualFunkos = funkoServiceImpl.findAll(nombre, categoria);
        assertIterableEquals(expectedFunkos, actualFunkos);
        verify(funkoRepository, times(1)).findAllByNombreAndCategoriaName(nombre, categoria);
    }

    @Test
    void findById() {
        Long id = 1L;
        Funko expectedFunko = funko1;
        when(funkoRepository.findById(id)).thenReturn(Optional.ofNullable(expectedFunko));
        Funko actualFunko = funkoServiceImpl.findById(id);
        assertEquals(expectedFunko, actualFunko);
        verify(funkoRepository, times(1)).findById(id);
    }

    @Test
    void findByIdNoExiste() {
        Long id = 1L;
        when(funkoRepository.findById(id)).thenReturn(Optional.empty());
        var res = assertThrows(FunkoNotFound.class, () -> funkoServiceImpl.findById(id));
        assertEquals("Funko no encontrado", res.getMessage());
        verify(funkoRepository, times(1)).findById(id);
    }

    @Test
    void save() throws IOException {
        FunkoCreateDto funkoCreateDto = FunkoCreateDto.builder()
                .nombre("nombre4")
                .precio(70.89)
                .cantidad(3)
                .imagen("rutaImagen4")
                .categoria(categoria1)
                .build();
        Funko expectedFunko = Funko.builder()
                .id(1L)
                .nombre("nombre4")
                .precio(70.89)
                .cantidad(3)
                .imagen("rutaImagen4")
                .categoria(categoria2)
                .build();

        when(categoryRepository.findByNameContainingIgnoreCase(funkoCreateDto.categoria().getName())).thenReturn(Optional.of(categoria1));
        when(funkoRepository.save(funkoArgumentCaptor.capture())).thenReturn(expectedFunko);
        when(funkoMapper.toFunko(funkoCreateDto, categoria1)).thenReturn(expectedFunko);
        when(funkoMapper.toFunkoResponseDto(expectedFunko)).thenReturn(new FunkoResponseDto(
                expectedFunko.getId(),
                expectedFunko.getNombre(),
                expectedFunko.getPrecio(),
                expectedFunko.getCantidad(),
                expectedFunko.getImagen(),
                expectedFunko.getCategoria(),
                expectedFunko.getFechaCreacion(),
                expectedFunko.getFechaActualizacion()
        ));
        doNothing().when(webSocketHandlerMock).sendMessage(any());

        FunkoResponseDto actualFunko = funkoServiceImpl.save(funkoCreateDto);

        assertAll(
                () -> assertEquals(expectedFunko.getNombre(), actualFunko.nombre()),
                () -> assertEquals(expectedFunko.getPrecio(), actualFunko.precio()),
                () -> assertEquals(expectedFunko.getCantidad(), actualFunko.cantidad()),
                () -> assertEquals(expectedFunko.getImagen(), actualFunko.imagen()),
                () -> assertEquals(expectedFunko.getCategoria(), actualFunko.categoria())
        );

        verify(categoryRepository, times(1)).findByNameContainingIgnoreCase(funkoCreateDto.categoria().getName());
        verify(funkoRepository, times(1)).save(funkoArgumentCaptor.capture());
        verify(funkoMapper, times(1)).toFunko(funkoCreateDto, categoria1);
    }

    @Test
    void update() throws IOException {
        long id = 1L;
        FunkoUpdateDto funkoUpdateRequest = new FunkoUpdateDto(
                "nombre4",
                70.89,
                3,
                "rutaImagen4",
                categoria1
        );
        Funko existingFunko = funko1;

        FunkoResponseDto expectedFunkoResponseDto = new FunkoResponseDto(
                existingFunko.getId(),
                existingFunko.getNombre(),
                existingFunko.getPrecio(),
                existingFunko.getCantidad(),
                existingFunko.getImagen(),
                existingFunko.getCategoria(),
                existingFunko.getFechaCreacion(),
                existingFunko.getFechaActualizacion()
        );

        when(funkoRepository.findById(id)).thenReturn(Optional.of(existingFunko));
        when(categoryRepository.findByNameContainingIgnoreCase(funkoUpdateRequest.categoria().getName())).thenReturn(Optional.of(categoria1));
        when(funkoRepository.save(existingFunko)).thenReturn(existingFunko);
        when(funkoMapper.toFunko(funkoUpdateRequest, funko1, categoria1)).thenReturn(existingFunko);
        when(funkoMapper.toFunkoResponseDto(existingFunko)).thenReturn(expectedFunkoResponseDto);
        doNothing().when(webSocketHandlerMock).sendMessage(any());

        FunkoResponseDto actualFunko = funkoServiceImpl.update(funkoUpdateRequest, id);

        assertAll(
                () -> assertEquals(funkoUpdateRequest.nombre(), actualFunko.nombre()),
                () -> assertEquals(funkoUpdateRequest.precio(), actualFunko.precio()),
                () -> assertEquals(funkoUpdateRequest.cantidad(), actualFunko.cantidad()),
                () -> assertEquals(funkoUpdateRequest.imagen(), actualFunko.imagen()),
                () -> assertEquals(funkoUpdateRequest.categoria(), actualFunko.categoria())
        );

        verify(funkoRepository, times(1)).findById(id);
        verify(categoryRepository, times(1)).findByNameContainingIgnoreCase(funkoUpdateRequest.categoria().getName());
        verify(funkoRepository, times(1)).save(funkoArgumentCaptor.capture());
        verify(funkoMapper, times(1)).toFunko(funkoUpdateRequest, funko1, categoria1);
    }

    @Test
    void updateNoExiste() {
        Long id = 1L;
        FunkoUpdateDto funkoUpdateDto = new FunkoUpdateDto(
                "nombre4",
                70.89,
                3,
                "rutaImagen4",
                categoria1
        );

        when(funkoRepository.findById(id)).thenReturn(Optional.empty());

        var res = assertThrows(FunkoNotFound.class, () -> funkoServiceImpl.update(funkoUpdateDto, id));

        assertEquals("Funko no encontrado", res.getMessage());
        verify(funkoRepository, times(1)).findById(id);
    }

    @Test
    void updateImage() throws IOException {
        String imageUrl = "rutaImagen4";

        MultipartFile multipartFile = mock(MultipartFile.class);

        when(funkoRepository.findById(funko1.getId())).thenReturn(Optional.of(funko1));
        when(storageService.store(multipartFile)).thenReturn(imageUrl);
        when(storageService.getUrl(imageUrl)).thenReturn(imageUrl);
        when(funkoRepository.save(any(Funko.class))).thenReturn(funko1);
        doNothing().when(webSocketHandlerMock).sendMessage(anyString());

        // Act
        Funko updatedFunko = funkoServiceImpl.updateImage(funko1.getId(), multipartFile);

        // Assert
        assertEquals(imageUrl, updatedFunko.getImagen());

        verify(funkoRepository, times(1)).save(any(Funko.class));
        verify(storageService, times(1)).delete(funko1.getImagen());
        verify(storageService, times(1)).store(multipartFile);
    }

    @Test
    void deleteById() throws IOException {
        long id = 1L;
        Funko expectedFunko = funko1;
        when(funkoRepository.findById(id)).thenReturn(Optional.of(expectedFunko));
        doNothing().when(webSocketHandlerMock).sendMessage(any());

        funkoServiceImpl.deleteById(id);

        verify(funkoRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteByIdNoExiste() {
        Long id = 1L;

        when(funkoRepository.findById(id)).thenReturn(Optional.empty());

        var res = assertThrows(FunkoNotFound.class, () -> funkoServiceImpl.findById(id));
        assertEquals("Funko no encontrado", res.getMessage());

        verify(funkoRepository, times(0)).deleteById(id);
    }

    @Test
    void onChange() throws IOException{
        doNothing().when(webSocketHandlerMock).sendMessage(any(String.class));
        funkoServiceImpl.onChange(Notification.Tipo.CREATE, funko1);
    }
}