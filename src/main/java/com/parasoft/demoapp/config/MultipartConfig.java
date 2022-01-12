package com.parasoft.demoapp.config;

import com.parasoft.demoapp.messages.ConfigMessages;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

import javax.servlet.MultipartConfigElement;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

@Slf4j
@Configuration
public class MultipartConfig {
	
	public static final String DEFAULT_MULTIPART_LOCATION = "./pda-files/tmp";

	@Value("${spring.servlet.multipart.location}")
	private String multipartLocation;

	@Value("${spring.servlet.multipart.max-file-size}")
	private DataSize maxFileSize;

	@Value("${spring.servlet.multipart.max-request-size}")
	private DataSize maxRequestSize;

	@Value("${spring.servlet.multipart.file-size-threshold}")
	private DataSize fileSizeThreshold;

	@Bean
    public MultipartConfigElement multipartConfigElement() {
		
		if(multipartLocation == null || "".equals(multipartLocation.trim())){
			multipartLocation = DEFAULT_MULTIPART_LOCATION;
		}
		
        MultipartConfigFactory factory = new MultipartConfigFactory();
        File tmpFile = new File(multipartLocation);
        if (!tmpFile.exists()) {
            tmpFile.mkdirs();
        }

        String tmpPath;
		try {
			tmpPath = tmpFile.getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(
					MessageFormat.format(ConfigMessages.CAN_NOT_DEFINE_MULTIPART_TEMPORARY_LOCATION, multipartLocation), e);
		}
		
        factory.setLocation(tmpPath);
		factory.setFileSizeThreshold(fileSizeThreshold);
		factory.setMaxFileSize(maxFileSize);
		factory.setMaxRequestSize(maxRequestSize);

        log.info(MessageFormat.format(ConfigMessages.MULTIPART_TEMPORARY_LOCATION, tmpPath));
        
        return factory.createMultipartConfig();
    }
}