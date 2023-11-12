package com.example.springfunko.Funko.controller;


import com.example.springfunko.rest.category.models.Categoria;
import com.example.springfunko.rest.funkos.dto.FunkoCreateDto;
import com.example.springfunko.rest.funkos.dto.FunkoResponseDto;
import com.example.springfunko.rest.funkos.dto.FunkoUpdateDto;
import com.example.springfunko.rest.funkos.exception.FunkoNotFound;
import com.example.springfunko.rest.funkos.models.Funko;
import com.example.springfunko.rest.funkos.services.FunkoServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import com.example.springfunko.utils.pagination.PageResponse;

import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
    FunkoResponseDto funkoResponseDto = new FunkoResponseDto(1L, "nombre4", 70.89, 3, "rutaImagen4", categoria1, LocalDate.now(), LocalDate.now());
    FunkoResponseDto funkoResponseDto2 = new FunkoResponseDto(2L, "nombre5", 54.52, 1, "rutaImagen5", categoria2, LocalDate.now(), LocalDate.now());

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
        var funkolist = List.of(funkoResponseDto, funkoResponseDto2);
        var page = new PageImpl<>(funkolist);
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());

        Optional<String> name = Optional.empty();
        Optional<String> categoria = Optional.empty();
        Optional<Double> precioMax = Optional.empty();

        when(funkoService.findAll(name, categoria, precioMax, pageable)).thenReturn(page);

        MockHttpServletResponse response = mockMvc.perform(
                        get(myEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        PageResponse<FunkoResponseDto> res = mapper.readValue(response.getContentAsString(), new TypeReference<>(){});

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(2, res.content().size())
        );

        verify(funkoService, times(1)).findAll(name, categoria, precioMax, pageable);
    }

    @Test
    void getAllFunkosByCategoria() throws Exception {
        var funkolist = List.of(funkoResponseDto);
        var LOCAL_ENDPOINT = "/api/funkos?categoria=Disney";

        Optional<String> categoria = Optional.of("Disney");

        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(funkolist);

        when(funkoService.findAll(Optional.empty(), categoria, Optional.empty(), pageable)).thenReturn(page);

        MockHttpServletResponse response = mockMvc.perform(
                        get(LOCAL_ENDPOINT)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        PageResponse<FunkoResponseDto> res = mapper.readValue(response.getContentAsString(), new TypeReference<>(){
        });

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(1, res.content().size())
        );

        verify(funkoService, times(1)).findAll(Optional.empty(), categoria, Optional.empty(), pageable);
    }

    @Test
    void getAllFunkoByNombre() throws Exception {
        var LOCAL_ENDPOINT = "/api/funkos?nombre=nombre4";
        var funkolist = List.of(funkoResponseDto);
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(funkolist);

        Optional<String> name = Optional.of("nombre4");

        when(funkoService.findAll(name, Optional.empty(), Optional.empty(), pageable)).thenReturn(page);

        MockHttpServletResponse response = mockMvc.perform(
                        get(LOCAL_ENDPOINT)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        PageResponse<FunkoResponseDto> res = mapper.readValue(response.getContentAsString(), new TypeReference<>(){});

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(1, res.content().size())
        );

        verify(funkoService, times(1)).findAll(name, Optional.empty(), Optional.empty(), pageable);
    }

    @Test
    void getAllFunkoByMaxPrice() throws Exception {
        var LOCAL_ENDPOINT = "/api/funkos?precioMax=70.89";
        var funkolist = List.of(funkoResponseDto);
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(funkolist);

        Optional<String> name = Optional.empty();
        Optional<String> categoria = Optional.empty();
        Optional<Double> precioMax = Optional.of(70.89);

        when(funkoService.findAll(name, categoria, precioMax, pageable)).thenReturn(page);

        MockHttpServletResponse response = mockMvc.perform(
                        get(LOCAL_ENDPOINT)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        PageResponse<FunkoResponseDto> res = mapper.readValue(response.getContentAsString(), new TypeReference<>(){});

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(1, res.content().size())
        );

        verify(funkoService, times(1)).findAll(name, categoria, precioMax, pageable);
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

        when(funkoService.save(funkoDto)).thenReturn(funkoResponseDto);
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
        FunkoResponseDto funkoResponseDto = new FunkoResponseDto(1L, "nombre4", 70.89, 3, "rutaImagen4", categoria1, LocalDate.now(), LocalDate.now());


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
        when(funkoService.update(funkoDto, 1L)).thenReturn(funkoResponseDto);
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

        when(funkoService.update(funkoDto, 1L)).thenReturn(funkoResponseDto);

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
    void patchFunkoImage() throws Exception {
        var localEndPoint = myEndpoint + "/imagen/1";

        when(funkoService.updateImage(anyLong(), any(MultipartFile.class))).thenReturn(funko1);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "filename.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "Contenido del archivo".getBytes()
        );

        MockHttpServletResponse response = mockMvc.perform(
                        multipart(localEndPoint)
                                .file(file)
                                .with(req -> {
                                    req.setMethod("PATCH");
                                    return req;
                                })
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        Funko res = mapper.readValue(response.getContentAsString(), Funko.class);

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(funko1.getNombre(), res.getNombre()),
                () -> assertEquals(funko1.getPrecio(), res.getPrecio()),
                () -> assertEquals(funko1.getCantidad(), res.getCantidad()),
                () -> assertEquals(funko1.getImagen(), res.getImagen()),
                () -> assertEquals(funko1.getCategoria(), res.getCategoria())
        );

        verify(funkoService, times(1)).updateImage(anyLong(), any(MultipartFile.class));
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