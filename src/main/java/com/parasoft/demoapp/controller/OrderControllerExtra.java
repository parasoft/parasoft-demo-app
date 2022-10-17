package com.parasoft.demoapp.controller;

import com.parasoft.demoapp.dto.UnreviewedOrderNumberResponseDTO;
import com.parasoft.demoapp.service.OrderServiceExtra;
import com.parasoft.demoapp.util.AuthenticationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Tag(name = "orders")
@Controller
@RequestMapping(value = {"/v1/orders", "/proxy/v1/orders"}, produces = MediaType.APPLICATION_JSON_VALUE)
public class OrderControllerExtra {

    @Autowired
    private OrderServiceExtra orderServiceExtra;

    @Operation(description = "Obtain the number of unreviewed order.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "The number of unreviewed order got successfully."),
            @ApiResponse(responseCode = "401",
                    description = "You are not authorized to get the unreviewed order number.",
                    content = {@Content(schema = @Schema(hidden = true)) }),
    })
    @GetMapping("/unreviewedNumber")
    @ResponseBody
    public ResponseResult<UnreviewedOrderNumberResponseDTO> unreviewedOrderNumber(Authentication auth) {

        ResponseResult<UnreviewedOrderNumberResponseDTO> response = ResponseResult.getInstance(ResponseResult.STATUS_OK,
                ResponseResult.MESSAGE_OK);

        String currentUsername = AuthenticationUtil.getUsernameInAuthentication(auth);
        UnreviewedOrderNumberResponseDTO result = orderServiceExtra.getUnreviewedOrderNumber(currentUsername);

        response.setData(result);

        return response;
    }
}
