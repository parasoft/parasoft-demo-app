package com.parasoft.demoapp.repository.industry;

import com.parasoft.demoapp.model.global.LocalizationLanguageType;
import com.parasoft.demoapp.model.industry.LabelEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface LabelRepository extends JpaRepository<LabelEntity, Long>, JpaSpecificationExecutor<LabelEntity> {

    void deleteAllByLanguageType(LocalizationLanguageType languageType);

    List<LabelEntity> findAllByLanguageType(LocalizationLanguageType languageType);
}
