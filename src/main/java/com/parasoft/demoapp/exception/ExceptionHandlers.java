package com.parasoft.demoapp.exception;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.parasoft.demoapp.controller.ResponseResult;

@ControllerAdvice
@Slf4j
public class ExceptionHandlers {

    @Hidden
    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ResponseResult<Void> exceptionHandler(Exception e){
        if(log.isErrorEnabled()){
            log.error(e.getMessage(), e);
        }

        return ResponseResult.getInstance(ResponseResult.STATUS_ERR, e.getMessage());
    }

    @Hidden
    @ExceptionHandler(value = ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ResponseResult<Void> resourceNotFoundExceptionHandler(Exception e){
        if(log.isErrorEnabled()){
            log.error(e.getMessage(), e);
        }

        return ResponseResult.getInstance(ResponseResult.STATUS_ERR, e.getMessage());
    }

    @Hidden
    @ExceptionHandler(value = ResourceExistsAlreadyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseResult<Void> resourceExistsAlreadyExceptionHandler(Exception e){
        if(log.isErrorEnabled()){
            log.error(e.getMessage(), e);
        }

        return ResponseResult.getInstance(ResponseResult.STATUS_ERR, e.getMessage());
    }

    @Hidden
    @ExceptionHandler(value = ParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseResult<Void> parameterExceptionHandler(Exception e){
        if(log.isErrorEnabled()){
            log.error(e.getMessage(), e);
        }

        return ResponseResult.getInstance(ResponseResult.STATUS_ERR, e.getMessage());
    }

    @Hidden
    @ExceptionHandler(value = CategoryHasAtLeastOneItemException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseResult<Void> categoryHasAtLeastOneItemExceptionHandler(Exception e){
        if(log.isErrorEnabled()){
            log.error(e.getMessage(), e);
        }

        return ResponseResult.getInstance(ResponseResult.STATUS_ERR, e.getMessage());
    }

    @Hidden
    @ExceptionHandler(value = UnsupportedOperationInCurrentIndustryException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseResult<Void> unsupportedOperationInCurrentIndustryExceptionHandler(Exception e){
        if(log.isErrorEnabled()){
            log.error(e.getMessage(), e);
        }

        return ResponseResult.getInstance(ResponseResult.STATUS_ERR, e.getMessage());
    }
    
    @Hidden
    @ExceptionHandler(value = NoPermissionException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public ResponseResult<Void> noPermissionExceptionHandler(Exception e){
        if(log.isErrorEnabled()){
            log.error(e.getMessage(), e);
        }

        return ResponseResult.getInstance(ResponseResult.STATUS_ERR, e.getMessage());
    }
    
    @Hidden
    @ExceptionHandler(value = IncorrectOperationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseResult<Void> incorrectOperationExceptionHandler(Exception e){
        if(log.isErrorEnabled()){
            log.error(e.getMessage(), e);
        }

        return ResponseResult.getInstance(ResponseResult.STATUS_ERR, e.getMessage());
    }

    @Hidden
    @ExceptionHandler(value = EndpointInvalidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseResult<Void> endpointInvalidExceptionHandler(Exception e){
        if(log.isErrorEnabled()){
            log.error(e.getMessage(), e);
        }

        return ResponseResult.getInstance(ResponseResult.STATUS_ERR, e.getMessage());
    }

    @Hidden
    @ExceptionHandler(value = VirtualizeServerUrlException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseResult<Void> virtualizeServerUrlExceptionHandler(Exception e){
        if(log.isErrorEnabled()){
            log.error(e.getMessage(), e);
        }

        return ResponseResult.getInstance(ResponseResult.STATUS_ERR, e.getMessage());
    }

}
