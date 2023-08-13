package com.shong91.app.util;

import com.shong91.app.domain.User;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Getter
@Builder
public class TokenUser {

  private String userId;
  private String email;
  private String username;

  /**
   * Security Context 에 저장해둔 인증 정보로 로그인 유저 정보를 가져온다.
   */
  public static long getId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    // null or anonymousUser
    if (authentication == null || authentication.getPrincipal() instanceof String) {
      return -1;
    }

    User user = (User) authentication.getPrincipal();
    return user.getId();
  }

  public static User getUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    // null or anonymousUser
    if (authentication == null || authentication.getPrincipal() instanceof String) {
      return null;
    }

    User user = (User) authentication.getPrincipal();

    return user;
  }
}
