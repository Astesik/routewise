package com.example.ioproject.auth.service;

import com.example.ioproject.auth.dto.request.LoginRequest;
import com.example.ioproject.auth.dto.request.SignupRequest;
import com.example.ioproject.auth.dto.response.JwtResponse;
import com.example.ioproject.auth.dto.response.MessageResponse;
import com.example.ioproject.auth.exception.RoleNotFoundException;
import com.example.ioproject.auth.model.ERole;
import com.example.ioproject.auth.model.Role;
import com.example.ioproject.auth.model.User;
import com.example.ioproject.auth.repository.RoleRepository;
import com.example.ioproject.auth.repository.UserRepository;
import com.example.ioproject.common.util.CookieUtils;
import com.example.ioproject.security.jwt.JwtUtils;
import com.example.ioproject.security.services.UserDetailsImpl;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;

    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder encoder,
                       JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
    }

    public ResponseEntity<JwtResponse> authenticateUser(LoginRequest loginRequest, HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        Cookie cookie = CookieUtils.createJwtCookie(jwt, "192.168.50.106", false, 60 * 60 * 24);
        response.addCookie(cookie);

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }

    public ResponseEntity<MessageResponse> registerUser(SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<Role> roles = getRolesForNewUser(signUpRequest.getRole());
        user.setRoles(roles);

        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    public ResponseEntity<?> getCurrentUser(UserDetailsImpl userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body(new MessageResponse("Not authenticated"));
        }

        List<String> roles = userDetails.getAuthorities().stream()
                .map(r -> r.getAuthority()).collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(
                null,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles
        ));
    }

    public ResponseEntity<MessageResponse> logoutUser(HttpServletResponse response) {
        Cookie cookie = CookieUtils.createLogoutCookie("192.168.50.106", false);
        response.addCookie(cookie);

        return ResponseEntity.ok(new MessageResponse("You've been signed out!"));
    }

    private Set<Role> getRolesForNewUser(Set<String> strRoles) {
        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RoleNotFoundException("Error: Role is not found."));
        roles.add(userRole);
        // Możesz tu obsłużyć dodatkowe role jeśli dodasz ich wybór przy rejestracji
        return roles;
    }
}
