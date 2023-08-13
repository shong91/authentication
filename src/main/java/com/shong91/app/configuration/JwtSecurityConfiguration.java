package com.shong91.app.configuration;

import com.shong91.app.filter.JwtFilter;
import com.shong91.app.util.TokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class JwtSecurityConfiguration
    extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

  private final TokenUtil tokenUtil;

  @Override
  public void configure(HttpSecurity httpSecurity) {
    JwtFilter filter = new JwtFilter(tokenUtil);
    httpSecurity.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
  }
}
