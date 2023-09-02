package com.rezende.DsCatalog.service;

import com.rezende.DsCatalog.dto.CategoryDTO;
import com.rezende.DsCatalog.entities.Category;
import com.rezende.DsCatalog.respositories.CategoryRepository;
import com.rezende.DsCatalog.service.exceptions.DataBaseException;
import com.rezende.DsCatalog.service.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

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
    public Page<CategoryDTO> findAllPage(PageRequest pageRequest){
        Page<Category> entity = categoryRepository.findAll(pageRequest);
        return entity.map(CategoryDTO::new);
    }

    @Transactional
    public CategoryDTO insert(CategoryDTO dto){
        Category entity = new Category();
        entity.setName(dto.getName());

        entity = categoryRepository.save(entity);
        return new CategoryDTO(entity);
    }

    @Transactional
    public CategoryDTO update(@PathVariable Long id, @RequestBody CategoryDTO dto){
        try {
            Category entity = categoryRepository.getReferenceById(id);
            entity.setName(dto.getName());

            categoryRepository.save(entity);
            return new CategoryDTO(entity);
        }
        catch (EntityNotFoundException e){
            throw new ResourceNotFoundException("Impossivel atualizar. Id não encontrado!");
        }
    }
    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(@PathVariable Long id){
        try{
            categoryRepository.deleteById(id);
        }
        catch (EmptyResultDataAccessException e){
            throw new ResourceNotFoundException("Impossivel deletar. id não encontrado");
        }
        catch (DataIntegrityViolationException e){
            throw new DataBaseException("Erro de integridade referêncial!");
        }
    }

}
