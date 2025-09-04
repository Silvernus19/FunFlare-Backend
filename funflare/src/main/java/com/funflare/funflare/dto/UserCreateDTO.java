package com.funflare.funflare.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserCreateDTO {
    @NotBlank(message = "first name is required")
    @Size(max = 100, message = "first name must not exceed 100 characters")
    private String firstName;

    @NotBlank(message = "last name is required")
    @Size(max = 100, message = "last name must not exceed 100 characters")
    private String lastName;

    @NotBlank(message = "username is required")
    @Size(max = 100, message = "username must not exceed 100 characters")
    private String userName;

    @NotBlank(message = "email is required")
    @Email(message = "email must be valid")
    @Size(max = 255, message = "email must not exceed 255 characters")
    private String emailAdress;

    @NotBlank(message = "phone number is required")
    @Size(max = 100, message = "phone number must not exceed 12 characters")
    private String phoneNumber;

    @NotBlank(message = "role is required")
    @Size(max = 50, message = "role must not exceed 50 characters")
    private String role;

    @Size(max = 100, message = "organization name must not exceed 100 characters")
    private String organizationName; // Optional, required only for ORGANIZER

    @NotBlank(message = "password is required")
    @Size(min = 8, message = "password must exceed 8 characters")
    private String password;

    // Getters and setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getEmailAdress() { return emailAdress; }
    public void setEmailAdress(String emailAdress) { this.emailAdress = emailAdress; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getOrganizationName() { return organizationName; }
    public void setOrganizationName(String organizationName) { this.organizationName = organizationName; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}