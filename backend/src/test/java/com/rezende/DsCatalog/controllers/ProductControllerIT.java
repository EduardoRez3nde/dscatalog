package com.rezende.DsCatalog.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rezende.DsCatalog.dto.ProductDTO;
import com.rezende.DsCatalog.tests.Factory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import javax.print.attribute.standard.Media;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerIT {

    private long existingId;
    private long nonExistingId;
    private long countTotalProduct;
    private ProductDTO productDTO;

    private ResultActions result;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws Exception {

        existingId = 1L;
        nonExistingId = 1000L;
        countTotalProduct = 25;
    }

    @Test
    public void findAllShouldReturnDortedPageWhenSortByName() throws Exception {
        result = mockMvc.perform(get("/products?page=0&size=12&sort=name,asc")
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.content").exists());
        result.andExpect(jsonPath("$.totalElements").value(countTotalProduct));
        result.andExpect(jsonPath("$.content[0].name").value("Macbook Pro"));
        result.andExpect(jsonPath("$.content[1].name").value("PC Gamer"));
        result.andExpect(jsonPath("$.content[2].name").value("PC Gamer Alfa"));
    }

    @Test
    public void updateShouldReturnProductDTOWhenidExists() throws Exception {
        productDTO = Factory.createProductDto();
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        String name = productDTO.getName();
        String description = productDTO.getDescription();

        result = mockMvc.perform(put("/products/{id}", existingId)
                .content(jsonBody)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.name").value(name));
        result.andExpect(jsonPath("$.description").value(description));
    }

    @Test
    public void updateShouldReturnNotFoundWhenidDoesNotExists() throws Exception {
        productDTO = Factory.createProductDto();
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        result = mockMvc.perform(put("/products/{id}", nonExistingId)
                .content(jsonBody)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON));
        result.andExpect(status().isNotFound());
    }
}
