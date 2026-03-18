package com.diseasepredictor.dto;

import com.diseasepredictor.entity.User;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank @Size(min = 3, max = 50) private String username;
    @NotBlank @Email                   private String email;
    @NotBlank @Size(min = 6)           private String password;
    @NotBlank                          private String fullName;
    private String phone;
    private User.Role role = User.Role.PATIENT;
    private String institutePassword;
}
