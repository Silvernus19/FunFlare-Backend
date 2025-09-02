package com.funflare.funflare.dto;

//import com.funflare.funflare.entity.User;
import com.funflare.funflare.model.User;
import org.springframework.http.ResponseEntity;
import com.funflare.funflare.service.UserService;

import java.time.OffsetDateTime;

public class UserResponseDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private User.Role role;
    private String phone;
    private String organizationName;
    private Boolean verified;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public UserResponseDTO(User user) {
        //his.id = user.getId();
        this.firstName = user.getFirstname();
        this.lastName = user.getLastname();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.phone = user.getPhone();
        this.organizationName = user.getOrganizationName();
        this.verified = user.getVerified();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
    }

    public UserResponseDTO(UserService userService, ResponseEntity<UserResponseDTO> userResponseDTOResponseEntity) {

    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User.Role getRole() {
        return role;
    }
    public void setRole(User.Role role) {
        this.role = role;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}