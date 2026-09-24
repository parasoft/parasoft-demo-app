package com.parasoft.demoapp.config.security;

import com.parasoft.demoapp.model.global.RoleType;
import com.parasoft.demoapp.model.global.UserEntity;
import com.parasoft.demoapp.service.CustomUserDetailsService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.util.CastUtils;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import com.parasoft.demoapp.exception.RoleNotMatchException;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;

import jakarta.annotation.Nullable;
import java.util.*;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String USER_REALM_ROLE_MAPPER_NAME = "pda-realm-role";
    private static final String ROLE_PREFIX = "ROLE_";

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Autowired
    private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    @Autowired
    private CustomOAuth2AuthenticationFailureHandler customOAuth2AuthenticationFailureHandler;

    @Autowired
    private CustomLogoutSuccessHandler customLogoutSuccessHandler;

    @Value("${spring.security.oauth2.client.provider.keycloak.jwk-set-uri}")
    private String keycloakJwkSetUri;

    @Value("${spring.security.oauth2.client.provider.keycloak.user-name-attribute}")
    private String keycloakUsernameAttribute;

    @Bean
    DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    @Order(1)
    SecurityFilterChain apiSecurityFilterChain(HttpSecurity http,
                                               DaoAuthenticationProvider daoAuthenticationProvider) throws Exception {
        // Include all '/v1/**' URLs except '/v1/login' and '/v1/logout'.
        http.securityMatcher(new RegexRequestMatcher("/v1/(?!(login$|logout$)).*", null))
                .authenticationProvider(daoAuthenticationProvider)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.NEVER))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.GET, "/v1/demoAdmin/**").permitAll()
                        .requestMatchers("/v1/demoAdmin/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/v1/assets/**").permitAll()
                        .requestMatchers("/v1/assets/**").authenticated()
                        .requestMatchers("/v1/cartItems/**").hasRole("PURCHASER")
                        .requestMatchers("/v1/locations/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/v1/orders/**").hasRole("PURCHASER")
                        .requestMatchers("/v1/orders/**").authenticated()
                        .requestMatchers("/v1/images").authenticated()
                        .requestMatchers(HttpMethod.GET, "/v1/labels").permitAll()
                        .requestMatchers("/v1/labels").authenticated()
                        .requestMatchers("/v1/**").permitAll())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(new CustomAuthenticationConverter()))
                        .authenticationEntryPoint(new CustomBearerTokenAuthenticationEntryPoint()))
                .httpBasic(basic -> basic
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                        .realmName("Parasoft Demo App"))
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                        .accessDeniedHandler(new CustomAccessDeniedHandler()));
        return http.build();
    }

    @Bean
    SecurityFilterChain formLoginSecurityFilterChain(HttpSecurity http,
                                                     DaoAuthenticationProvider daoAuthenticationProvider) throws Exception {
        http.authenticationProvider(daoAuthenticationProvider)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/").authenticated()
                        .requestMatchers("/demoAdmin").authenticated()
                        .requestMatchers("/categories/**").hasRole("PURCHASER")
                        .requestMatchers("/items/**").hasRole("PURCHASER")
                        .requestMatchers("/orderWizard").hasRole("PURCHASER")
                        .requestMatchers("/orders").hasRole("PURCHASER")
                        .requestMatchers("/actuator/routes/**").authenticated()
                        .requestMatchers("/**").permitAll())
                .formLogin(form -> form
                        .loginPage("/loginPage")
                        .loginProcessingUrl("/v1/login")
                        .failureHandler(customAuthenticationFailureHandler)
                        .successHandler(customAuthenticationSuccessHandler))
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/v1/logout", "GET"))
                        .logoutSuccessHandler(customLogoutSuccessHandler))
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/loginPage")
                        .userInfoEndpoint(userInfo -> userInfo.oidcUserService(this.oidcUserService()))
                        .failureHandler(customOAuth2AuthenticationFailureHandler))
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                        .accessDeniedHandler(new CustomAccessDeniedHandler()));
        return http.build();
    }

    private OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
        final OidcUserService delegate = new OidcUserService();

        return (userRequest) -> {
            OidcUser oidcUser = delegate.loadUser(userRequest);
            OidcUserInfo userInfo = oidcUser.getUserInfo();
            OidcIdToken idToken = oidcUser.getIdToken();

            UserEntity userEntity;
            try {
                userEntity = (UserEntity) customUserDetailsService
                        .loadUserByUsername(userInfo.getPreferredUsername());
            } catch (UsernameNotFoundException exception) {
                // Customize the exception to passing tokens to ensure that we can remove the session in keycloak when the login fails
                String idTokenHint = idToken != null ? idToken.getTokenValue() : null;
                throw new UsernameNotFoundException(idTokenHint, exception);
            }
            CustomOidcUser customOidcUser =
                    new CustomOidcUser(mapAuthoritiesToOidcUserAuthorityType(userEntity, userInfo, idToken), idToken, userInfo);
            customOidcUser.setId(userEntity.getId());
            customOidcUser.setUsername(userEntity.getUsername());
            customOidcUser.setRole(userEntity.getRole());
            return customOidcUser;
        };
    }

    @Bean
    JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withJwkSetUri(keycloakJwkSetUri).build();
    }

    class CustomAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {
        public AbstractAuthenticationToken convert(Jwt jwt) {
            UserEntity userEntity = (UserEntity) customUserDetailsService
                    .loadUserByUsername((String) jwt.getClaims().get(keycloakUsernameAttribute));

            return new JwtAuthenticationToken(new CustomJwt(jwt, userEntity), mapAuthorityForOAuth2UserAuthorityType(jwt.getClaims(), userEntity));
        }
    }

    public static class CustomJwt extends Jwt {

        @Getter
        private final UserEntity userInfo;

        public CustomJwt(Jwt jwt, UserEntity userInfo) {
            super(jwt.getTokenValue(), jwt.getIssuedAt(), jwt.getExpiresAt(), jwt.getHeaders(), jwt.getClaims());
            this.userInfo = userInfo;
        }
    }

    private Set<GrantedAuthority> mapAuthoritiesToOidcUserAuthorityType(UserEntity userEntity, OidcUserInfo userInfo, OidcIdToken idToken) {
        return mapAuthorities(userInfo.getClaims(), userEntity, OidcUserAuthority.class, userInfo, idToken);
    }

    private Set<GrantedAuthority> mapAuthorityForOAuth2UserAuthorityType(Map<String, Object> claims, UserEntity userEntity) {
        return mapAuthorities(claims, userEntity, OAuth2UserAuthority.class, null, null);
    }

    private Set<GrantedAuthority> mapAuthorities(Map<String, Object> claims,
                                                 UserEntity userEntity,
                                                 Class<? extends GrantedAuthority> grantedAuthorityType,
                                                 @Nullable OidcUserInfo userInfo,
                                                 @Nullable OidcIdToken idToken) {
        Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
        Object realmRoleClaim = claims.get(USER_REALM_ROLE_MAPPER_NAME);
        if (realmRoleClaim instanceof List<?>) {
            List<String> realmRoles = CastUtils.cast(realmRoleClaim);
            userEntity.getAuthorities().forEach(grantedAuthority -> {
                // Role matching related
                String grantedRoleType = grantedAuthority.getAuthority();
                if ((!realmRoles.contains(grantedRoleType.substring(5)))
                        || (RoleType.ROLE_PURCHASER.name().equals(grantedRoleType) && realmRoles.contains("APPROVER"))
                        || (RoleType.ROLE_APPROVER.name().equals(grantedRoleType) && realmRoles.contains("PURCHASER"))
                ) {
                    String idTokenHint = idToken != null ? idToken.getTokenValue() : null;
                    throw new RoleNotMatchException(idTokenHint);
                }

                realmRoles.forEach(realmRole -> {
                    String authority = grantedAuthority.getAuthority();
                    if (authority.contentEquals(ROLE_PREFIX + realmRole)) {
                        GrantedAuthority mappedAuthority = null;
                        if (grantedAuthorityType.equals(OidcUserAuthority.class)) {
                            mappedAuthority = new OidcUserAuthority(authority, Objects.requireNonNull(idToken), Objects.requireNonNull(userInfo));
                        } else if (grantedAuthorityType.equals(OAuth2UserAuthority.class)) {
                            mappedAuthority = new OAuth2UserAuthority(ROLE_PREFIX + realmRole, claims);
                        }

                        if (mappedAuthority != null) {
                            mappedAuthorities.add(mappedAuthority);
                        }
                    }
                });
            });
        }
        return mappedAuthorities;
    }
}
