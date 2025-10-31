package com.funflare.funflare.model;


import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    public enum Role {
        ORGANIZER, ATTENDEE, ADMIN;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;





    @Column(nullable = false, length = 100)
    private String firstname;

    @Column(nullable = false, length = 100)
    private String lastname;

    @Column(nullable = false, length = 100)
    private String username;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(length = 20)
    private String phone;

    @Column(name = "organization_name", length = 255)
    private String organizationName;

    @Column(name = "verified" ,nullable = false)
    private Boolean verified = false;

    @Column(name = "created_at", nullable = false,
            columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP")
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at", nullable = false,
            columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP")
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    @Column(name = "last_login")
    private OffsetDateTime lastLogin;

    @Column(name = "verification_token",  length = 255)
    private String verificationToken;


//one to many relationship
@OneToMany(mappedBy = "organizer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
private List<Event> events = new ArrayList<>();

//ONE TO ONE RELATIONSHIP WITH THE WALLETS TABLE
@OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
private Wallet wallet;

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    //    default constructor
    public User() {}


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

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

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
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

    public OffsetDateTime getLastLogin() {
        return lastLogin;
    }

    public String getVerificationToken() {
        return verificationToken;
    }

    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }


    public void setLastLogin(OffsetDateTime lastLogin) {
        this.lastLogin = lastLogin;


    }
}
