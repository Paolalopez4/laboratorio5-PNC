package com.grupo9.productoinventario.service;

import com.grupo9.productoinventario.dto.ProductoRequestDTO;
import com.grupo9.productoinventario.dto.ProductoResponseDTO;
import com.grupo9.productoinventario.entity.Producto;
import com.grupo9.productoinventario.mapper.ProductoMapper;
import com.grupo9.productoinventario.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoService {
    private final ProductoRepository productoRepository;
    private final ProductoMapper productoMapper;

    public List<ProductoResponseDTO> obtenerTodos() {
        return productoRepository.findAll()
                .stream()
                .map(productoMapper::toDTO)
                .collect(Collectors.toList());
    }

    public ProductoResponseDTO obtenerPorId(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + id));
        return productoMapper.toDTO(producto);
    }

    public ProductoResponseDTO crear(ProductoRequestDTO requestDTO) {
        Producto producto = productoMapper.toEntity(requestDTO);
        Producto guardado = productoRepository.save(producto);
        return productoMapper.toDTO(guardado);
    }

    public ProductoResponseDTO actualizar(Long id, ProductoRequestDTO requestDTO) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + id));

        producto.setNombre(requestDTO.getNombre());
        producto.setDescripcion(requestDTO.getDescripcion());
        producto.setPrecio(requestDTO.getPrecio());
        producto.setStock(requestDTO.getStock());

        Producto actualizado = productoRepository.save(producto);
        return productoMapper.toDTO(actualizado);
    }

    public void eliminar(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new RuntimeException("Producto no encontrado con id: " + id);
        }
        productoRepository.deleteById(id);
    }

    public List<ProductoResponseDTO> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre)
                .stream()
                .map(productoMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<ProductoResponseDTO> obtenerConStockBajo(Integer umbral) {
        return productoRepository.findByStockLessThan(umbral)
                .stream()
                .map(productoMapper::toDTO)
                .collect(Collectors.toList());
    }
}