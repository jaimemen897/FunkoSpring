package com.example.springfunko.web.funkos.controllers;

import com.example.springfunko.rest.category.models.Categoria;
import com.example.springfunko.rest.category.services.CategoryService;
import com.example.springfunko.rest.funkos.dto.FunkoCreateDto;
import com.example.springfunko.rest.funkos.dto.FunkoResponseDto;
import com.example.springfunko.rest.funkos.dto.FunkoUpdateDto;
import com.example.springfunko.rest.funkos.services.FunkoServiceImpl;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

@Controller
//"funkos" and ""
@RequestMapping(path = {"/funkos", ""})
@Slf4j
public class FunkoWebController {
    private final FunkoServiceImpl funkoService;
    private final CategoryService categoryService;
    private final MessageSource messageSource;
    //user store

    @Autowired
    public FunkoWebController(FunkoServiceImpl funkoService, CategoryService categoryService, MessageSource messageSource) {
        this.funkoService = funkoService;
        this.categoryService = categoryService;
        this.messageSource = messageSource;
    }

    @GetMapping(path = {"", "/", "/index", "/list"})
    public String index(HttpSession session,
                        Model model,
                        @RequestParam(value = "search", required = false) Optional<String> search,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @RequestParam(defaultValue = "id") String sortBy,
                        @RequestParam(defaultValue = "asc") String direction,
                        Locale locale
    ) {
        log.info("Index GET with params page: {}, size: {}, sortBy: {}, direction: {}", page, size, sortBy, direction);

        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        System.out.println(funkoService.findAll(Optional.empty(), Optional.empty(), Optional.empty(), pageable).getContent());
        var funkoPage = funkoService.findAll(search, Optional.empty(), Optional.empty(), pageable);

        model.addAttribute("search", search.orElse(""));
        model.addAttribute("funkosPage", funkoPage);

        return "index";
    }

    @GetMapping("/details/{id}")
    public String details(@PathVariable("id") Long id, Model model, HttpSession session) {
        log.info("Details GET with id: {}", id);

        FunkoResponseDto funko = funkoService.findById(id);
        model.addAttribute("funko", funko);

        return "details";
    }

    @GetMapping("/create")
    public String createForm(Model model, HttpSession session) {
        log.info("CREATE GET");

        Stream<String> categorias = categoryService.findAll(Optional.empty(), Optional.empty(), PageRequest.of(0, 1000)).get().map(Categoria::getName);
        var funko = FunkoCreateDto.builder().build();
        model.addAttribute("funko", funko);
        model.addAttribute("categorias", categorias);
        return "create";
    }

    @PostMapping("/create")
    public String createFunko(@Valid @ModelAttribute("funko") FunkoCreateDto funkoCreateDto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            var categorias = categoryService.findAll(Optional.empty(), Optional.empty(), PageRequest.of(0, 1000)).get().map(Categoria::getName);
            model.addAttribute("categorias", categorias);
            return "create";
        }
        log.info("CREATE POST");

        var res = funkoService.save(funkoCreateDto);
        System.out.println(res);
        return "redirect:/funkos";
    }



    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable("id") Long id, Model model, HttpSession session) {
        log.info("UPDATE GET with id: {}", id);

        Stream<String> categorias = categoryService.findAll(Optional.empty(), Optional.empty(), PageRequest.of(0, 1000)).get().map(Categoria::getName);
        FunkoResponseDto funko = funkoService.findById(id);
        FunkoUpdateDto funkoUpdateDto = new FunkoUpdateDto(funko.nombre(), funko.precio(), funko.cantidad(), funko.imagen(), funko.categoria().getName());
        model.addAttribute("funko", funkoUpdateDto);
        model.addAttribute("categorias", categorias);
        return "update";
    }

    @PostMapping("/update/{id}")
    public String updateFunko(@PathVariable("id") Long id, @Valid @ModelAttribute("funko") FunkoUpdateDto funkoUpdateDto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            var categorias = categoryService.findAll(Optional.empty(), Optional.empty(), PageRequest.of(0, 1000)).get().map(Categoria::getName);
            model.addAttribute("categorias", categorias);
            return "update";
        }
        log.info("UPDATE POST with id: {}", id);

        var res = funkoService.update(funkoUpdateDto, id);
        System.out.println(res);
        return "redirect:/funkos";
    }

    @GetMapping("/update-image/{id}")
    public String updateImageForm(@PathVariable("id") Long funkoId, Model model, HttpSession session) {
        log.info("UPDATE GET with funkoId: {}", funkoId);

        FunkoResponseDto funko = funkoService.findById(funkoId);
        model.addAttribute("funko", funko);

        return "update-image";
    }

    @PostMapping("/update-image/{id}")
    public String updateImageFunko(@PathVariable("id") Long funkoId, @RequestParam("imagen") MultipartFile imagen) {
        log.info("UPDATE POST with funkoId: {}", funkoId);

        funkoService.updateImage(funkoId, imagen);

        return "redirect:/funkos";
    }

    @GetMapping("/delete/{id}")
    public String deleteFunko(@PathVariable("id") Long id, HttpSession session) {
        log.info("DELETE GET with id: {}", id);

        funkoService.deleteById(id);

        return "redirect:/funkos";
    }
}
