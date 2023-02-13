package com.parasoft.demoapp.controller;

import com.parasoft.demoapp.dto.IdTokenDTO;
import com.parasoft.demoapp.exception.CannotLogoutFromKeycloakException;
import com.parasoft.demoapp.service.KeycloakService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
public class SwaggerUIController {
    @Autowired
    KeycloakService keycloakService;

    @GetMapping("/swagger-ui/index.html")
    public String showSwaggerUIPage() {

        return "swaggerUIIndex";
    }

    @Hidden
    @ResponseBody
    @PostMapping(value = {"/v1/swaggerOAuth2Logout"})
    public ResponseResult<Void> swaggerOAuth2Logout(@RequestBody IdTokenDTO idTokenDTO) throws CannotLogoutFromKeycloakException {
        keycloakService.oauth2Logout(idTokenDTO.getIdToken());
        return ResponseResult.getInstance(ResponseResult.STATUS_OK, ResponseResult.MESSAGE_OK);
    }
}
