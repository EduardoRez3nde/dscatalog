package com.rezende.DsCatalog.service;

import com.rezende.DsCatalog.dto.UserDTO;
import com.rezende.DsCatalog.dto.UserInsertDTO;
import com.rezende.DsCatalog.entities.Role;
import com.rezende.DsCatalog.entities.User;
import com.rezende.DsCatalog.respositories.RoleRepository;
import com.rezende.DsCatalog.respositories.UserRepository;
import com.rezende.DsCatalog.service.exceptions.DataBaseException;
import com.rezende.DsCatalog.service.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserDTO findById(Long id) {
        User result = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Entity not Found!"));
        return new UserDTO(result);
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> findAll(Pageable pageable) {
        Page<User> result = repository.findAll(pageable);
        return result.map(UserDTO::new);
    }

    @Transactional
    public UserDTO insert(UserInsertDTO dto) {
        User entity = new User();
        copyDtoToEntity(dto, entity);
        entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        entity = repository.save(entity);
        return new UserDTO(entity);
    }

    @Transactional
    public UserDTO update(Long id, UserDTO dto){
        try {
            User entity = repository.getReferenceById(id);
            copyDtoToEntity(dto, entity);
            repository.save(entity);
            return new UserDTO(entity);
        }
        catch (EntityNotFoundException e){
            throw new ResourceNotFoundException("Impossivel atualizar. Id não encontrado!");
        }
    }

    public void delete(@PathVariable Long id){
        try{
            repository.deleteById(id);
        }
        catch (EmptyResultDataAccessException e){
            throw new ResourceNotFoundException("Impossivel deletar. id não encontrado");
        }
        catch (DataIntegrityViolationException e){
            throw new DataBaseException("Erro de integridade referêncial!");
        }
    }

    private void copyDtoToEntity(UserDTO dto, User entity){

        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setEmail(dto.getEmail());

        entity.getRoles().clear();
        dto.getRoles().forEach(roleDTO -> {
            Role role = roleRepository.getReferenceById(roleDTO.getId());
            entity.getRoles().add(role);
        });
    }

}
