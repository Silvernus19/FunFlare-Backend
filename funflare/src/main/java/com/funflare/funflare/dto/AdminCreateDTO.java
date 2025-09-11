package com.funflare.funflare.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AdminCreateDTO {



    @NotBlank(message = "please enter your first name")
    private String firstname;

    @NotBlank(message = "please enter your first name")
    private String lastname;

//    @NotBlank(message = "please enter your username")
//    private String username;
    @NotBlank(message = "please enter your email adress to sign up or in")
    @Email(message = "please enter a valid email")
    private String email;

    @NotBlank(message = "please enter a valid password")
    @Size(min = 10, message = "admin passwords must exceed 10 characters")
    private String password;

    private final String role = "ADMIN";


//    getters and setters

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

    public String getRole() {
        return role;
    }
//
//    public String getUsername() {
//        return username;
//    }

//    public void setUsername(String username) {
//        this.username = username;
//    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
}
