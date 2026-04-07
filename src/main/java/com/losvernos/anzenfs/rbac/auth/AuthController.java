package com.losvernos.anzenfs.rbac.auth;

import java.util.List;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthenticationManager authManager;
  private final JwtUtils jwtUtils;

  public AuthController(AuthenticationManager authManager, JwtUtils jwtUtils) {
    this.authManager = authManager;
    this.jwtUtils = jwtUtils;
  }

  @PostMapping("/login")
  public String login(@RequestBody LoginRequest login) {
    Authentication auth = authManager.authenticate(
        new UsernamePasswordAuthenticationToken(login.username(), login.password()));

    List<String> roles = auth.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .toList();

    return jwtUtils.generateToken(auth.getName(), roles);
  }
}
