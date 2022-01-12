package com.parasoft.demoapp.config;

import com.parasoft.demoapp.model.global.preferences.IndustryType;

public class ImplementedIndustries {

    /**
     * Not all industries are implemented, we need update this method when a new industry is implemented.
     * @return All implemented Industries.
     */
    public static IndustryType[] get(){
        return new IndustryType[]{IndustryType.DEFENSE, IndustryType.AEROSPACE, IndustryType.OUTDOOR};
    }

    public static boolean isIndustryImplemented(IndustryType industry){
        for(IndustryType industryType : get()){
            if(industry.equals(industryType)){
                return true;
            }
        }

        return false;
    }
}
