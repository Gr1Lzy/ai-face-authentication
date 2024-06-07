package com.example.aifaceauthentication.service.impl;

import com.example.aifaceauthentication.dto.user.UserLoginRequestDto;
import com.example.aifaceauthentication.dto.user.UserLoginResponseDto;
import com.example.aifaceauthentication.dto.user.UserRegisterRequestDto;
import com.example.aifaceauthentication.dto.user.UserResponseDto;
import com.example.aifaceauthentication.exception.RegistrationException;
import com.example.aifaceauthentication.mapper.UserMapper;
import com.example.aifaceauthentication.model.Role;
import com.example.aifaceauthentication.model.User;
import com.example.aifaceauthentication.repository.RoleRepository;
import com.example.aifaceauthentication.repository.UserRepository;
import com.example.aifaceauthentication.security.JwtTokenProvider;
import com.example.aifaceauthentication.service.AuthenticationService;
import com.example.aifaceauthentication.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final RoleRepository roleRepository;
    private final UserService userService;

    @Override
    @Transactional
    public UserResponseDto register(UserRegisterRequestDto requestDto) throws RegistrationException {
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new RegistrationException("User with this email already exists: " + requestDto.getEmail());
        }

        User user = userMapper.toModel(requestDto);
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));

        Role userRole = roleRepository.findByName(Role.RoleName.ROLE_USER).orElseThrow(
                () -> new RuntimeException("Cannot find a role " + Role.RoleName.ROLE_USER));
        userRole.setName(Role.RoleName.ROLE_USER);
        user.setRoles(Set.of(userRole));

        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Override
    public UserLoginResponseDto login(UserLoginRequestDto requestDto) {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDto.getEmail(),
                        requestDto.getPassword())
        );

        String token = jwtTokenProvider.generateToken(authentication.getName());
        return new UserLoginResponseDto(token);
    }

    @Override
    public UserResponseDto getUserByEmail(String email) {
        User user = userService.findByEmail(email);
        return userMapper.toDto(user);
    }
}
