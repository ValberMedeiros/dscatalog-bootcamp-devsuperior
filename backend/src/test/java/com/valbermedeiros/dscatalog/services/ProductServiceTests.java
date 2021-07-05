package com.valbermedeiros.dscatalog.services;

import com.valbermedeiros.dscatalog.dto.ProductDto;
import com.valbermedeiros.dscatalog.entities.Category;
import com.valbermedeiros.dscatalog.entities.Product;
import com.valbermedeiros.dscatalog.repositories.CategoryRepository;
import com.valbermedeiros.dscatalog.repositories.ProductRepository;
import com.valbermedeiros.dscatalog.services.exceptions.DataBaseException;
import com.valbermedeiros.dscatalog.services.exceptions.ResourceNotFoundException;
import com.valbermedeiros.dscatalog.tests.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class ProductServiceTests {

    @InjectMocks
    private ProductService service;

    @Mock
    private ProductRepository repository;

    @Mock
    private CategoryRepository categoryRepository;

    private long existingId;
    private long nonExisttingId;
    private long dependetId;
    private PageImpl<Product> page;
    private Product product;
    private Category category;
    private ProductDto productDto;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExisttingId = 1000L;
        dependetId = 4L;
        product = Factory.createProduct();
        category = Factory.createCategory();
        page = new PageImpl<>(List.of(product));
        productDto = Factory.createProductDTO();

        when(repository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);
        when(repository.save(ArgumentMatchers.any())).thenReturn(product);
        when(repository.findById(existingId)).thenReturn(Optional.of(product));
        when(repository.findById(nonExisttingId)).thenReturn(Optional.empty());
        when(repository.find(any(), any(), any())).thenReturn(page);
        when(repository.getOne(existingId)).thenReturn(product);
        when(repository.getOne(nonExisttingId)).thenThrow(EntityNotFoundException.class);
        when(categoryRepository.getOne(existingId)).thenReturn(category);
        when(categoryRepository.getOne(nonExisttingId)).thenThrow(EntityNotFoundException.class);
        doNothing().when(repository).deleteById(existingId);
        doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExisttingId);
        doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependetId);
    }

    @Test
    void deleteShouldDoNothingWhenIdExists() {
        assertDoesNotThrow(() -> service.delete(existingId));
        verify(repository).deleteById(existingId);
    }

    @Test
    void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
        assertThrows(ResourceNotFoundException.class, () -> service.delete(nonExisttingId));
        verify(repository).deleteById(nonExisttingId);
    }

    @Test
    void deleteShouldThrowDataBaseExceptionWhenIdIsDependet() {
        assertThrows(DataBaseException.class, () -> service.delete(dependetId));
        verify(repository).deleteById(dependetId);
    }

    @Test
    void findAllPagedShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProductDto> result = service.findAll(0L, "", pageable);

        assertNotNull(result);
    }

    @Test
    void findByIdShouldReturnProductDTOWhenIdExists() {
        ProductDto productDto = service.findById(existingId);

        assertNotNull(productDto);
        verify(repository).findById(existingId);
    }

    @Test
    void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
        assertThrows(ResourceNotFoundException.class, () -> service.findById(nonExisttingId));
    }

    @Test
    void updateShouldReturnProductDTOWhenIdExists() {
        ProductDto result = service.update(productDto, existingId);
        assertNotNull(result);
    }

    @Test
    void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
        assertThrows(ResourceNotFoundException.class, () -> service.update(productDto, nonExisttingId));
    }
}
