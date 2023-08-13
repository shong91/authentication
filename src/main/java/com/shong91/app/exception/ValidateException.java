package com.shong91.app.exception;

import com.shong91.app.common.ResultCode;

public class ValidateException extends RuntimeException {

  private String message;
  private ResultCode code;

  public ValidateException(final ResultCode code) {
    super(code.getDescription());
    this.message = code.getDescription();
    this.code = code;
  }

  @Override
  public String getMessage() {
    return this.message;
  }

  public ResultCode getCode() {
    return this.code;
  }
}
