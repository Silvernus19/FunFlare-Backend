package com.funflare.funflare.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginRequestDTO {

//    @NotBlank(message = "username or email is required")
//    @Size(max =100, message = "username must not exceed 100 characters")
//    public String username;

    @NotBlank(message = "please enter a valid username or password")
    @Email(message = "email must be valid")
    @Size(max= 100, message = " email must not exceed 100 characters")
    public String email;

    @NotBlank(message = "please enter a valid password")
    @Size(min = 8, message = "password must exceed 8 characters")
    public String password;

//    Getters and setters


//    public String getUsername() {
//        return username;
//    }
//
//    public void setUsername(String username) {
//        this.username = username;
//    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
