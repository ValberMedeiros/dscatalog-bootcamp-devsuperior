package com.valbermedeiros.dscatalog.repositories;

import com.valbermedeiros.dscatalog.entities.Product;
import com.valbermedeiros.dscatalog.tests.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.Optional;

@DataJpaTest
class ProductRepositoryTests {

    @Autowired
    private ProductRepository repository;

    private long existingId;
    private long nonExixstId;
    private long countTotalProducts;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExixstId = 1000L;
        countTotalProducts = 25L;
    }

    @Test
    void deleteShouldDeleteObjectWhenIdExists() {

        repository.deleteById(existingId);

        Optional<Product> result = repository.findById(existingId);
        Assertions.assertFalse(result.isPresent());
    }

    @Test
    void deleteShouldThrowResultDataAccessExceptionWhenIdNotExists() {

        Assertions.assertThrows(EmptyResultDataAccessException.class, () -> repository.deleteById(nonExixstId));
    }

    @Test
    void saveShouldPersistWithAutoincrementWhenIdIsNull() {
        Product product = Factory.createProduct();
        product.setId(null);

        product = repository.save(product);

        Assertions.assertNotNull(product.getId());
        Assertions.assertEquals(countTotalProducts + 1, product.getId());
    }

    @Test
    void findByIdShouldReturnOptionalIsPresentWhenIdExists() {
        Optional<Product> optional = repository.findById(existingId);

        Assertions.assertTrue(optional.isPresent());
    }

    @Test
    void findByIdShouldReturnOptionalEmptyWhenIdNotExists() {
        Optional<Product> optional = repository.findById(nonExixstId);

        Assertions.assertFalse(optional.isPresent());
    }
}
