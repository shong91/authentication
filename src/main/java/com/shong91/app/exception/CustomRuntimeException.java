package com.shong91.app.exception;

import com.shong91.app.common.ResultCode;

public class CustomRuntimeException extends RuntimeException {

  private static final long serialVersionUID = 4040068008772917150L;
  private final ResultCode resultCode;

  public CustomRuntimeException(ResultCode resultCode) {
    super();
    this.resultCode = resultCode;
  }

  public CustomRuntimeException(ResultCode resultCode, String message) {
    super(message);
    this.resultCode = resultCode;
  }

  public CustomRuntimeException(ResultCode resultCode, String message, Throwable t) {
    super(message, t);
    this.resultCode = resultCode;
  }

  public ResultCode getErrCode() {
    return resultCode;
  }
}
