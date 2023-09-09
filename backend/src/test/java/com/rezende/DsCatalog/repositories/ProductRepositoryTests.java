package com.rezende.DsCatalog.repositories;

import com.rezende.DsCatalog.entities.Product;
import com.rezende.DsCatalog.respositories.ProductRepository;
import com.rezende.DsCatalog.tests.Factory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ProductRepositoryTests {

    private long existingId;
    private long nonExistingId;
    private long countTotalProducts;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() throws Exception {
        Long existingId = 1L;
        Long nonExistingId = 1000L;
        Long countTotalProducts = 26L;
    }

    @Test
    void saveShouldPersistWithAutoIncrementWhenIsNull() {

        Product product = Factory.createProduct();
        product.setId(null);

        product = productRepository.save(product);
        assertNotNull(product.getId());
        assertEquals(26L, product.getId());
    }

    @Test
    void deleteShouldDeleteObjectWhenIdExists() {

        productRepository.deleteById(existingId);
        Optional<Product> result = productRepository.findById(existingId);
        assertFalse(result.isPresent());

    }

    /*
    @Test
    public void deleteShouldThrowEmptyResultDataAccessExceptionWhenIdDoesNotExists() {

        Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
            productRepository.deleteById(nonExistingId);
        });

    }
     */

    @Test
    void findByIdShouldReturnEmptyOptionalWhenIdExists() {
        Optional<Product> result = productRepository.findById(1L);
        assertTrue(result.isPresent());
    }

    @Test
    void findByIdShouldReturnOptionalWhenIdDoesNotExists() {
        Optional<Product> result = productRepository.findById(1000L);
        assertTrue(result.isEmpty());
    }





}
