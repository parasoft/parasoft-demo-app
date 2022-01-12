package com.parasoft.demoapp.repository.global;

import com.parasoft.demoapp.model.global.DatabaseInitResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DatabaseInitResultRepository extends JpaRepository<DatabaseInitResultEntity, Long> {
    DatabaseInitResultEntity findFirstByOrderByCreatedTimeDesc();
}
