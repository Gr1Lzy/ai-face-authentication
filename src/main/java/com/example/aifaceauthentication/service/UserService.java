package com.example.aifaceauthentication.service;

import com.example.aifaceauthentication.dto.user.UserUpdateDto;
import com.example.aifaceauthentication.entity.User;

import java.util.Optional;

public interface UserService {
    User findById(Long id);
    Optional<User> findByEmail(String email);
    User update(UserUpdateDto userUpdateDto);
    void deleteById(Long id);
}
