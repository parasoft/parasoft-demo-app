package com.parasoft.demoapp.controller;

import javax.servlet.http.HttpSession;

import com.parasoft.demoapp.service.CategoryService;
import com.parasoft.demoapp.service.GlobalPreferencesService;
import com.parasoft.demoapp.service.ItemService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.parasoft.demoapp.util.SessionUtil;
import com.parasoft.demoapp.model.global.RoleType;

@Controller
public class PageController {

	@Autowired
	private GlobalPreferencesService globalPreferencesService;

	@Autowired
	private ItemService itemService;
	
	@Autowired
	private CategoryService categoryService;
	
    @GetMapping("/")
    public String showHomePage(HttpSession httpSession, ModelMap modelMap){
    	String role = (String) httpSession.getAttribute(SessionUtil.FULL_ROLE_NAME_KEY);

		try {
			modelMap.addAttribute("industry", globalPreferencesService.getCurrentIndustry().getValue());
		} catch (Exception e) {
			e.printStackTrace();
			return "error/500";
		}

		if(RoleType.ROLE_PURCHASER.toString().equals(role)){
    		
    		return "purchaser";
    	}else if(RoleType.ROLE_APPROVER.toString().equals(role)) {
    		
    		return "approver";
    	}else {
    		
    		return "redirect:/loginPage";
    	}
    }
    
    @GetMapping("/demoAdmin")
	public String showDemoAdminPage(ModelMap modelMap) {

		try {
			modelMap.addAttribute("industry", globalPreferencesService.getCurrentIndustry().getValue());
		} catch (Exception e) {
			e.printStackTrace();
			return "error/500";
		}

		return "demoAdmin";
	}
    
    @GetMapping("/categories/{categoryId}")
	public String showItemsListPage(ModelMap modelMap, @PathVariable Long categoryId) {

    	try {
			modelMap.addAttribute("industry", globalPreferencesService.getCurrentIndustry().getValue());
			if(!categoryService.existsByCategoryId(categoryId)) {
				return "error/404";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "error/500";
		}

		return "category";
	}
    
	@GetMapping("/items/{itemId}")
	public String showItemDetailsPage(ModelMap modelMap, @PathVariable Long itemId) {

		try {
			modelMap.addAttribute("industry", globalPreferencesService.getCurrentIndustry().getValue());
			if(!itemService.existsByItemId(itemId)) {
				return "error/404";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "error/500";
		}

		return "item";
	}
	
	@GetMapping("/orderWizard")
	public String showOrderWizardPage(ModelMap modelMap) {

		try {
			modelMap.addAttribute("industry", globalPreferencesService.getCurrentIndustry().getValue());
		} catch (Exception e) {
			e.printStackTrace();
			return "error/500";
		}

		return "orderWizard";
	}

	@GetMapping("/orders")
	public String showOrdersPage(ModelMap modelMap) {

		try {
			modelMap.addAttribute("industry", globalPreferencesService.getCurrentIndustry().getValue());
			
		} catch (Exception e) {
			e.printStackTrace();
			return "error/500";
		}

		return "orders";
	}
	
	@GetMapping("/swagger-ui/index.html")
	public String showSwaggerUIPage() {

		return "swaggerUIIndex";
	}
}
