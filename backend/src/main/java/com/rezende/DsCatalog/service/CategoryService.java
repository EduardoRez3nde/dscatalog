package com.rezende.DsCatalog.service;

import com.rezende.DsCatalog.dto.CategoryDTO;
import com.rezende.DsCatalog.entities.Category;
import com.rezende.DsCatalog.respositories.CategoryRepository;
import com.rezende.DsCatalog.service.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public CategoryDTO findById(@PathVariable Long id){
        Category entity = categoryRepository.findById(id).orElseThrow(
        () -> new ResourceNotFoundException("Recurso não encontrado!"));
        return new CategoryDTO(entity);
    }

    @Transactional(readOnly = true)
    public Page<CategoryDTO> findAll(Pageable pageable){
        Page<Category> entity = categoryRepository.findAll(pageable);
        return entity.map(CategoryDTO::new);


    }

}
