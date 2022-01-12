package com.parasoft.demoapp.repository.global;

import com.parasoft.demoapp.model.global.preferences.DemoBugEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DemoBugRepository extends JpaRepository<DemoBugEntity,Long> {

    void deleteByGlobalPreferencesId(long globalPreferencesId);
}
