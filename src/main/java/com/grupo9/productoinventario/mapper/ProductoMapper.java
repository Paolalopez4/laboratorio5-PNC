package com.grupo9.productoinventario.mapper;

import com.grupo9.productoinventario.dto.ProductoRequestDTO;
import com.grupo9.productoinventario.dto.ProductoResponseDTO;
import com.grupo9.productoinventario.entity.Producto;
import org.springframework.stereotype.Component;

@Component
public class ProductoMapper {
    public Producto toEntity(ProductoRequestDTO dto) {
        return Producto.builder()
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .precio(dto.getPrecio())
                .stock(dto.getStock())
                .build();
    }

    public ProductoResponseDTO toDTO(Producto producto) {
        return ProductoResponseDTO.builder()
                .id(producto.getId())
                .nombre(producto.getNombre())
                .descripcion(producto.getDescripcion())
                .precio(producto.getPrecio())
                .stock(producto.getStock())
                .fechaCreacion(producto.getFechaCreacion())
                .build();
    }
}