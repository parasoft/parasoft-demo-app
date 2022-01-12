package com.parasoft.demoapp.dto;

import com.parasoft.demoapp.model.global.LocalizationLanguageType;
import com.parasoft.demoapp.model.industry.LabelEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Schema
public class LabelsRequestDTO {

    private LocalizationLanguageType languageType;

    private Boolean labelsOverrided;

    private Map<String, String> labelPairs;

    public List<LabelEntity> toEntities() {
        List<LabelEntity> labelEntities = new ArrayList<>();
        if(labelPairs == null){
            return labelEntities;
        }

        labelPairs.forEach((key, value) -> labelEntities.add(new LabelEntity(key, value, languageType)));

        return labelEntities;
    }
}
