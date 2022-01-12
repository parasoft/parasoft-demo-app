package com.parasoft.demoapp.controller;

import javax.servlet.http.HttpSession;

import com.parasoft.demoapp.service.GlobalPreferencesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import com.parasoft.demoapp.util.SessionUtil;
import com.parasoft.demoapp.model.global.UserEntity;

@Controller
public class LoginController {

	@Autowired
	private GlobalPreferencesService globalPreferencesService;

	@GetMapping("/loginPage")
	public String showLoginPage(HttpSession session, ModelMap modelMap) {

		try {
			modelMap.addAttribute("industry", globalPreferencesService.getCurrentIndustry().getValue());
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}

		// If a user has logged in, the page will jump to home page directly. 
		//if not, jumping to login page
		UserEntity user = SessionUtil.getUserEntityInSession(session);
		if (user != null) {
			return "redirect:/";
		}
		
		return "login";
	}
}
