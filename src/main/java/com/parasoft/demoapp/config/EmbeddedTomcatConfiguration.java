package com.parasoft.demoapp.config;

import org.apache.catalina.Context;
import org.apache.catalina.webresources.ExtractingRoot;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
// The factory is packaged in WEB-INF/lib-provided for executable WARs. Do not
// inspect this embedded-server-only configuration when running in external
// Tomcat, where that directory is intentionally excluded from the webapp.
@ConditionalOnClass(name = "org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory")
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
