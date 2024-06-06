package com.example.aifaceauthentication.service.impl;

import com.example.aifaceauthentication.dto.user.UserUpdateDto;
import com.example.aifaceauthentication.model.User;
import com.example.aifaceauthentication.repository.UserRepository;
import com.example.aifaceauthentication.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public void findById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new NoSuchElementException("Cannot find user with email " + email));
    }

    @Override
    public User update(UserUpdateDto userUpdateDto) {
        return null;
    }

    @Override
    public void deleteById(Long id) {

    }
}
