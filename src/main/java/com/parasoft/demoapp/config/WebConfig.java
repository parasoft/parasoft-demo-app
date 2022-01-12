package com.parasoft.demoapp.config;

import com.parasoft.demoapp.config.interceptor.ParasoftJDBCProxyValidateInterceptor;
import com.parasoft.demoapp.messages.ConfigMessages;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.MessageFormat;

@Slf4j
@Configuration
public class WebConfig implements WebMvcConfigurer {
	
	public static final String UPLOADED_IMAGE_STATIC_PATH_PATTERN = "/uploaded_images/**";
	public static final String UPLOADED_IMAGES_SUB_LOCATION =  "/uploaded_images/";
	
	@Value("${uploaded.image-parent-location}")
	private String uploadedImageParentLocation;

	@Value("${server.port}")
	private int serverPort;

	@Autowired
	private ParasoftJDBCProxyValidateInterceptor parasoftJDBCProxyValidateInterceptor;

	private String uploadedImagesStorePathLocation;

	@Bean
	public PasswordEncoder gerEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		
		File parentLocation = new File(uploadedImageParentLocation);
		if(!parentLocation.exists()) {
			parentLocation.mkdirs();
		}
		
		try {
			uploadedImagesStorePathLocation =
					Paths.get(parentLocation.getCanonicalPath(), UPLOADED_IMAGES_SUB_LOCATION) + File.separator;
		} catch (IOException e) {
			e.printStackTrace();
			log.warn(MessageFormat.format(ConfigMessages.CAN_NOT_ADD_RESOURCES_STATIC_LOCATION, uploadedImageParentLocation));
			return;
		}
		
		registry.addResourceHandler(UPLOADED_IMAGE_STATIC_PATH_PATTERN)
				.addResourceLocations("file:" + uploadedImagesStorePathLocation);
		
		log.info(MessageFormat.format(ConfigMessages.MAP_RESOURCES_STATIC_PATTERN,
				UPLOADED_IMAGE_STATIC_PATH_PATTERN, uploadedImagesStorePathLocation));
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(parasoftJDBCProxyValidateInterceptor)
				.addPathPatterns("/v1/assets/categories/**",
								 "/v1/assets/items/**",
								 "/v1/cartItems/**",
								 "/v1/orders/**",
								 "/v1/locations/**",
								 "/v1/parasoftJDBCProxy/status");
	}

	public String getUploadedImagesStorePath() {
		return uploadedImagesStorePathLocation;
	}

	public int getServerPort(){
		return serverPort;
	}
}