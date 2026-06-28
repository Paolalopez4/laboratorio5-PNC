package com.grupo9.productoinventario.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoRequestDTO {
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Integer stock;
}