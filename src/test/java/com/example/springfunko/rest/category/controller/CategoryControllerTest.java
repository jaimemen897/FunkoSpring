package com.example.springfunko.rest.category.controller;

import com.example.springfunko.rest.category.dto.CategoryResponseDto;
import com.example.springfunko.rest.category.exception.CategoryNotFound;
import com.example.springfunko.rest.category.models.Categoria;
import com.example.springfunko.rest.category.services.CategoryService;
import com.example.springfunko.utils.pagination.PageResponse;
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
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

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
@WithMockUser(username = "admin", password = "admin", roles = {"ADMIN", "USER"})
class CategoryControllerTest {
    private final String BASE_URL = "/api/categorias";
    private final Categoria categoria = Categoria.builder().name("Categoria 1").build();
    private final Categoria categoria2 = Categoria.builder().name("Categoria 2").build();
    private final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    MockMvc mockMvc;
    @MockBean
    private CategoryService categoryService;
    @Autowired
    private JacksonTester<Categoria> json;

    @Autowired
    public CategoryControllerTest(CategoryService categoryService) {
        this.categoryService = categoryService;
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    void getAll() throws Exception {
        var categoryList = List.of(categoria, categoria2);
        Page<Categoria> page = new PageImpl<>(categoryList);
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());

        when(categoryService.findAll(Optional.empty(), Optional.empty(), pageable)).thenReturn(page);

        MockHttpServletResponse response = mockMvc.perform(
                        get(BASE_URL)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        PageResponse<Categoria> res = mapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(2, res.content().size()),
                () -> assertEquals("Categoria 1", res.content().get(0).getName()),
                () -> assertEquals("Categoria 2", res.content().get(1).getName())

        );
        verify(categoryService, times(1)).findAll(Optional.empty(), Optional.empty(), pageable);
    }

    @Test
    void getAllByName() throws Exception {
        var LOCAL_URL = BASE_URL + "?name=Categoria 1";
        var categoryList = List.of(categoria);
        Optional<String> name = Optional.of("Categoria 1");
        Page<Categoria> page = new PageImpl<>(categoryList);
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());

        when(categoryService.findAll(name, Optional.empty(), pageable)).thenReturn(page);

        MockHttpServletResponse response = mockMvc.perform(
                        get(LOCAL_URL)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        PageResponse<Categoria> res = mapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(1, res.content().size()),
                () -> assertEquals("Categoria 1", res.content().get(0).getName())

        );
        verify(categoryService, times(1)).findAll(name, Optional.empty(), pageable);
    }

    @Test
    void getAllByIsDeleted() throws Exception {
        var LOCAL_URL = BASE_URL + "?isDeleted=false";
        var categoryList = List.of(categoria);
        Optional<Boolean> isDeleted = Optional.of(false);
        Page<Categoria> page = new PageImpl<>(categoryList);
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());

        when(categoryService.findAll(Optional.empty(), isDeleted, pageable)).thenReturn(page);

        MockHttpServletResponse response = mockMvc.perform(
                        get(LOCAL_URL)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        PageResponse<Categoria> res = mapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(1, res.content().size()),
                () -> assertEquals("Categoria 1", res.content().get(0).getName())

        );
        verify(categoryService, times(1)).findAll(Optional.empty(), isDeleted, pageable);
    }

    @Test
    void getById() throws Exception {
        var LOCAL_URL = BASE_URL + "/1";
        when(categoryService.findById(1L)).thenReturn(categoria);

        MockHttpServletResponse response = mockMvc.perform(
                        get(LOCAL_URL)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        Categoria actualCategory = mapper.readValue(response.getContentAsString(), Categoria.class);

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals("Categoria 1", actualCategory.getName()),
                () -> assertEquals(categoria.getName(), actualCategory.getName())
        );
        verify(categoryService, times(1)).findById(1L);
    }

    @Test
    void getByIdNotFound() throws Exception {
        var LOCAL_URL = BASE_URL + "/1";
        when(categoryService.findById(1L)).thenThrow(new CategoryNotFound("Categoria no encontrada"));

        MockHttpServletResponse response = mockMvc.perform(
                        get(LOCAL_URL)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(404, response.getStatus())
        );
        verify(categoryService, times(1)).findById(1L);
    }

    @Test
    void save() throws Exception {
        CategoryResponseDto categoryResponseDto = new CategoryResponseDto("Categoria 1", false);
        when(categoryService.save(categoryResponseDto)).thenReturn(categoria);

        MockHttpServletResponse response = mockMvc.perform(
                        post(BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(categoryResponseDto))
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        Categoria actualCategory = mapper.readValue(response.getContentAsString(), Categoria.class);

        assertAll(
                () -> assertEquals(201, response.getStatus()),
                () -> assertEquals("Categoria 1", actualCategory.getName()),
                () -> assertEquals(categoria.getName(), actualCategory.getName())
        );
        verify(categoryService, times(1)).save(categoryResponseDto);
    }

    @Test
    void saveBadRequest() throws Exception {
        CategoryResponseDto categoryResponseDto = new CategoryResponseDto("", false);
        when(categoryService.save(categoryResponseDto)).thenReturn(categoria);

        MockHttpServletResponse response = mockMvc.perform(
                        post(BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(categoryResponseDto))
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(400, response.getStatus())
        );
        verify(categoryService, times(0)).save(categoryResponseDto);
    }

    @Test
    void update() throws Exception {
        var LOCAL_URL = BASE_URL + "/1";
        CategoryResponseDto categoryResponseDto = new CategoryResponseDto("Categoria 1", false);
        when(categoryService.update(categoryResponseDto, 1L)).thenReturn(categoria);

        MockHttpServletResponse response = mockMvc.perform(
                        put(LOCAL_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(categoryResponseDto))
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        Categoria actualCategory = mapper.readValue(response.getContentAsString(), Categoria.class);

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals("Categoria 1", actualCategory.getName()),
                () -> assertEquals(categoria.getName(), actualCategory.getName())
        );
        verify(categoryService, times(1)).update(categoryResponseDto, 1L);
    }

    @Test
    void putBadRequest() throws Exception {
        var LOCAL_URL = BASE_URL + "/1";
        CategoryResponseDto categoryResponseDto = new CategoryResponseDto("", false);
        when(categoryService.update(categoryResponseDto, 1L)).thenReturn(categoria);

        MockHttpServletResponse response = mockMvc.perform(
                        put(LOCAL_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(categoryResponseDto))
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(400, response.getStatus())
        );
        verify(categoryService, times(0)).update(categoryResponseDto, 1L);
    }

    @Test
    void putNotFound() throws Exception {
        var LOCAL_URL = BASE_URL + "/1";
        CategoryResponseDto categoryResponseDto = new CategoryResponseDto("Categoria 1", false);
        when(categoryService.update(categoryResponseDto, 1L)).thenThrow(new CategoryNotFound("Categoria no encontrada"));

        MockHttpServletResponse response = mockMvc.perform(
                        put(LOCAL_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(categoryResponseDto))
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(404, response.getStatus())
        );
        verify(categoryService, times(1)).update(categoryResponseDto, 1L);
    }

    @Test
    void deleteCategory() throws Exception {
        var LOCAL_URL = BASE_URL + "/1";
        doNothing().when(categoryService).deleteById(1L);
        MockHttpServletResponse response = mockMvc.perform(
                        delete(LOCAL_URL)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(204, response.getStatus())
        );
        verify(categoryService, times(1)).deleteById(1L);
    }

    @Test
    void deleteCategoryNotFound() throws Exception {
        var LOCAL_URL = BASE_URL + "/1";
        doThrow(new CategoryNotFound("Categoria no encontrada")).when(categoryService).deleteById(1L);
        MockHttpServletResponse response = mockMvc.perform(
                        delete(LOCAL_URL)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(404, response.getStatus())
        );
        verify(categoryService, times(1)).deleteById(1L);
    }
}