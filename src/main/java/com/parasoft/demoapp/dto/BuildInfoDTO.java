package com.parasoft.demoapp.dto;

import lombok.Data;

@Data
public class BuildInfoDTO {

    private String buildVersion;

    private String buildId;

    private long buildTime;

}
