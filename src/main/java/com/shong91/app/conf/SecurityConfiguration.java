package com.shong91.app.conf;

import com.shong91.app.filter.JwtAccessDeniedHandler;
import com.shong91.app.filter.JwtAuthenticationEntryPoint;
import com.shong91.app.filter.JwtFilter;
import com.shong91.app.util.TokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfiguration {

  private final TokenUtil tokenUtil;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.httpBasic()
        .disable()
        .csrf()
        .disable() // REST API: basic auth, csrf 설정 하지 않음
        .cors() // cors -> cors.disable()
        .and()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // JWT 사용: 세션 사용하지 않음
        .and()
        .authorizeRequests()
        /* permit all; swagger resources */
        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-resources/**")
        .permitAll()
        .requestMatchers("/api/v1/auth/login")
        .permitAll()
        .anyRequest()
        .authenticated()
        .and()
        .exceptionHandling()
        .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
        .accessDeniedHandler(new JwtAccessDeniedHandler())
        .and()
        .addFilterBefore(new JwtFilter(tokenUtil), UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }
}
