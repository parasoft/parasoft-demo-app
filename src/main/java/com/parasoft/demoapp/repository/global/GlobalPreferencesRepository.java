package com.parasoft.demoapp.repository.global;

import com.parasoft.demoapp.model.global.preferences.GlobalPreferencesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GlobalPreferencesRepository extends JpaRepository<GlobalPreferencesEntity, Long> {

}
