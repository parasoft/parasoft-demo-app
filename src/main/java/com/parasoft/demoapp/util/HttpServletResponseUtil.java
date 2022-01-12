package com.parasoft.demoapp.util;

import com.google.gson.Gson;
import com.parasoft.demoapp.controller.ResponseResult;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class HttpServletResponseUtil {

    public static final String CHARSET_UTF8 = "UTF-8";
    public static final String CONTENT_TYPE_JSON= "application/json;charset=utf-8";

    public static <T> void returnJsonErrorResponse(HttpServletResponse resp, int status, int code, String message, T data) throws IOException {
        resp.setStatus(status);
        resp.setCharacterEncoding(CHARSET_UTF8);
        resp.setContentType(CONTENT_TYPE_JSON);

        try (PrintWriter writer = resp.getWriter()) {
            writer.write(generateResponseResultJsonString(code, message, data));
            writer.flush();
        }
    }

    public static <T> String generateResponseResultJsonString(int code, String message, T data){
        ResponseResult<T> responseBody = ResponseResult.getInstance(code, message, data);
        Gson gs = new Gson();
        return gs.toJson(responseBody);
    }
}
