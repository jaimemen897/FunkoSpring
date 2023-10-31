package com.example.springfunko.controller;

import com.example.springfunko.category.models.Categoria;
import com.example.springfunko.funkos.dto.FunkoCreateDto;
import com.example.springfunko.funkos.dto.FunkoResponseDto;
import com.example.springfunko.funkos.dto.FunkoUpdateDto;
import com.example.springfunko.funkos.exception.FunkoNotFound;
import com.example.springfunko.funkos.models.Funko;
import com.example.springfunko.funkos.services.FunkoServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@ExtendWith(MockitoExtension.class)
class FunkoRestControllerTest {
    private final ObjectMapper mapper = new ObjectMapper();
    private final String myEndpoint = "/api/funkos";
    private final Categoria categoria1 = Categoria.builder().id(null).name("Disney").build();
    private final Categoria categoria2 = Categoria.builder().id(null).name("Serie").build();

    private final Funko funko1 = Funko.builder()
            .id(1L)
            .nombre("nombre4")
            .precio(70.89)
            .cantidad(3)
            .categoria(categoria1)
            .imagen("rutaImagen4")
            .build();
    private final Funko funko2 = Funko.builder()
            .id(2L)
            .nombre("nombre5")
            .precio(54.52)
            .cantidad(1)
            .categoria(categoria2)
            .imagen("rutaImagen5")
            .build();

    @Autowired
    MockMvc mockMvc;
    @MockBean
    private FunkoServiceImpl funkoService;
    @Autowired
    private JacksonTester<Funko> jsonFunko;

    @Autowired
    public FunkoRestControllerTest(FunkoServiceImpl funkoService) {
        this.funkoService = funkoService;
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    void getAllFunkos() throws Exception {
        var funkolist = List.of(funko1, funko2);

        when(funkoService.findAll(null, null)).thenReturn(funkolist);

        MockHttpServletResponse response = mockMvc.perform(
                        get(myEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        List<FunkoResponseDto> res = mapper.readValue(response.getContentAsString(),
                mapper.getTypeFactory().constructCollectionType(List.class, FunkoResponseDto.class));
        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(2, funkolist.size()),
                () -> assertEquals(funkolist.get(0).getNombre(), res.get(0).nombre()),
                () -> assertEquals(funkolist.get(0).getPrecio(), res.get(0).precio()),
                () -> assertEquals(funkolist.get(0).getCantidad(), res.get(0).cantidad()),
                () -> assertEquals(funkolist.get(0).getImagen(), res.get(0).imagen()),
                () -> assertEquals(funkolist.get(0).getCategoria(), res.get(0).categoria()),
                () -> assertEquals(funkolist.get(1).getNombre(), res.get(1).nombre()),
                () -> assertEquals(funkolist.get(1).getPrecio(), res.get(1).precio()),
                () -> assertEquals(funkolist.get(1).getCantidad(), res.get(1).cantidad()),
                () -> assertEquals(funkolist.get(1).getImagen(), res.get(1).imagen()),
                () -> assertEquals(funkolist.get(1).getCategoria(), res.get(1).categoria())
        );

        verify(funkoService, times(1)).findAll(null, null);
    }

    @Test
    void getAllProductosByCategoria() throws Exception {
        var funkoList = List.of(funko2);
        var localEndPoint = myEndpoint + "?categoria=disney";
        when(funkoService.findAll(null, "disney")).thenReturn(funkoList);
        MockHttpServletResponse response = mockMvc.perform(
                        get(localEndPoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        List<FunkoResponseDto> res = mapper.readValue(response.getContentAsString(),
                mapper.getTypeFactory().constructCollectionType(List.class, FunkoResponseDto.class));

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(1, funkoList.size()),
                () -> assertEquals(funkoList.get(0).getNombre(), res.get(0).nombre()),
                () -> assertEquals(funkoList.get(0).getPrecio(), res.get(0).precio()),
                () -> assertEquals(funkoList.get(0).getCantidad(), res.get(0).cantidad()),
                () -> assertEquals(funkoList.get(0).getImagen(), res.get(0).imagen()),
                () -> assertEquals(funkoList.get(0).getCategoria(), res.get(0).categoria())
        );
    }

    @Test
    void getAllFunkoByNombre() throws Exception {
        var funkoList = List.of(funko1);
        var localEndPoint = myEndpoint + "?nombre=nombre4";
        when(funkoService.findAll("nombre4", null)).thenReturn(funkoList);
        MockHttpServletResponse response = mockMvc.perform(
                        get(localEndPoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        List<FunkoResponseDto> res = mapper.readValue(response.getContentAsString(),
                mapper.getTypeFactory().constructCollectionType(List.class, FunkoResponseDto.class));
        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(1, funkoList.size()),
                () -> assertEquals(funkoList.get(0).getNombre(), res.get(0).nombre()),
                () -> assertEquals(funkoList.get(0).getPrecio(), res.get(0).precio()),
                () -> assertEquals(funkoList.get(0).getCantidad(), res.get(0).cantidad()),
                () -> assertEquals(funkoList.get(0).getImagen(), res.get(0).imagen()),
                () -> assertEquals(funkoList.get(0).getCategoria(), res.get(0).categoria())
        );
    }

    @Test
    void getFunkoById() throws Exception {
        var localEndPoint = myEndpoint + "/1";
        when(funkoService.findById(1L)).thenReturn(funko1);
        MockHttpServletResponse response = mockMvc.perform(
                        get(localEndPoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        FunkoResponseDto res = mapper.readValue(response.getContentAsString(), FunkoResponseDto.class);
        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(funko1.getNombre(), res.nombre()),
                () -> assertEquals(funko1.getPrecio(), res.precio()),
                () -> assertEquals(funko1.getCantidad(), res.cantidad()),
                () -> assertEquals(funko1.getImagen(), res.imagen()),
                () -> assertEquals(funko1.getCategoria(), res.categoria())
        );
    }

    @Test
    void getFunkosByIdNoExiste() throws Exception {
        var localEndPoint = myEndpoint + "/1";
        when(funkoService.findById(1L)).thenThrow(new FunkoNotFound("Funko no encontrado"));
        MockHttpServletResponse response = mockMvc.perform(
                        get(localEndPoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertEquals(404, response.getStatus());
        verify(funkoService, times(1)).findById(1L);
    }

    @Test
    void postFunko() throws Exception {
        var funkoDto = new FunkoCreateDto("nombre4", 70.89, 3, "rutaImagen4", categoria1);
        when(funkoService.save(funkoDto)).thenReturn(funko1);
        MockHttpServletResponse response = mockMvc.perform(
                        post(myEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonFunko.write(funko1).getJson()))
                .andReturn().getResponse();
        FunkoResponseDto res = mapper.readValue(response.getContentAsString(), FunkoResponseDto.class);
        assertAll(
                () -> assertEquals(201, response.getStatus()),
                () -> assertEquals(funko1.getNombre(), res.nombre()),
                () -> assertEquals(funko1.getPrecio(), res.precio()),
                () -> assertEquals(funko1.getCantidad(), res.cantidad()),
                () -> assertEquals(funko1.getImagen(), res.imagen()),
                () -> assertEquals(funko1.getCategoria(), res.categoria())
        );
    }

    @Test
    void postFunkoNotValidName() throws Exception {
        var funkoDto = new FunkoCreateDto("", 70.89, 3, "rutaImagen4", new Categoria());
        when(funkoService.save(funkoDto)).thenReturn(funko1);
        MockHttpServletResponse response = mockMvc.perform(
                        post(myEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(funkoDto))
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();


        assertAll(
                () -> assertEquals(400, response.getStatus())
        );
    }

    @Test
    void postFunkoNotValidPrice() throws Exception {
        var funkoDto = new FunkoCreateDto("nombre4", -70.89, 3, "rutaImagen4", categoria1);
        when(funkoService.save(funkoDto)).thenReturn(funko1);
        MockHttpServletResponse response = mockMvc.perform(
                        post(myEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(funkoDto))
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();


        assertAll(
                () -> assertEquals(400, response.getStatus())
        );
    }

    @Test
    void postFunkoNotValidCantidad() throws Exception {
        var funkoDto = new FunkoCreateDto("nombre4", 70.89, -3, "rutaImagen4", categoria1);
        when(funkoService.save(funkoDto)).thenReturn(funko1);
        MockHttpServletResponse response = mockMvc.perform(
                        post(myEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(funkoDto))
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();


        assertAll(
                () -> assertEquals(400, response.getStatus())
        );
    }

    @Test
    void postFunkoNotValidImagen() throws Exception {
        var funkoDto = new FunkoCreateDto("nombre4", 70.89, 3, null, categoria1);
        when(funkoService.save(funkoDto)).thenReturn(funko1);
        MockHttpServletResponse response = mockMvc.perform(
                        post(myEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(funkoDto))
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();


        assertAll(
                () -> assertEquals(400, response.getStatus())
        );
    }

    @Test
    void postFunkoNotValidCategoria() throws Exception {
        var funkoDto = new FunkoCreateDto("nombre4", 70.89, 3, "rutaImagen4", null);
        when(funkoService.save(funkoDto)).thenReturn(funko1);
        MockHttpServletResponse response = mockMvc.perform(
                        post(myEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(funkoDto))
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();


        assertAll(
                () -> assertEquals(400, response.getStatus())
        );
    }


    @Test
    void putFunko() throws Exception {
        var localEndPoint = myEndpoint + "/1";
        var funkoDto = new FunkoUpdateDto("nombre4", 70.89, 3, "rutaImagen4", categoria1);
        when(funkoService.update(funkoDto, 1L)).thenReturn(funko1);
        MockHttpServletResponse response = mockMvc.perform(
                        put(localEndPoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonFunko.write(funko1).getJson())
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        FunkoResponseDto res = mapper.readValue(response.getContentAsString(), FunkoResponseDto.class);
        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(funko1.getNombre(), res.nombre()),
                () -> assertEquals(funko1.getPrecio(), res.precio()),
                () -> assertEquals(funko1.getCantidad(), res.cantidad()),
                () -> assertEquals(funko1.getImagen(), res.imagen()),
                () -> assertEquals(funko1.getCategoria(), res.categoria())
        );
    }

    @Test
    void putFunkoNotFound() {
        var localEndPoint = myEndpoint + "/1";
        var funkoDto = new FunkoUpdateDto("nombre4", 70.89, 3, "rutaImagen4", categoria1);
        when(funkoService.update(funkoDto, 1L)).thenThrow(new FunkoNotFound("Funko no encontrado"));
        assertAll(
                () -> assertEquals(404, mockMvc.perform(
                                put(localEndPoint)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(jsonFunko.write(funko1).getJson())
                                        .accept(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse().getStatus())
        );
    }

    @Test
    void patchFunko() throws Exception {
        var localEndPoint = myEndpoint + "/1";
        var funkoDto = new FunkoUpdateDto("nombre4", 70.89, 3, "rutaImagen4", categoria1);
        when(funkoService.update(funkoDto, 1L)).thenReturn(funko1);
        MockHttpServletResponse response = mockMvc.perform(
                        patch(localEndPoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonFunko.write(funko1).getJson())
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        FunkoResponseDto res = mapper.readValue(response.getContentAsString(), FunkoResponseDto.class);
        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(funko1.getNombre(), res.nombre()),
                () -> assertEquals(funko1.getPrecio(), res.precio()),
                () -> assertEquals(funko1.getCantidad(), res.cantidad()),
                () -> assertEquals(funko1.getImagen(), res.imagen()),
                () -> assertEquals(funko1.getCategoria(), res.categoria())
        );
        verify(funkoService, times(1)).update(funkoDto, 1L);
    }

    @Test
    void patchFunkoNotFound() {
        var localEndPoint = myEndpoint + "/1";
        var funkoDto = new FunkoUpdateDto("nombre4", 70.89, 3, "rutaImagen4", categoria1);
        when(funkoService.update(funkoDto, 1L)).thenThrow(new FunkoNotFound("Funko no encontrado"));
        assertAll(
                () -> assertEquals(404, mockMvc.perform(
                                patch(localEndPoint)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(jsonFunko.write(funko1).getJson())
                                        .accept(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse().getStatus())
        );
    }


    @Test
    void deleteFunko() throws Exception {
        var localEndPoint = myEndpoint + "/1";
        doNothing().when(funkoService).deleteById(1L);
        MockHttpServletResponse response = mockMvc.perform(
                        delete(localEndPoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertAll(
                () -> assertEquals(204, response.getStatus())
        );
        verify(funkoService, times(1)).deleteById(1L);
    }

    @Test
    void deleteFunkoNotFound() throws Exception {
        var localEndPoint = myEndpoint + "/1";
        doThrow(new FunkoNotFound("Funko no encontrado")).when(funkoService).deleteById(1L);
        MockHttpServletResponse response = mockMvc.perform(
                        delete(localEndPoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertAll(
                () -> assertEquals(404, response.getStatus())
        );
        verify(funkoService, times(1)).deleteById(1L);
    }
}