package com.example.ioproject.auth.controller;

import com.example.ioproject.auth.dto.request.LoginRequest;
import com.example.ioproject.auth.dto.request.SignupRequest;
import com.example.ioproject.auth.dto.response.JwtResponse;
import com.example.ioproject.auth.dto.response.MessageResponse;
import com.example.ioproject.auth.service.AuthService;
import com.example.ioproject.security.services.UserDetailsImpl;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5175", allowCredentials = "true", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/signin")
  public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest,
                                                      HttpServletResponse response) {
    return authService.authenticateUser(loginRequest, response);
  }

  @PostMapping("/signup")
  public ResponseEntity<MessageResponse> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
    return authService.registerUser(signupRequest);
  }

  @GetMapping("/current-user")
  public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
    return authService.getCurrentUser(userDetails);
  }

  @PostMapping("/logout")
  public ResponseEntity<MessageResponse> logoutUser(HttpServletResponse response) {
    return authService.logoutUser(response);
  }
}
