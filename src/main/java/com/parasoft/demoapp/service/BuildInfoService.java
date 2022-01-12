package com.parasoft.demoapp.service;

import com.parasoft.demoapp.dto.BuildInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Service;

@Service
public class BuildInfoService {

    @Autowired
    private BuildProperties buildProperties;

    public BuildInfoDTO getBuildInfo() {
        BuildInfoDTO buildInfo = new BuildInfoDTO();
        buildInfo.setBuildVersion(buildProperties.getVersion());
        buildInfo.setBuildTime(buildProperties.getTime().toEpochMilli());
        // The build information property name for buildId should be same as the one in Gradle build script.
        buildInfo.setBuildId(buildProperties.get("id"));
        return buildInfo;
    }
}
