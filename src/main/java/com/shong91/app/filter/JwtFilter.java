package com.shong91.app.filter;

import com.shong91.app.util.TokenUtil;
import com.shong91.app.common.ResultCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtFilter extends GenericFilterBean {
  private final TokenUtil tokenUtil;

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
      throws IOException, ServletException {
    // 1. Request Header 에서 JWT 토큰 추출
    HttpServletRequest httpServletRequest = (HttpServletRequest) request;
    String token = resolveToken(httpServletRequest);
    String requestURI = httpServletRequest.getRequestURI();

    // 유효성 검증
    ResultCode tokenStatus = tokenUtil.getTokenStatus(token);
    if (StringUtils.hasText(token) && ResultCode.OK.equals(tokenStatus)) {
      // save authentication on security context
      Authentication authentication = tokenUtil.getAuthentication(token);
      SecurityContextHolder.getContext().setAuthentication(authentication);
    } else {
      request.setAttribute("exception", tokenStatus);
    }

    filterChain.doFilter(request, response);
  }

  // 헤더에서 토큰 정보를 꺼내온다.
  private String resolveToken(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }
}
