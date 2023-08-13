package com.shong91.app.service;

import com.shong91.app.controller.dto.AuthDto;
import com.shong91.app.controller.dto.AuthDto.Response;
import com.shong91.app.domain.User;
import com.shong91.app.util.TokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AuthService {

  private final TokenUtil tokenUtil;
  private final AuthenticationManagerBuilder authenticationManagerBuilder;

  /**
   * 사용자 로그인
   *
   * @param request
   * @return
   */
  public Response signIn(AuthDto.Request request) {
    // 1. create authentication (authenticated == false)
    UsernamePasswordAuthenticationToken authenticationToken =
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());

    // 2. authenticate (implements loadUserByUsername)
    Authentication authentication =
        authenticationManagerBuilder.getObject().authenticate(authenticationToken);
    User user = (User) authentication.getPrincipal();

    // 3. create tokens if authenticated
    String accessToken = tokenUtil.createToken(authentication);

    return new Response(user.toDto(), accessToken);
  }
}
