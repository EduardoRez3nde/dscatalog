package com.rezende.DsCatalog.respositories;

import com.rezende.DsCatalog.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
