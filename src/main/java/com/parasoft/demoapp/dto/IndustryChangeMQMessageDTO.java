package com.parasoft.demoapp.dto;

import com.parasoft.demoapp.model.global.preferences.IndustryType;
import lombok.Data;

@Data
public class IndustryChangeMQMessageDTO {

    private boolean isIndustryChanged = false;
    private IndustryType previousIndustry;
    private IndustryType currentIndustry;

    public IndustryChangeMQMessageDTO(IndustryType previousIndustry, IndustryType currentIndustry) {
        this.previousIndustry = previousIndustry;
        this.currentIndustry = currentIndustry;

        if(previousIndustry != currentIndustry){
            this.isIndustryChanged = true;
        }
    }
}
