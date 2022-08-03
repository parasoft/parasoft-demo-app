package com.parasoft.demoapp.controller;

import com.parasoft.demoapp.dto.ForgotPasswordResponseDTO;
import com.parasoft.demoapp.exception.UserNotFoundException;
import com.parasoft.demoapp.model.global.RoleType;
import com.parasoft.demoapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping(value = {"/forgotPassword"}, produces = MediaType.APPLICATION_JSON_VALUE)
public class ForgotPasswordController {

    @Autowired
    UserService userService;

    @GetMapping
    @ResponseBody
    public ResponseResult<List<ForgotPasswordResponseDTO>> forgotPassword() {

        ForgotPasswordResponseDTO purchaser = null;
        ForgotPasswordResponseDTO approver = null;
        try {
            purchaser = ForgotPasswordResponseDTO.getInstanceFrom(userService.getFirstUserByRoleName(RoleType.ROLE_PURCHASER.name()));
        } catch (UserNotFoundException e) {
            purchaser = ForgotPasswordResponseDTO.getInstance(RoleType.ROLE_PURCHASER.name());
        }

        try {
            approver = ForgotPasswordResponseDTO.getInstanceFrom(userService.getFirstUserByRoleName(RoleType.ROLE_APPROVER.name()));
        } catch (UserNotFoundException e) {
            approver = ForgotPasswordResponseDTO.getInstance(RoleType.ROLE_APPROVER.name());
        }

        List<ForgotPasswordResponseDTO> users = new ArrayList<ForgotPasswordResponseDTO>();
        users.add(purchaser);
        users.add(approver);
        ResponseResult<List<ForgotPasswordResponseDTO>> response = ResponseResult.getInstance(ResponseResult.STATUS_OK, ResponseResult.MESSAGE_OK);
        response.setData(users);

        return response;
    }
}
