package com.generacionimpacto.service;

import com.generacionimpacto.config.JwtUtil;
import com.generacionimpacto.dto.AuthResponse;
import com.generacionimpacto.dto.LoginRequest;
import com.generacionimpacto.dto.RegisterRequest;
import com.generacionimpacto.model.Role;
import com.generacionimpacto.model.User;
import com.generacionimpacto.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {
    
    private static final String ADMIN_SECRET_CODE = "GENIMPACTO2025";
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email ya está registrado");
        }
        
        if (userRepository.existsByStudentCode(request.getStudentCode())) {
            throw new RuntimeException("Código de estudiante ya está registrado");
        }
        
        // Validar código secreto para admin
        if (request.getRole() == Role.ADMIN) {
            if (!ADMIN_SECRET_CODE.equals(request.getAdminSecretCode())) {
                throw new RuntimeException("Código secreto inválido para administrador");
            }
        }
        
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setStudentCode(request.getStudentCode());
        user.setRole(request.getRole());
        
        user = userRepository.save(user);
        
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name(), user.getId());
        return new AuthResponse(token, user.getEmail(), user.getName(), user.getRole(), user.getId());
    }
    
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name(), user.getId());
        return new AuthResponse(token, user.getEmail(), user.getName(), user.getRole(), user.getId());
    }
}




