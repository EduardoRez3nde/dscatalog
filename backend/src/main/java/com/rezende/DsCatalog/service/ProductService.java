package com.rezende.DsCatalog.service;

import com.rezende.DsCatalog.dto.CategoryDTO;
import com.rezende.DsCatalog.dto.ProductDTO;
import com.rezende.DsCatalog.entities.Category;
import com.rezende.DsCatalog.entities.Product;
import com.rezende.DsCatalog.respositories.CategoryRepository;
import com.rezende.DsCatalog.respositories.ProductRepository;
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

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id){
        Product entity = productRepository.findById(id).orElseThrow(
        () -> new ResourceNotFoundException("Recurso não encontrado!"));
        return new ProductDTO(entity, entity.getCategories());
    }

    @Transactional(readOnly = true)
    public Page<ProductDTO> findAllPage(PageRequest pageRequest){
        Page<Product> entity = productRepository.findAll(pageRequest);
        return entity.map(x -> new ProductDTO(x, x.getCategories()));
    }

    @Transactional
    public ProductDTO insert(ProductDTO dto){
        Product entity = new Product();
        copyDtoToEntity(dto, entity);
        entity = productRepository.save(entity);
        return new ProductDTO(entity);
    }

    @Transactional
    public ProductDTO update(Long id, ProductDTO dto){
        try {
            Product entity = productRepository.getReferenceById(id);
            copyDtoToEntity(dto, entity);
            productRepository.save(entity);
            return new ProductDTO(entity);
        }
        catch (EntityNotFoundException e){
            throw new ResourceNotFoundException("Impossivel atualizar. Id não encontrado!");
        }
    }
    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(@PathVariable Long id){
        try{
            productRepository.deleteById(id);
        }
        catch (EmptyResultDataAccessException e){
            throw new ResourceNotFoundException("Impossivel deletar. id não encontrado");
        }
        catch (DataIntegrityViolationException e){
            throw new DataBaseException("Erro de integridade referêncial!");
        }
    }

    private void copyDtoToEntity(ProductDTO dto, Product entity){
        entity.setName(dto.getName());
        entity.setImgUrl(dto.getImgUrl());
        entity.setPrice(dto.getPrice());
        entity.setDescription(dto.getDescription());
        entity.setDate(dto.getDate());

        entity.getCategories().clear();
        for (CategoryDTO catDto : dto.getCategories()){
            Category category = categoryRepository.getReferenceById(catDto.getId());
            entity.getCategories().add(category);
        }
    }

}
