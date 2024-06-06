package com.example.aifaceauthentication.service;

import com.example.aifaceauthentication.dto.user.UserUpdateDto;
import com.example.aifaceauthentication.model.User;

public interface UserService {
    void findById(Long id);
    User findByEmail(String email);
    User update(UserUpdateDto userUpdateDto);
    void deleteById(Long id);
}
