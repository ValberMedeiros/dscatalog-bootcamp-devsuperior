package com.valbermedeiros.dscatalog.services;

import com.valbermedeiros.dscatalog.dto.ProductDto;
import com.valbermedeiros.dscatalog.repositories.ProductRepository;
import com.valbermedeiros.dscatalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ProductServiceIT {

    @Autowired
    private ProductService service;

    @Autowired
    private ProductRepository repository;

    private Long existingId;
    private Long nonExistingId;
    private Long countTotalProducts;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 1000L;
        countTotalProducts = 25L;
    }

    @Test
    void deleteShouldDeleteResourceWhenIdExists() {
        service.delete(existingId);

        assertEquals(countTotalProducts -1, repository.count());
    }

    @Test
    void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
        assertThrows(ResourceNotFoundException.class, () -> service.delete(nonExistingId));
    }

    @Test
    void findAllPagedShouldReturnPageWhenPage0Size10() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        Page<ProductDto> result = service.findAll(pageRequest);

        assertFalse(result.isEmpty());
        assertEquals(0, result.getNumber());
        assertEquals(10, result.getSize());
        assertEquals(countTotalProducts, result.getTotalElements());

    }

    @Test
    void findAllPagedShouldReturnPageWhenPageDoesNotExists() {
        PageRequest pageRequest = PageRequest.of(50, 10);

        Page<ProductDto> result = service.findAll(pageRequest);

        assertTrue(result.isEmpty());
    }

    @Test
    void findAllPagedShouldReturnSortedPageWhenSortByName() {
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("name"));

        Page<ProductDto> result = service.findAll(pageRequest);

        assertFalse(result.isEmpty());
        assertEquals("Macbook Pro", result.getContent().get(0).getName());
        assertEquals("PC Gamer", result.getContent().get(1).getName());
        assertEquals("PC Gamer Alfa", result.getContent().get(2).getName());

    }
}
