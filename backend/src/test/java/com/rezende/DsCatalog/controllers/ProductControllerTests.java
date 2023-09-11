package com.rezende.DsCatalog.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rezende.DsCatalog.dto.ProductDTO;
import com.rezende.DsCatalog.service.ProductService;
import com.rezende.DsCatalog.service.UserService;
import com.rezende.DsCatalog.service.exceptions.DataBaseException;
import com.rezende.DsCatalog.service.exceptions.ResourceNotFoundException;
import com.rezende.DsCatalog.tests.Factory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ProductController.class)
class ProductControllerTests {

	private long existingId;
	private long nonExistingId;
	private long dependentId;
	
    private PageImpl<ProductDTO> page;
    private ProductDTO productDTO;
    ResultActions result;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;
    
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() throws Exception {
    	
    	existingId = 1L;
    	nonExistingId = 1000L;
    	dependentId = 2L;
    	
        productDTO = Factory.createProductDto();
        page = new PageImpl<>(List.of(productDTO));

        Mockito.when(productService.findAllPage(any())).thenReturn(page);
        
        Mockito.when(productService.findById(existingId)).thenReturn(productDTO);
        Mockito.when(productService.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);
        
        Mockito.when(productService.update(eq(existingId), any())).thenReturn(productDTO);
        Mockito.when(productService.update(eq(nonExistingId), any())).thenThrow(ResourceNotFoundException.class);
        
        Mockito.doNothing().when(productService).delete(existingId);
        Mockito.doThrow(ResourceNotFoundException.class).when(productService).delete(nonExistingId);
        Mockito.doThrow(DataBaseException.class).when(productService).delete(dependentId);
        
        Mockito.when(productService.insert(any())).thenReturn(productDTO);
    }
    
    @Test
    public void insertShouldReturnCreateProductDTO() throws Exception {
    	
    	String jsonBody = objectMapper.writeValueAsString(productDTO);
    	
    	result = mockMvc.perform(post("/products")
    			.accept(MediaType.APPLICATION_JSON)
    			.contentType(MediaType.APPLICATION_JSON)
    			.content(jsonBody));
    	result.andExpect(status().isCreated());
    	result.andExpect(jsonPath("$.id").exists());
    	result.andExpect(jsonPath("$.name").exists()); 	
    	result.andExpect(jsonPath("$.description").exists());
    }
    
    @Test
    public void deleteShouldReturnNoContentWhenIdExists() throws Exception {
    	
    	result = mockMvc.perform(delete("/products/{id}", existingId)
    			.accept(MediaType.APPLICATION_JSON));
    	result.andExpect(status().isNoContent());
    }
    
    @Test
    public void deleteShouldreturnNotFoundWhenIdDoesNotExists() throws Exception {
   
    	result = mockMvc.perform(delete("/products/{id}", nonExistingId)
    			.accept(MediaType.APPLICATION_JSON)
    			);
    	result.andExpect(status().isNotFound());
    }
    
    @Test
    public void updateShouldReturnProductDTOWhenIdExists() throws Exception {
    	
    	String jsonBody = objectMapper.writeValueAsString(productDTO);  
    	
    	result = mockMvc.perform(put("/products/{id}", existingId)
    			.content(jsonBody)
    			.contentType(MediaType.APPLICATION_JSON)
    			.accept(MediaType.APPLICATION_JSON));
    	
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").exists());
    	result.andExpect(jsonPath("$.name").exists()); 	
    	result.andExpect(jsonPath("$.description").exists()); 	
    }
    
    @Test
    public void updateShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
    	
    	String jsonBody = objectMapper.writeValueAsString(productDTO);  
    	
    	result = mockMvc.perform(put("/products/{id}", nonExistingId)
    			.content(jsonBody)
    			.contentType(MediaType.APPLICATION_JSON)
    			.accept(MediaType.APPLICATION_JSON));
    	
        result.andExpect(status().isNotFound());
    }

    @Test
    public void findAllShouldReturnPage() throws Exception {
        result = mockMvc.perform(get("/products").accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isOk());
    }
    
    @Test
    public void findByIdShouldReturnProductWhenIdExixts() throws Exception {
    	result = mockMvc.perform(get("/products/{id}", existingId)
    			.accept(MediaType.APPLICATION_JSON));
    	
    	result.andExpect(status().isOk());
    	result.andExpect(jsonPath("$.id").exists());
    	result.andExpect(jsonPath("$.name").exists()); 	
    	result.andExpect(jsonPath("$.description").exists()); 	
   }
    
    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExixts() throws Exception {
    	result = mockMvc.perform(get("/products/{$.id}", nonExistingId).accept(MediaType.APPLICATION_JSON));
    	
    	result.andExpect(status().isNotFound());
   }

}


















