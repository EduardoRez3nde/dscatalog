package com.rezende.DsCatalog.controllers;

import com.rezende.DsCatalog.dto.ProductDTO;
import com.rezende.DsCatalog.dto.UserDTO;
import com.rezende.DsCatalog.dto.UserInsertDTO;
import com.rezende.DsCatalog.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    @Autowired
    private UserService service;

    @GetMapping(value = "/{id}")
    public ResponseEntity<UserDTO> findById(@PathVariable Long id) {
        UserDTO result = service.findById(id);
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<Page<UserDTO>> findAll(Pageable pageable) {
        Page<UserDTO> entity = service.findAll(pageable);
        return ResponseEntity.ok(entity);
    }

    @PostMapping
    public ResponseEntity<UserDTO> insert(@Valid @RequestBody UserInsertDTO dto){
        UserDTO newDto = service.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(newDto.getId()).toUri();
        return ResponseEntity.created(uri).body(newDto);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<UserDTO> update(@Valid @PathVariable Long id, @RequestBody UserDTO dto){
        dto = service.update(id, dto);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
