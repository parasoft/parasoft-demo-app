package com.parasoft.demoapp.controller;

import com.parasoft.demoapp.model.global.RoleType;
import com.parasoft.demoapp.service.CategoryService;
import com.parasoft.demoapp.service.GlobalPreferencesService;
import com.parasoft.demoapp.service.ItemService;
import com.parasoft.demoapp.util.AuthenticationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.text.MessageFormat;

@Slf4j
@Controller
public class PageController {

    @Autowired
    private GlobalPreferencesService globalPreferencesService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/")
    public String showHomePage(Authentication authentication, ModelMap modelMap){
        String role = AuthenticationUtil.getUserRoleNameInAuthentication(authentication);

        try {
            modelMap.addAttribute("industry", globalPreferencesService.getCurrentIndustry().getValue());
            modelMap.addAttribute("currentWebServiceMode", globalPreferencesService.getCurrentGlobalPreferences().getWebServiceMode().getValue());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "error/500";
        }

        if(RoleType.ROLE_PURCHASER.toString().equals(role)){
            return "purchaser";
        }else if(RoleType.ROLE_APPROVER.toString().equals(role)) {
            return "approver";
        }

        log.error(MessageFormat.format("Unsupported role: {0}", role));

        return "error/500";
    }

    @GetMapping("/demoAdmin")
    public String showDemoAdminPage(ModelMap modelMap) {

        try {
            modelMap.addAttribute("industry", globalPreferencesService.getCurrentIndustry().getValue());
            modelMap.addAttribute("currentWebServiceMode", globalPreferencesService.getCurrentGlobalPreferences().getWebServiceMode().getValue());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "error/500";
        }

        return "demoAdmin";
    }

    @GetMapping("/categories/{categoryId}")
    public String showItemsListPage(ModelMap modelMap, @PathVariable Long categoryId) {

        try {
            modelMap.addAttribute("industry", globalPreferencesService.getCurrentIndustry().getValue());
            modelMap.addAttribute("currentWebServiceMode", globalPreferencesService.getCurrentGlobalPreferences().getWebServiceMode().getValue());
            if(!categoryService.existsByCategoryId(categoryId)) {
                return "error/404";
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "error/500";
        }

        return "category";
    }

    @GetMapping("/items/{itemId}")
    public String showItemDetailsPage(ModelMap modelMap, @PathVariable Long itemId) {

        try {
            modelMap.addAttribute("industry", globalPreferencesService.getCurrentIndustry().getValue());
            modelMap.addAttribute("currentWebServiceMode", globalPreferencesService.getCurrentGlobalPreferences().getWebServiceMode().getValue());
            if(!itemService.existsByItemId(itemId)) {
                return "error/404";
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "error/500";
        }

        return "item";
    }

    @GetMapping("/orderWizard")
    public String showOrderWizardPage(ModelMap modelMap) {

        try {
            modelMap.addAttribute("industry", globalPreferencesService.getCurrentIndustry().getValue());
            modelMap.addAttribute("currentWebServiceMode", globalPreferencesService.getCurrentGlobalPreferences().getWebServiceMode().getValue());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "error/500";
        }

        return "orderWizard";
    }

    @GetMapping("/orders")
    public String showOrdersPage(ModelMap modelMap) {

        try {
            modelMap.addAttribute("industry", globalPreferencesService.getCurrentIndustry().getValue());
            modelMap.addAttribute("currentWebServiceMode", globalPreferencesService.getCurrentGlobalPreferences().getWebServiceMode().getValue());

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "error/500";
        }

        return "orders";
    }

    @GetMapping("/accessDenied")
    public String showAccessDeniedPage(ModelMap modelMap, String type) {
        try {
            modelMap.addAttribute("errorType", type);
            modelMap.addAttribute("currentWebServiceMode", globalPreferencesService.getCurrentGlobalPreferences().getWebServiceMode().getValue());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return "error/403";
    }

    @GetMapping("/unauthorized")
    public String showUnauthorizedPage(ModelMap modelMap, String type) {
        try {
            modelMap.addAttribute("errorType", type);
            modelMap.addAttribute("currentWebServiceMode", globalPreferencesService.getCurrentGlobalPreferences().getWebServiceMode().getValue());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return "error/401";
    }
}
