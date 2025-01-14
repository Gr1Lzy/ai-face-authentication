package com.example.aifaceauthentication.mapper;

import com.example.aifaceauthentication.config.MapperConfig;
import com.example.aifaceauthentication.dto.user.UserRegisterRequestDto;
import com.example.aifaceauthentication.dto.user.UserResponseDto;
import com.example.aifaceauthentication.model.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserResponseDto toDto(User user);
    User toModel(UserRegisterRequestDto userResponseDto);
}
