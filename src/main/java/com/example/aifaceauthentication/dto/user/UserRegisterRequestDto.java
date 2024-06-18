package com.example.aifaceauthentication.dto.user;

import com.example.aifaceauthentication.validation.matcher.FieldMatch;
import com.example.aifaceauthentication.validation.validator.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@FieldMatch(first = "password", second = "repeatPassword")
public class UserRegisterRequestDto {
    @Email
    @NotBlank
    private String email;
    @NotBlank
    @Length(min = 6)
    @ValidPassword
    private String password;
    @NotBlank
    private String repeatPassword;

    private String firstName;

    private String lastName;

}
