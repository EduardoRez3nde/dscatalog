package com.rezende.DsCatalog.services;

import com.rezende.DsCatalog.dto.ProductDTO;
import com.rezende.DsCatalog.entities.Category;
import com.rezende.DsCatalog.entities.Product;
import com.rezende.DsCatalog.respositories.CategoryRepository;
import com.rezende.DsCatalog.respositories.ProductRepository;
import com.rezende.DsCatalog.service.CategoryService;
import com.rezende.DsCatalog.service.ProductService;
import com.rezende.DsCatalog.service.exceptions.DataBaseException;
import com.rezende.DsCatalog.service.exceptions.ResourceNotFoundException;
import com.rezende.DsCatalog.tests.Factory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class ProductServiceTests {

    private Long existingId;
    private Long nonExixtingId;
    private Long dependentId;
    private ProductDTO productDTO;
    private Category category;
    private Product product;
    
    @InjectMocks
    private ProductService service;

    @Mock
    private ProductRepository repository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryService categoryService;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExixtingId = 1000L;
        dependentId = 4L;

        product = Factory.createProduct();
        PageImpl<Product> page = new PageImpl<>(List.of(product));

        productDTO = Factory.createProductDto();
        category = Factory.createCategory();

        Mockito.when(repository.getReferenceById(existingId)).thenReturn(product);
        Mockito.when(repository.getReferenceById(nonExixtingId)).thenThrow(EntityNotFoundException.class);

        Mockito.when(categoryRepository.getReferenceById(existingId)).thenReturn(category);
        Mockito.when(categoryRepository.getReferenceById(nonExixtingId)).thenThrow(EntityNotFoundException.class);

        Mockito.when(repository.findAll((Pageable)ArgumentMatchers.any())).thenReturn(page);
        Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);

        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));
        Mockito.when(repository.findById(nonExixtingId)).thenReturn(Optional.empty());
        Mockito.doThrow(ResourceNotFoundException.class).when(repository).findById(nonExixtingId);

        Mockito.doNothing().when(repository).deleteById(existingId);
        Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExixtingId);

        Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
    }

    @Test
    void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
        assertThrows(ResourceNotFoundException.class, () -> {
            service.update(nonExixtingId, productDTO);
        });
    }

    @Test
    void updateShouldReturnProductDTOWhenIdExists() {
        productDTO = service.update(existingId, productDTO);
        assertNotNull(productDTO);
    }

    @Test
    void findByIdShouldReturnProductDTOWhenIdExists() {
        ProductDTO productDTO = service.findById(existingId);
        assertNotNull(productDTO);

        Mockito.verify(repository).findById(existingId);
    }

    @Test
    void findByIdShouldResourceNotFoundExceptionWhenDoesNotIdExist() {
        assertThrows(ResourceNotFoundException.class, () -> {
            service.findById(nonExixtingId);
        });
        Mockito.verify(repository).findById(nonExixtingId);
    }

    @Test
    void deleteShouldDoNothingWhenIdExists(){
        assertDoesNotThrow(() -> {
            service.delete(existingId);
        });
        Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);
    }

    @Test
    void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist(){
        assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(nonExixtingId);
        });
        Mockito.verify(repository, Mockito.times(1)).deleteById(nonExixtingId);
    }

    @Test
    void deleteShouldThrowDataBaseExceptionWhenDependId() {
        assertThrows(DataBaseException.class, () -> {
           service.delete(dependentId);
        });
        Mockito.verify(repository, Mockito.times(1)).deleteById(dependentId);
    }

    @Test
    void findAllPagedShouldResturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProductDTO> result = service.findAllPage(pageable);
        assertNotNull(result);

        Mockito.verify(repository).findAll(pageable);
    }
}
