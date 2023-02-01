package com.parasoft.demoapp.controller;

import com.parasoft.demoapp.service.GlobalPreferencesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

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
}
