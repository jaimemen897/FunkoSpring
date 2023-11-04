package com.example.springfunko.websockets.notifications.mapper;

import com.example.springfunko.rest.funkos.models.Funko;
import com.example.springfunko.websockets.notifications.dto.FunkoNotificationResponse;
import org.springframework.stereotype.Component;

@Component
public class FunkoNotificationMapper {
    public FunkoNotificationResponse toFunkoNotificationDto(Funko funko) {
        return new FunkoNotificationResponse(
                funko.getId(),
                funko.getNombre(),
                funko.getPrecio(),
                funko.getCantidad(),
                funko.getImagen(),
                funko.getFechaCreacion(),
                funko.getFechaActualizacion(),
                funko.getCategoria()
        );
    }
}