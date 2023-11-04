package com.example.springfunko.rest.funkos.services;

import com.example.springfunko.config.websockets.WebSocketConfig;
import com.example.springfunko.config.websockets.WebSocketHandler;
import com.example.springfunko.rest.category.models.Categoria;
import com.example.springfunko.rest.category.repositories.CategoryRepository;
import com.example.springfunko.rest.funkos.dto.FunkoCreateDto;
import com.example.springfunko.rest.funkos.dto.FunkoResponseDto;
import com.example.springfunko.rest.funkos.dto.FunkoUpdateDto;
import com.example.springfunko.rest.funkos.exception.FunkoBadRequest;
import com.example.springfunko.rest.funkos.exception.FunkoNotFound;
import com.example.springfunko.rest.funkos.mapper.FunkoMapper;
import com.example.springfunko.rest.funkos.models.Funko;
import com.example.springfunko.rest.funkos.repositories.FunkoRepository;
import com.example.springfunko.rest.storage.services.StorageService;
import com.example.springfunko.websockets.notifications.dto.FunkoNotificationResponse;
import com.example.springfunko.websockets.notifications.mapper.FunkoNotificationMapper;
import com.example.springfunko.websockets.notifications.models.Notification;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@CacheConfig(cacheNames = {"funkos"})
@Service
@Slf4j
public class FunkoServiceImpl implements FunkoService {

    private final FunkoRepository funkoRepository;
    private final CategoryRepository categoryRepository;
    private final FunkoMapper funkoMapper;
    private final StorageService storageService;

    private final WebSocketConfig webSocketConfig;
    private final ObjectMapper mapper;
    private final FunkoNotificationMapper funkoNotificationMapper;
    private WebSocketHandler webSocketService;

    @Autowired
    public FunkoServiceImpl(FunkoRepository funkoRepository, StorageService storageService, CategoryRepository categoryRepository, FunkoMapper funkoMapper, WebSocketConfig webSocketConfig, FunkoNotificationMapper funkoNotificationMapper) {
        this.funkoRepository = funkoRepository;
        this.categoryRepository = categoryRepository;
        this.funkoMapper = funkoMapper;
        this.storageService = storageService;
        this.webSocketConfig = webSocketConfig;
        webSocketService = webSocketConfig.webSocketFunkosHandler();
        this.mapper = new ObjectMapper();
        this.funkoNotificationMapper = funkoNotificationMapper;
        mapper.registerModule(new JavaTimeModule());
    }

    @Override
    public List<Funko> findAll(String nombre, String categoria) {
        if ((nombre == null || nombre.isEmpty()) && (categoria == null || categoria.isEmpty())) {
            return funkoRepository.findAll();
        }
        if ((nombre != null && !nombre.isEmpty()) && (categoria == null || categoria.isEmpty())) {
            return funkoRepository.findAllByNombre(nombre);
        }
        if (nombre == null || nombre.isEmpty()) {
            return funkoRepository.findAllByCategoriaName(categoria);
        }
        return funkoRepository.findAllByNombreAndCategoriaName(nombre, categoria);
    }

    @Override
    @Cacheable
    public Funko findById(long id) {
        return funkoRepository.findById(id).orElseThrow(
                () -> new FunkoNotFound("Funko no encontrado"));
    }

    private Categoria checkCategoria(String nameCategory) {
        var categoria = categoryRepository.findByNameContainingIgnoreCase(nameCategory);
        if (categoria.isEmpty() || categoria.get().getIsDeleted()) {
            throw new FunkoBadRequest("La categoria no existe");
        }
        return categoria.get();
    }

    @Override
    @Cacheable
    public FunkoResponseDto save(FunkoCreateDto funko) {
        var categoria = checkCategoria(funko.categoria().getName());
        var funkoSaved = funkoRepository.save(funkoMapper.toFunko(funko, categoria));
        onChange(Notification.Tipo.CREATE, funkoSaved);
        return funkoMapper.toFunkoResponseDto(funkoSaved);
    }

    @Override
    @Cacheable
    @Transactional
    public FunkoResponseDto update(FunkoUpdateDto funko, Long id) {
        var funkoActual = funkoRepository.findById(id).orElseThrow(() -> new FunkoNotFound("Funko no encontrado"));
        Categoria categoria = null;

        if (funko.categoria() != null && !funko.categoria().getName().isEmpty()) {
            categoria = checkCategoria(funko.categoria().getName());
        } else {
            categoria = funkoActual.getCategoria();
        }

        var funkoUpdated = funkoRepository.save(funkoMapper.toFunko(funko, funkoActual, categoria));
        onChange(Notification.Tipo.UPDATE, funkoUpdated);
        return funkoMapper.toFunkoResponseDto(funkoUpdated);
    }

    @CachePut(key = "#id")
    public Funko updateImage(Long id, MultipartFile file) {
        if (!file.isEmpty()) {
            String imagen = storageService.store(file);
            String urlImagen = storageService.getUrl(imagen);

            Funko funko = findById(id);
            storageService.delete(funko.getImagen());
            funko.setImagen(urlImagen);
            onChange(Notification.Tipo.UPDATE, funko);
            return funkoRepository.save(funko);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se ha enviado la imagen");
        }
    }

    @Override
    @Cacheable
    @Transactional
    public void deleteById(long id) {
        var funk = funkoRepository.findById(id).orElseThrow(() -> new FunkoNotFound("Funko no encontrado"));
        funkoRepository.deleteById(id);
        if (funk.getImagen() != null && !funk.getImagen().equals(Funko.IMAGE_DEFAULT)) {
            storageService.delete(funk.getImagen());
        }
        onChange(Notification.Tipo.DELETE, funk);
    }

    public void onChange(Notification.Tipo tipo, Funko data) {
        log.info("Servicio de funkos onChange con tipo: {} y datos: {}", tipo, data);

        if (webSocketService == null) {
            log.warn("No se ha podido enviar la notificación a los clientes ws, no se ha encontrado el servicio");
            webSocketService = this.webSocketConfig.webSocketFunkosHandler();
        }

        try {
            Notification<FunkoNotificationResponse> notificacion = new Notification<>(
                    "FUNKOS",
                    tipo,
                    funkoNotificationMapper.toFunkoNotificationDto(data),
                    LocalDateTime.now().toString()
            );

            String json = mapper.writeValueAsString((notificacion));

            log.info("Enviando mensaje a los clientes ws");

            Thread senderThread = new Thread(() -> {
                try {
                    webSocketService.sendMessage(json);
                } catch (Exception e) {
                    log.error("Error al enviar el mensaje a través del servicio WebSocket", e);
                }
            });
            senderThread.start();
        } catch (JsonProcessingException e) {
            log.error("Error al convertir la notificación a JSON", e);
        }
    }

    public void setWebSocketService(WebSocketHandler webSocketHandlerMock) {
        this.webSocketService = webSocketHandlerMock;
    }
}
