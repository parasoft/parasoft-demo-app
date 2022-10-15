package com.parasoft.demoapp.controller;

import com.parasoft.demoapp.exception.LocalizationException;
import com.parasoft.demoapp.exception.ParameterException;
import com.parasoft.demoapp.exception.ResourceNotFoundException;
import com.parasoft.demoapp.model.global.LocalizationLanguageType;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.parasoft.demoapp.service.LocalizationService;

@Hidden
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

	@GetMapping("/v1/localize/{lang}/{key}")
	@ResponseBody
	public ResponseResult<String> getLocalization(@PathVariable("key") String key,
												  @PathVariable(value = "lang") LocalizationLanguageType languageType)
												 throws LocalizationException, ParameterException, ResourceNotFoundException {

		ResponseResult<String> response =
				ResponseResult.getInstance(ResponseResult.STATUS_OK, ResponseResult.MESSAGE_OK);

		String localizedValue = localizationService.getLocalization(key, languageType);
		response.setData(localizedValue);

		return response;
	}
}
