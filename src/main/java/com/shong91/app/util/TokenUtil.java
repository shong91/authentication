package com.shong91.app.util;

import com.shong91.app.common.ResultCode;
import com.shong91.app.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenUtil {

  private final JwtProperties jwtProperties;

  public Key getKey() {
    return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.getSecret()));
  }

  /**
   * 토큰 발급 - Authentication 이 생성되었을 시
   *
   * <p>ex 회원 로그인
   *
   * @param authentication
   * @return
   */
  public String createToken(Authentication authentication) {
    Key key = getKey();
    User user = (User) authentication.getPrincipal();

    Map<String, Object> claims =
        Map.of("userId", user.getId(), "email", user.getEmail(), "username", user.getUsername());

    return Jwts.builder()
        .setClaims(claims)
        .setSubject(user.getEmail())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(
            new Date(System.currentTimeMillis() + jwtProperties.getExpirationSec() * 1000))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  public Claims parseClaims(String token) {
    try {
      return Jwts.parserBuilder()
          .setSigningKey(getKey().getEncoded())
          .build()
          .parseClaimsJws(token)
          .getBody();
    } catch (ExpiredJwtException e) {
      // 만료 시 재발행을 위해 return claims
      return e.getClaims();
    }
  }

  // 토큰을 받아 클레임을 만들고 권한정보를 빼서 시큐리티 유저객체를 만들어 Authentication 객체 반환
  public Authentication getAuthentication(String token) {
    Claims claims = parseClaims(token);

    User principal =
        User.builder()
            .id(Long.parseLong(String.valueOf(claims.get("userId"))))
            .email((String) claims.get("email"))
            .username((String) claims.get("username"))
            .build();

    return new UsernamePasswordAuthenticationToken(principal, token, principal.getAuthorities());
  }

  // 토큰 유효성 검사
  public ResultCode getTokenStatus(String token) {
    try {
      // 만료 시에 적절한 result code 를 보내주게 하기 위하여 parseClaims() 을 재사용하지 않음
      Jwts.parserBuilder()
          .setSigningKey(getKey().getEncoded())
          .build()
          .parseClaimsJws(token)
          .getBody();
      return ResultCode.OK;

    } catch (SecurityException | MalformedJwtException e) {
      return ResultCode.WRONG_TYPE_TOKEN;

    } catch (ExpiredJwtException e) {
      return ResultCode.EXPIRED_TOKEN;

    } catch (UnsupportedJwtException e) {
      return ResultCode.UNSUPPORTED_TOKEN;

    } catch (IllegalArgumentException e) {
      return ResultCode.ILLEGAL_ARGUMENT_TOKEN;

    } catch (Exception e) {
      return ResultCode.SERVER_ERROR;
    }
  }
}
