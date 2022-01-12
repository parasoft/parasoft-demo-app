package com.parasoft.demoapp.controller;

import io.swagger.v3.oas.annotations.media.Schema;

public class ResponseResult<T> {

  public static final int STATUS_ERR = 0;
  public static final int STATUS_OK = 1;
  public static final int STATUS_WARN = 2;

  public static final String MESSAGE_ERR = "error";
  public static final String MESSAGE_OK = "success";
  public static final String MESSAGE_WARN = "warn";

  @Schema(description = "The status of response data, 0 - error, 1 - success, 2 - warn.")
  private Integer status;
  @Schema(description = "'error', 'success', 'warn' or exception message.")
  private String message;
  @Schema(description = "The response data or null if no data needs to be responded.")
  private T data;

  public ResponseResult() {
    super();
  }

  private ResponseResult(Integer status) {
    this.status = status;
  }

  private ResponseResult(Integer status, String message) {
    this(status);
    this.message = message;
  }

  private ResponseResult(Integer status, String message, T data) {
    this(status, message);
    this.data = data;
  }

  private ResponseResult(Throwable throwable) {
    this(STATUS_ERR, throwable.getMessage());
  }

  public static <T> ResponseResult<T> getInstance(Integer status) {
    return new ResponseResult<T>(status);
  }

  public static <T> ResponseResult<T> getInstance(Integer status, String message) {
    return new ResponseResult<T>(status, message);
  }

  public static <T> ResponseResult<T> getInstance(Integer status, String message, T data) {
    return new ResponseResult<T>(status, message, data);
  }

  public static <T> ResponseResult<T> getInstance(Throwable throwable) {
    return new ResponseResult<T>(throwable);
  }

  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }

}


