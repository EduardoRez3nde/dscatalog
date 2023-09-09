package com.rezende.DsCatalog.services;

import com.rezende.DsCatalog.dto.ProductDTO;
import com.rezende.DsCatalog.respositories.ProductRepository;
import com.rezende.DsCatalog.service.ProductService;
import com.rezende.DsCatalog.tests.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class ProductServiceIT {

	private long existingId;
	private long nonExistingId;
	private long countTotalProduct;
	private ProductDTO productDTO;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private ProductService service;
	
	@BeforeEach
	void setUp() throws Exception{

		existingId = 1L;
		nonExistingId = 1000L;
		countTotalProduct = 25;

		productDTO = Factory.createProductDto();
	}

	@Test
	public void findAllPageShouldReturnSortedPageWhenSortByName() {
		PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("name"));
		Page<ProductDTO> result = service.findAllPage(pageRequest);

		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals("Macbook Pro", result.getContent().get(0).getName());
		Assertions.assertEquals("PC Gamer", result.getContent().get(1).getName());
		Assertions.assertEquals("PC Gamer Alfa", result.getContent().get(2).getName());

	}

	@Test
	public void findAllPageShouldReturnPage() {
		PageRequest pageRequest = PageRequest.of(0, 10);
		Page<ProductDTO> result = service.findAllPage(pageRequest);

		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals(0, result.getNumber());
		Assertions.assertEquals(10, result.getSize());
		Assertions.assertEquals(countTotalProduct, result.getTotalElements());
	}

	@Test
	public void findAllPageShouldReturnEmptyPageWhenDoesNotExist() {
		PageRequest pageRequest = PageRequest.of(500, 10);
		Page<ProductDTO> result = service.findAllPage(pageRequest);

		Assertions.assertTrue(result.isEmpty());
	}
	
	@Test
	public void deleteShouldDeleteResourceWhenIdExists() {
		service.delete(existingId);
		Assertions.assertEquals(countTotalProduct - 1, productRepository.count());
	}

	/*
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistingId);
		});
	}
	*/

	
	
	
	
	
	
	
	
	
	
	
}
