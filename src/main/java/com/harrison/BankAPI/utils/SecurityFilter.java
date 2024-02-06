package com.harrison.BankAPI.utils;

import com.harrison.BankAPI.service.PersonService;
import com.harrison.BankAPI.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class SecurityFilter extends OncePerRequestFilter {

  private final PersonService personService;

  private final TokenService tokenService;

  @Autowired
  public SecurityFilter(PersonService personService, TokenService tokenService) {
    this.personService = personService;
    this.tokenService = tokenService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    String token = recoveryToken(request);
    if (token != null) {
      String subject = tokenService.validateToken(token);
      UserDetails userDetails = personService.loadUserByUsername(subject);
      UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
          userDetails, null, userDetails.getAuthorities());
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    filterChain.doFilter(request, response);
  }

  private String recoveryToken(HttpServletRequest request) {
    String autoHeader = request.getHeader("Authorization");
    if (autoHeader == null) {
      return null;
    }
    return autoHeader.replace("Bearer ", "");
  }
}
