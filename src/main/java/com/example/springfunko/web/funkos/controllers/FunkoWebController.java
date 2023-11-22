package com.example.springfunko.web.funkos.controllers;

import com.example.springfunko.rest.category.models.Categoria;
import com.example.springfunko.rest.category.services.CategoryService;
import com.example.springfunko.rest.funkos.dto.FunkoCreateDto;
import com.example.springfunko.rest.funkos.dto.FunkoResponseDto;
import com.example.springfunko.rest.funkos.dto.FunkoUpdateDto;
import com.example.springfunko.rest.funkos.services.FunkoServiceImpl;
import com.example.springfunko.web.funkos.store.UserStore;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

@Controller
@RequestMapping(path = {"/funkos", ""})
@Slf4j
public class FunkoWebController {
    private static final String REDIRECT_FUNKOS = "redirect:/funkos";
    private static final String USER_NOT_LOGGED = "User not logged";
    private static final String LOGIN = "/login";
    private final FunkoServiceImpl funkoService;
    private final CategoryService categoryService;
    private final UserStore userSession;

    @Autowired
    public FunkoWebController(FunkoServiceImpl funkoService, CategoryService categoryService, UserStore userSession) {
        this.funkoService = funkoService;
        this.categoryService = categoryService;
        this.userSession = userSession;
    }

    @GetMapping("/login")
    public String login(HttpSession session) {
        log.info("Login GET");
        if (isLoggedAndSessionIsActive(session)) {
            log.info("User already logged");
            return REDIRECT_FUNKOS;
        }
        return "login";
    }

    @PostMapping
    public String login(@RequestParam("password") String password, HttpSession session, Model model) {
        log.info("Login POST");
        if ("pass".equals(password)) {
            userSession.setLastLogin(new Date());
            userSession.setLogged(true);
            session.setAttribute("userSession", userSession);
            session.setMaxInactiveInterval(1800);
            return REDIRECT_FUNKOS;
        } else {
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        log.info("Logout GET");
        session.invalidate();
        return REDIRECT_FUNKOS;
    }

    @GetMapping(path = {"", "/", "/index", "/list"})
    public String index(HttpSession session,
                        Model model,
                        @RequestParam(value = "search", required = false) Optional<String> search,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "5") int size,
                        @RequestParam(defaultValue = "id") String sortBy,
                        @RequestParam(defaultValue = "asc") String direction
    ) {
        if (!isLoggedAndSessionIsActive(session)) {
            log.info(USER_NOT_LOGGED);
            return REDIRECT_FUNKOS + LOGIN;
        }

        log.info("Index GET with params page: {}, size: {}, sortBy: {}, direction: {}", page, size, sortBy, direction);

        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        var funkoPage = funkoService.findAll(search, Optional.empty(), Optional.empty(), pageable);

        UserStore sessionData = (UserStore) session.getAttribute("userSession");
        sessionData.incrementLoginCount();
        var numVisits = sessionData.getLoginCount();
        var lastLogin = sessionData.getLastLogin();
        var localizedDate = getLocalizedDate(lastLogin, Locale.getDefault());

        model.addAttribute("search", search.orElse(""));
        model.addAttribute("funkosPage", funkoPage);
        model.addAttribute("numVisits", numVisits);
        model.addAttribute("lastLoginDate", localizedDate);

        return "index";
    }

    @GetMapping("/details/{id}")
    public String details(@PathVariable("id") Long id, Model model, HttpSession session) {
        if (!isLoggedAndSessionIsActive(session)) {
            log.info(USER_NOT_LOGGED);
            return REDIRECT_FUNKOS + LOGIN;
        }
        log.info("Details GET with id: {}", id);

        FunkoResponseDto funko = funkoService.findById(id);
        model.addAttribute("funko", funko);

        return "details";
    }

    @GetMapping("/create")
    public String createForm(Model model, HttpSession session) {
        if (!isLoggedAndSessionIsActive(session)) {
            log.info(USER_NOT_LOGGED);
            return REDIRECT_FUNKOS + LOGIN;
        }
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
        log.info("CREATE POST with res: {}", res);
        return REDIRECT_FUNKOS;
    }


    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable("id") Long id, Model model, HttpSession session) {
        if (!isLoggedAndSessionIsActive(session)) {
            log.info(USER_NOT_LOGGED);
            return REDIRECT_FUNKOS + LOGIN;
        }
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
        log.info("UPDATE POST with res: {}", res);
        return REDIRECT_FUNKOS;
    }

    @GetMapping("/update-image/{id}")
    public String updateImageForm(@PathVariable("id") Long funkoId, Model model, HttpSession session) {
        if (!isLoggedAndSessionIsActive(session)) {
            log.info(USER_NOT_LOGGED);
            return REDIRECT_FUNKOS + LOGIN;
        }
        log.info("UPDATE GET with funkoId: {}", funkoId);

        FunkoResponseDto funko = funkoService.findById(funkoId);
        model.addAttribute("funko", funko);

        return "update-image";
    }

    @PostMapping("/update-image/{id}")
    public String updateImageFunko(@PathVariable("id") Long funkoId, @RequestParam("imagen") MultipartFile imagen) {
        log.info("UPDATE POST with funkoId: {}", funkoId);

        funkoService.updateImage(funkoId, imagen);

        return REDIRECT_FUNKOS;
    }

    @GetMapping("/delete/{id}")
    public String deleteFunko(@PathVariable("id") Long id, HttpSession session) {
        if (!isLoggedAndSessionIsActive(session)) {
            log.info(USER_NOT_LOGGED);
            return REDIRECT_FUNKOS + LOGIN;
        }
        log.info("DELETE GET with id: {}", id);

        funkoService.deleteById(id);

        return REDIRECT_FUNKOS;
    }

    private String getLocalizedDate(Date date, Locale locale) {
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").withLocale(locale);
        return localDateTime.format(formatter);
    }

    private boolean isLoggedAndSessionIsActive(HttpSession session) {
        UserStore sessionData = (UserStore) session.getAttribute("scopedTarget.userSession");
        return sessionData != null && sessionData.isLogged();
    }


}