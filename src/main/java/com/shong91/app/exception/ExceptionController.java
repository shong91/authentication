package com.shong91.app.exception;

import com.shong91.app.common.ResponseDto;
import com.shong91.app.common.ResultCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class ExceptionController {

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler({BindException.class, HttpMessageNotReadableException.class})
  public ResponseDto<String> handleMethodArgumentNotValidException(BindException exception) {
    BindingResult bindingResult = exception.getBindingResult();
    String message = bindingResult.getAllErrors().get(0).getDefaultMessage();
    return new ResponseDto<>(ResultCode.BAD_REQUEST, message);
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(Exception.class)
  public ResponseDto<String> handleException(Exception exception) {
    return new ResponseDto<>(ResultCode.SERVER_ERROR, exception.getMessage());
  }

  @ResponseStatus(HttpStatus.OK)
  @ExceptionHandler(CustomRuntimeException.class)
  public ResponseDto<String> handleCustomRuntimeException(CustomRuntimeException exception) {
    return new ResponseDto<>(exception.getErrCode(), exception.getMessage());
  }

  @ResponseStatus(HttpStatus.OK)
  @ExceptionHandler(AuthenticationException.class)
  public ResponseDto<String> handleAuthenticationException(
      WebRequest request, Exception exception) {
    return new ResponseDto<>(ResultCode.NOT_FOUND_USER);
  }
}
