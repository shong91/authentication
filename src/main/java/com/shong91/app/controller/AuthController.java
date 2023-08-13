package com.shong91.app.controller;

import com.shong91.app.common.ResponseDto;
import com.shong91.app.common.ResultCode;
import com.shong91.app.constants.AuthConstants;
import com.shong91.app.controller.dto.AuthDto;
import com.shong91.app.controller.dto.AuthDto.Response;
import com.shong91.app.controller.dto.TokenDto;
import com.shong91.app.exception.CustomRuntimeException;
import com.shong91.app.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

  private final AuthService authService;

  @PostMapping("/auth/login")
  public ResponseDto<Response> signIn(@RequestBody @Valid AuthDto.Request request) {
    return new ResponseDto<>(ResultCode.OK, authService.signIn(request));
  }

  @PostMapping("/auth/reissue-token")
  public ResponseDto<TokenDto> reissueToken(HttpServletRequest servletRequest) {
    String accessToken = servletRequest.getHeader(HttpHeaders.AUTHORIZATION);
    String refreshToken = servletRequest.getHeader(AuthConstants.REFRESH_TOKEN);

    if (!StringUtils.hasText(accessToken) || !StringUtils.hasText(refreshToken)) {
      throw new CustomRuntimeException(ResultCode.ILLEGAL_ARGUMENT_TOKEN);
    }

    if (StringUtils.hasText(accessToken) && accessToken.startsWith("Bearer ")) {
      accessToken = accessToken.substring(7);
    }

    return new ResponseDto<>(ResultCode.OK, authService.reissueToken(accessToken, refreshToken));
  }
}
