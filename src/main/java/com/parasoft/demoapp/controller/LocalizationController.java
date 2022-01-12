package com.parasoft.demoapp.controller;

import com.parasoft.demoapp.exception.LocalizationException;
import com.parasoft.demoapp.exception.ParameterException;
import com.parasoft.demoapp.model.global.LocalizationLanguageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.parasoft.demoapp.service.LocalizationService;

@Controller
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class LocalizationController {
	@Autowired
	private LocalizationService localizationService;
	
	@GetMapping("/localize")
	@ResponseBody
	public ResponseResult<String> getLocalization(@RequestParam(value = "lang") LocalizationLanguageType languageType)
			throws LocalizationException, ParameterException {

		ResponseResult<String> response =
				ResponseResult.getInstance(ResponseResult.STATUS_OK, ResponseResult.MESSAGE_OK);

		String localization = localizationService.getLocalization(languageType);
		response.setData(localization);

		return response;
	}

}
