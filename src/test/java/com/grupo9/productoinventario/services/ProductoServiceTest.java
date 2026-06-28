package com.grupo9.productoinventario.services;

import com.grupo9.productoinventario.dto.ProductoRequestDTO;
import com.grupo9.productoinventario.dto.ProductoResponseDTO;
import com.grupo9.productoinventario.entity.Producto;
import com.grupo9.productoinventario.mapper.ProductoMapper;
import com.grupo9.productoinventario.repository.ProductoRepository;
import com.grupo9.productoinventario.service.ProductoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private ProductoMapper productoMapper;

    @InjectMocks
    private ProductoService productoService;

    private Long productoId;
    private ProductoRequestDTO request;
    private ProductoResponseDTO productoResponse;
    private Producto productoEntity;

    @BeforeEach
    void setUp() {
        productoId = 1L;

        request = ProductoRequestDTO.builder()
                .nombre("Laptop Dell XPS")
                .descripcion("Laptop profesional 15 pulgadas")
                .precio(new BigDecimal("1299.99"))
                .stock(25)
                .build();

        productoEntity = Producto.builder()
                .id(productoId)
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .precio(request.getPrecio())
                .stock(request.getStock())
                .fechaCreacion(LocalDateTime.now())
                .build();

        productoResponse = ProductoResponseDTO.builder()
                .id(productoId)
                .nombre(productoEntity.getNombre())
                .descripcion(productoEntity.getDescripcion())
                .precio(productoEntity.getPrecio())
                .stock(productoEntity.getStock())
                .fechaCreacion(productoEntity.getFechaCreacion())
                .build();
    }

    @Test
    void crear_shouldSaveProductoWithCorrectData() {
        when(productoMapper.toEntity(request)).thenReturn(productoEntity);
        when(productoRepository.save(productoEntity)).thenReturn(productoEntity);
        when(productoMapper.toDTO(productoEntity)).thenReturn(productoResponse);

        var result = productoService.crear(request);

        assertThat(result).isEqualTo(productoResponse);
    }

    @Test
    void obtenerPorId_shouldReturnProducto_whenProductoExists() {
        when(productoRepository.findById(productoId)).thenReturn(Optional.of(productoEntity));
        when(productoMapper.toDTO(productoEntity)).thenReturn(productoResponse);

        ProductoResponseDTO result = productoService.obtenerPorId(productoId);

        assertThat(result).isEqualTo(productoResponse);
    }

    @Test
    void obtenerPorId_shouldThrowException_whenProductoNotFound() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> productoService.obtenerPorId(99L));

        assertTrue(exception.getMessage().contains("99"));
        verify(productoMapper, never()).toDTO(any());
    }

    @Test
    void obtenerTodos_shouldReturnListOfProductos() {
        when(productoRepository.findAll()).thenReturn(Arrays.asList(productoEntity));
        when(productoMapper.toDTO(productoEntity)).thenReturn(productoResponse);

        List<ProductoResponseDTO> result = productoService.obtenerTodos();

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(productoResponse);
        verify(productoRepository, times(1)).findAll();
    }

    @Test
    void obtenerTodos_shouldReturnEmptyList_whenNoProductos() {
        when(productoRepository.findAll()).thenReturn(Collections.emptyList());

        List<ProductoResponseDTO> result = productoService.obtenerTodos();

        assertThat(result).isEmpty();
        verify(productoMapper, never()).toDTO(any());
    }

    @Test
    void actualizar_shouldUpdateProducto_whenProductoExists() {
        ProductoRequestDTO requestActualizado = ProductoRequestDTO.builder()
                .nombre("Laptop Dell XPS 17")
                .descripcion("Versión actualizada")
                .precio(new BigDecimal("1599.99"))
                .stock(10)
                .build();

        ProductoResponseDTO responseActualizado = ProductoResponseDTO.builder()
                .id(productoId)
                .nombre("Laptop Dell XPS 17")
                .precio(new BigDecimal("1599.99"))
                .stock(10)
                .build();

        when(productoRepository.findById(productoId)).thenReturn(Optional.of(productoEntity));
        when(productoRepository.save(productoEntity)).thenReturn(productoEntity);
        when(productoMapper.toDTO(productoEntity)).thenReturn(responseActualizado);

        ProductoResponseDTO result = productoService.actualizar(productoId, requestActualizado);

        assertThat(result.getNombre()).isEqualTo("Laptop Dell XPS 17");
        verify(productoRepository, times(1)).save(productoEntity);
    }

    @Test
    void actualizar_shouldThrowException_whenProductoNotFound() {
        when(productoRepository.findById(50L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> productoService.actualizar(50L, request));

        verify(productoRepository, never()).save(any());
    }

    @Test
    void eliminar_shouldDeleteProducto_whenProductoExists() {
        when(productoRepository.existsById(productoId)).thenReturn(true);
        doNothing().when(productoRepository).deleteById(productoId);

        productoService.eliminar(productoId);

        verify(productoRepository, times(1)).existsById(productoId);
        verify(productoRepository, times(1)).deleteById(productoId);
    }

    @Test
    void eliminar_shouldThrowException_whenProductoNotFound() {
        when(productoRepository.existsById(99L)).thenReturn(false);

        assertThrows(RuntimeException.class,
                () -> productoService.eliminar(99L));

        verify(productoRepository, never()).deleteById(any());
    }

    @Test
    void buscarPorNombre_shouldReturnMatchingProductos() {
        when(productoRepository.findByNombreContainingIgnoreCase("laptop"))
                .thenReturn(Arrays.asList(productoEntity));
        when(productoMapper.toDTO(productoEntity)).thenReturn(productoResponse);

        List<ProductoResponseDTO> result = productoService.buscarPorNombre("laptop");

        assertThat(result).hasSize(1);
        verify(productoRepository, times(1))
                .findByNombreContainingIgnoreCase("laptop");
    }

    @Test
    void obtenerConStockBajo_shouldReturnProductosBelowThreshold() {
        Producto productoStockBajo = Producto.builder()
                .id(2L).nombre("Mouse").precio(new BigDecimal("29.99")).stock(3).build();

        ProductoResponseDTO responseStockBajo = ProductoResponseDTO.builder()
                .id(2L).nombre("Mouse").stock(3).build();

        when(productoRepository.findByStockLessThan(10))
                .thenReturn(Arrays.asList(productoStockBajo));
        when(productoMapper.toDTO(productoStockBajo)).thenReturn(responseStockBajo);

        List<ProductoResponseDTO> result = productoService.obtenerConStockBajo(10);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStock()).isLessThan(10);
        verify(productoRepository, times(1)).findByStockLessThan(10);
    }
}