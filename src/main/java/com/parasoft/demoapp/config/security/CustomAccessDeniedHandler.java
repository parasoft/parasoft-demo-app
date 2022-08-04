package com.parasoft.demoapp.config.security;

import com.parasoft.demoapp.controller.ResponseResult;
import com.parasoft.demoapp.messages.ConfigMessages;
import com.parasoft.demoapp.util.HttpServletResponseUtil;
import com.parasoft.demoapp.util.UrlUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse resp, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        if(!UrlUtil.isRestfulApiRequest(req)){
            resp.sendRedirect("/loginPage");
        }else{
            HttpServletResponseUtil.returnJsonErrorResponse(resp, HttpStatus.FORBIDDEN.value(),
                    ResponseResult.STATUS_ERR, ConfigMessages.USER_HAS_NO_PERMISSION, accessDeniedException.getMessage());
        }
    }
}
