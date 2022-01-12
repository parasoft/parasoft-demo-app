package com.parasoft.demoapp.repository.global;

import com.parasoft.demoapp.model.global.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    boolean existsByName(String roleName);

    RoleEntity findByName(String roleName);
}
