package lk.icbt.sunrise.dental.controller;

import jakarta.validation.Valid;
import lk.icbt.sunrise.dental.domain.StaffUser;
import lk.icbt.sunrise.dental.dto.LoginRequest;
import lk.icbt.sunrise.dental.dto.LoginResponse;
import lk.icbt.sunrise.dental.repository.StaffUserRepository;
import lk.icbt.sunrise.dental.security.JwtUtil;
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

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final StaffUserRepository staffUserRepository;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil,
                           StaffUserRepository staffUserRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.staffUserRepository = staffUserRepository;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("ROLE_STAFF")
                .replace("ROLE_", "");

        StaffUser staffUser = staffUserRepository.findByUsername(authentication.getName())
                .orElseThrow();

        String token = jwtUtil.generateToken(authentication.getName(), role);
        return new LoginResponse(token, authentication.getName(), staffUser.getFullName(), role, jwtUtil.getExpirationMillis());
    }
}
