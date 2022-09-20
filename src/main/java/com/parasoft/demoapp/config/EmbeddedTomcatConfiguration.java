package com.parasoft.demoapp.config;

import org.apache.catalina.Context;
import org.apache.catalina.webresources.ExtractingRoot;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmbeddedTomcatConfiguration {

    /**
     * PDA-1113: To solve problem that the response of the POST/PUT first request is slow after project is started up by war file
     * and only for the request with domain request body.
     * Reference: <a href="https://stackoverflow.com/questions/59242577/why-my-springboot-with-embbeded-tomcat-too-slow-when-process-first-request">https://stackoverflow.com/questions/59242577/why-my-springboot-with-embbeded-tomcat-too-slow-when-process-first-request</a>
    */
    @Bean
    TomcatServletWebServerFactory tomcatFactory() {
        return new TomcatServletWebServerFactory() {
            @Override
            protected void postProcessContext(Context context) {
                context.setResources(new ExtractingRoot());
            }
        };
    }
}
