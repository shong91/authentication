package com.shong91.app.service;

import com.shong91.app.common.ResultCode;
import com.shong91.app.controller.dto.AuthDto;
import com.shong91.app.controller.dto.AuthDto.Response;
import com.shong91.app.controller.dto.TokenDto;
import com.shong91.app.domain.User;
import com.shong91.app.exception.CustomRuntimeException;
import com.shong91.app.util.JwtProperties;
import com.shong91.app.util.TokenUtil;
import io.jsonwebtoken.Claims;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.http.HttpHeaders;
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
  private final RedisTemplate redisTemplate;
  private final JwtProperties jwtProperties;

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
    String accessToken = tokenUtil.createToken(authentication, jwtProperties.getExpirationSec());
    String refreshToken = tokenUtil.createToken(authentication,
        jwtProperties.getExpirationRedisSec());

    // 4. save refresh token in redis: 레디스 만료 - 2주
    ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
    valueOperations.set(String.valueOf(user.getId()), refreshToken,
        Duration.ofSeconds(jwtProperties.getExpirationRedisSec()));

    return new Response(user.toDto(), new TokenDto(accessToken, refreshToken));
  }

  /**
   * 토큰 재발급
   *
   * @param accessToken
   * @param refreshToken
   * @return
   */
  public TokenDto reissueToken(String accessToken, String refreshToken) {
    // 1. validate access token
    ResultCode tokenStatus = tokenUtil.getTokenStatus(accessToken);

    // 토큰이 만료되어 요청을 보낸 것이 아니라면 throw exception
    if (!ResultCode.EXPIRED_TOKEN.equals(tokenStatus)) {
      throw new CustomRuntimeException(tokenStatus);
    }

    // 2. validate refresh token
    int userId = (int) tokenUtil.parseClaims(accessToken).get("userId");
    ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
    String redisRefreshToken = valueOperations.get(String.valueOf(userId));

    // refresh token 이 만료되었다면(get from redis == null) 로그아웃 처리
    if (!refreshToken.equals(redisRefreshToken)) {
      throw new CustomRuntimeException(ResultCode.EXPIRED_TOKEN);
    }

    // 3. create new access token if valid
    Authentication authentication = tokenUtil.getAuthentication(accessToken);
    String newAccessToken = tokenUtil.createToken(authentication, jwtProperties.getExpirationSec());

    // 4. response new access token
    return new TokenDto(newAccessToken, refreshToken);
  }

  /**
   * 사용자 로그아웃
   *
   * @param accessToken
   * @return
   */
  public int signOut(String accessToken) {
    // 1. validate access token
    if (hasKeyBlackList(accessToken)) {
      throw new CustomRuntimeException(ResultCode.EXPIRED_TOKEN);
    }

    // 2. get claims
    Claims claims = tokenUtil.parseClaims(accessToken);
    int userId = (int) claims.get("userId");

    // 3. delete refresh token in Redis => refresh token 무효화
    redisTemplate.delete(String.valueOf(userId));

    // 4. add access token in blacklist => access token 무효화
    long expiration = claims.getExpiration().getTime();
    setBlackList(accessToken, HttpHeaders.AUTHORIZATION, expiration);

    return userId;
  }


  public void setBlackList(String key, Object object, Long milliSeconds) {
    redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer(object.getClass()));
    redisTemplate.opsForValue().set(key, object, milliSeconds, TimeUnit.MILLISECONDS);
  }

  public boolean hasKeyBlackList(String token) {
    return redisTemplate.hasKey(token);
  }
}
