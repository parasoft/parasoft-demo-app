package com.parasoft.demoapp.controller;

import com.parasoft.demoapp.service.GlobalPreferencesService;
import com.parasoft.demoapp.util.UrlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@Controller
public class LoginController {

    @Autowired
    private GlobalPreferencesService globalPreferencesService;

    @GetMapping("/loginPage")
    public String showLoginPage(Authentication authentication, ModelMap modelMap) {

        try {
            modelMap.addAttribute("industry", globalPreferencesService.getCurrentIndustry().getValue());
            modelMap.addAttribute("currentWebServiceMode", globalPreferencesService.getCurrentGlobalPreferences().getWebServiceMode().getValue());
        } catch (Exception e) {
            e.printStackTrace();
            return "error/500";
        }

        if (authentication != null) {
            return "redirect:/";
        }

        return "login";
    }

    @GetMapping("/keycloak")
    public String validateKeycloakServerUrl() throws IOException {
        String keycloakServerUrl = "/oauth2/authorization/keycloak";
        try {
            UrlUtil.validateUrl("http://localhost:8080"+ keycloakServerUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return "error/500";
        }
        return "redirect:" + keycloakServerUrl;
    }
}
