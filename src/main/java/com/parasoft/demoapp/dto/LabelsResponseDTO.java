package com.parasoft.demoapp.dto;

import com.parasoft.demoapp.model.global.LocalizationLanguageType;
import com.parasoft.demoapp.model.industry.LabelEntity;
import com.parasoft.demoapp.util.PropertiesMapUtil;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class LabelsResponseDTO {

    private LocalizationLanguageType languageType;

    private boolean labelsOverrided;

    private Map<String, String> labelPairs;

    /**
     * Convert {@link LabelEntity} to {@link LabelsResponseDTO}.
     * @param labelEntities Entities should have the same languageType values with languageType param.
     * @param languageType {@link LocalizationLanguageType}
     * @return {@link LabelsResponseDTO}
     */
    public static LabelsResponseDTO convertFrom(List<LabelEntity> labelEntities, LocalizationLanguageType languageType){
        LabelsResponseDTO labelsDTO = new LabelsResponseDTO();
        labelsDTO.setLanguageType(languageType);

        Map<String, String> labelPairs = new HashMap<>();

        if(labelEntities != null){
            for(LabelEntity labelEntity : labelEntities){
                labelPairs.put(labelEntity.getName(), labelEntity.getValue());
            }

            labelPairs = PropertiesMapUtil.sortByKey(labelPairs);
        }

        labelsDTO.setLabelPairs(labelPairs);

        return labelsDTO;
    }
}
