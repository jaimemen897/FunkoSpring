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
import jakarta.persistence.criteria.Join;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

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
    public Page<FunkoResponseDto> findAll(Optional<String> nombre, Optional<String> categoria, Optional<Double> precioMax, Pageable pageable) {
        Specification<Funko> specNombre = (root, query, criteriaBuilder) ->
                nombre.map(value -> criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), "%" + value.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Funko> specCategoria = (root, query, criteriaBuilder) ->
                categoria.map(value -> {
                    Join<Funko, Categoria> categoriaJoin = root.join("categoria");
                    return criteriaBuilder.like(criteriaBuilder.lower(categoriaJoin.get("name")), "%" + value.toLowerCase() + "%");
                }).orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Funko> specPrecio = (root, query, criteriaBuilder) ->
                precioMax.map(value -> criteriaBuilder.lessThanOrEqualTo(root.get("precio"), value))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Funko> spec = Specification.where(specNombre).and(specCategoria).and(specPrecio);
        return funkoRepository.findAll(spec, pageable).map(funkoMapper::toFunkoResponseDto);
    }

    @Override
    @Cacheable(key = "#id")
    public FunkoResponseDto findById(long id) {
        return funkoMapper.toFunkoResponseDto(funkoRepository.findById(id).orElseThrow(() -> new FunkoNotFound("Funko no encontrado")));
    }

    private Categoria checkCategoria(String nameCategory) {
        var categoria = categoryRepository.findByNameContainingIgnoreCase(nameCategory);
        if (categoria.isEmpty() || categoria.get().getIsDeleted()) {
            throw new FunkoBadRequest("La categoria no existe");
        }
        return categoria.get();
    }

    @Override
    @CachePut(key = "#result.id")
    public FunkoResponseDto save(FunkoCreateDto funko) {
        var categoria = checkCategoria(funko.categoria());
        var funkoSaved = funkoRepository.save(funkoMapper.toFunko(funko, categoria));
        onChange(Notification.Tipo.CREATE, funkoSaved);
        return funkoMapper.toFunkoResponseDto(funkoSaved);
    }

    @Override
    @CachePut(key = "#result.id")
    @Transactional
    public FunkoResponseDto update(FunkoUpdateDto funko, Long id) {
        var funkoActual = funkoRepository.findById(id).orElseThrow(() -> new FunkoNotFound("Funko no encontrado"));
        Categoria categoria = null;

        if (funko.categoria() != null && !funko.categoria().isEmpty()) {
            categoria = checkCategoria(funko.categoria());
        } else {
            categoria = funkoActual.getCategoria();
        }

        var funkoUpdated = funkoRepository.save(funkoMapper.toFunko(funko, funkoActual, categoria));
        onChange(Notification.Tipo.UPDATE, funkoUpdated);
        return funkoMapper.toFunkoResponseDto(funkoUpdated);
    }

    @Override
    @CachePut(key = "#result.id")
    @Transactional
    public FunkoResponseDto updateImage(Long id, MultipartFile file) {
        if (!file.isEmpty()) {
            String imagen = storageService.store(file);
            String urlImagen = storageService.getUrl(imagen);

            Funko funko = funkoRepository.findById(id).orElseThrow(() -> new FunkoNotFound("Funko no encontrado"));
            storageService.delete(funko.getImagen());
            funko.setImagen(urlImagen);
            onChange(Notification.Tipo.UPDATE, funko);
            return funkoMapper.toFunkoResponseDto(funkoRepository.save(funko));
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se ha enviado la imagen");
        }
    }

    @Override
    @CacheEvict
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
