package com.rezende.DsCatalog.dto;

import com.rezende.DsCatalog.entities.Role;

import java.io.Serializable;

public class RoleDTO implements Serializable {

    private Long id;
    private String authority;

    public RoleDTO() {}

    public RoleDTO(Long id, String authority) {
        this.id = id;
        this.authority = authority;
    }

    public RoleDTO(Role role) {
        id = role.getId();
        authority = role.getAuthority();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }
}
