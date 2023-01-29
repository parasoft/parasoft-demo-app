package com.parasoft.demoapp.config.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CustomOidcUser extends CustomOAuth2User implements OidcUser {
    private final DefaultOidcUser defaultOidcUser;

    public CustomOidcUser(Collection<? extends GrantedAuthority> authorities, OidcIdToken idToken) {
        this(authorities, idToken, IdTokenClaimNames.SUB);
    }

    public CustomOidcUser(Collection<? extends GrantedAuthority> authorities, OidcIdToken idToken, String nameAttributeKey) {
        this(authorities, idToken, null, nameAttributeKey);
    }

    public CustomOidcUser(Collection<? extends GrantedAuthority> authorities, OidcIdToken idToken, OidcUserInfo userInfo) {
        this(authorities, idToken, userInfo, IdTokenClaimNames.SUB);
    }

    public CustomOidcUser(Collection<? extends GrantedAuthority> authorities, OidcIdToken idToken,
                          OidcUserInfo userInfo, String nameAttributeKey) {
        super(authorities, collectClaims(idToken, userInfo), nameAttributeKey);
        this.defaultOidcUser = new DefaultOidcUser(authorities, idToken, userInfo, nameAttributeKey);
    }

    @Override
    public Map<String, Object> getClaims() {
        return this.getAttributes();
    }

    @Override
    public OidcIdToken getIdToken() {
        return this.defaultOidcUser.getIdToken();
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return this.defaultOidcUser.getUserInfo();
    }

    static Map<String, Object> collectClaims(OidcIdToken idToken, OidcUserInfo userInfo) {
        Assert.notNull(idToken, "idToken cannot be null");
        Map<String, Object> claims = new HashMap<>();
        if (userInfo != null) {
            claims.putAll(userInfo.getClaims());
        }
        claims.putAll(idToken.getClaims());
        return claims;
    }
}
