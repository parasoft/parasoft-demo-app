package com.parasoft.demoapp.config.security;

import com.parasoft.demoapp.model.global.UserEntity;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@ToString
public class CustomOAuth2User extends UserEntity implements OAuth2User, Serializable {
    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;
    private final DefaultOAuth2User defaultOAuth2User;

    public CustomOAuth2User(Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes,
                            String nameAttributeKey) {
        this.defaultOAuth2User = new DefaultOAuth2User(authorities, attributes, nameAttributeKey);
    }

    @Override
    public String getName() {
        return this.defaultOAuth2User.getName();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.defaultOAuth2User.getAuthorities();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.defaultOAuth2User.getAttributes();
    }
}
