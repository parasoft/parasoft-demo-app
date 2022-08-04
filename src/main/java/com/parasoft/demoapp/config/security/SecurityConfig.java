package com.parasoft.demoapp.config.security;

import com.parasoft.demoapp.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Autowired
    private CustomLogoutSuccessHandler customLogoutSuccessHandler;


    @Configuration
    @Order(1)
    public class ApiWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(customUserDetailsService)
                    .passwordEncoder(passwordEncoder);
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER);

            // Some considerations on the REST API URL pattern:
            // Change the URL of REST API to "/api/**" pattern, so it can support multiple version of the API,
            // like "/api/v1/**" and "/api/v2/**".
            // And we can configure REST API security with a single `antMatcher("/api/**")`
            http
                .antMatcher("/v1/**")
                .authorizeRequests()
                    .antMatchers(HttpMethod.GET, "/v1/demoAdmin/**").permitAll()
                    .antMatchers("/v1/demoAdmin/**").authenticated()
                    .antMatchers(HttpMethod.GET, "/v1/assets/**", "/proxy/v1/assets/**").permitAll()
                    .antMatchers("/v1/assets/**", "/proxy/v1/assets/**").authenticated()
                    .antMatchers("/v1/cartItems/**", "/proxy/v1/cartItems/**").access("hasRole('PURCHASER')")
                    .antMatchers("/v1/locations/**", "/proxy/v1/locations/**").authenticated()
                    .antMatchers(HttpMethod.POST, "/v1/orders/**", "/proxy/v1/orders/**").access("hasRole('PURCHASER')")
                    .antMatchers("/v1/orders/**", "/proxy/v1/orders/**").authenticated()
                    .antMatchers("/v1/images").authenticated()
                    .antMatchers(HttpMethod.GET, "/v1/labels").permitAll()
                    .antMatchers("/v1/labels").authenticated()
                    .antMatchers("/v1/**", "/proxy/v1/**").permitAll()
                 .and()
                    .httpBasic()
                        .realmName("Parasoft Demo App")
                 .and()
                    .csrf()
                        .disable();

            http.exceptionHandling()
                    .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                    .accessDeniedHandler((new CustomAccessDeniedHandler()));
        }
    }

    @Configuration
    public class FormLoginWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(customUserDetailsService)
                    .passwordEncoder(passwordEncoder);
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);

            http
                .authorizeRequests()
                    .antMatchers("/").authenticated()
                    .antMatchers("/demoAdmin").authenticated()
                    .antMatchers("/categories/**").access("hasRole('PURCHASER')")
                    .antMatchers("/items/**").access("hasRole('PURCHASER')")
                    .antMatchers("/orderWizard").access("hasRole('PURCHASER')")
                    .antMatchers("/orders").access("hasRole('PURCHASER')")
                    .antMatchers("/actuator/routes/**").authenticated()
                    .antMatchers("/**").permitAll()
                .and()
                    .formLogin()
                        .loginPage("/loginPage")
                        .loginProcessingUrl("/login")
                        .successHandler(customAuthenticationSuccessHandler)
                .and()
                    .logout()
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                        .logoutSuccessHandler(customLogoutSuccessHandler)
                .and()
                    .csrf()
                        .disable();

            http.exceptionHandling()
                    .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                    .accessDeniedHandler(new CustomAccessDeniedHandler());
        }
    }

}
