package com.funflare.funflare.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class OrganizerCreateDto {

    @NotBlank(message = "first name is required")
    @Size( max = 100, message = "first name must not exceed 100 characters")
    public String firstName;

    @NotBlank(message = "second name is required")
    @Size( max = 100, message = "second name must not exceed 100 characters")
    public String lastName;

    @NotBlank(message = "Username is required")
    @Size( max = 100, message = "second name must not exceed 100 characters")
    public String userName;

    @NotBlank(message = "email is required")
    @Email( message = "email must be valid")
    @Size(max = 255, message = "email must not exceed 100 characters")
    public String emailAdress;

    @NotBlank(message = "Phone number is required")
    @Size( max = 100, message = "phone number must not exceed 12 characters")
    public String phoneNumber;

    private final String role = "ORGANIZER";

    @NotBlank(message = "Organization name is required for organizers")
    @Size(max = 100, message = "organization name must not exceed 100 characters")
    public String organization_name;

    public String getOrganizationName() {
        return organization_name;
    }

    public void setOrganizationName(String organizationName) {
        this.organization_name = organizationName;
    }

    @NotBlank(message = "Password is required")
    @Size( min = 8, message = "password must exceed 8 characters")
    public String password;


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmailAdress() {
        return emailAdress;
    }

    public void setEmailAdress(String emailAdress) {
        this.emailAdress = emailAdress;
    }

    public String getRole() {
        return role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
