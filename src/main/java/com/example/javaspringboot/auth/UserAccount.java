package com.example.javaspringboot.auth;

import jakarta.persistence.*;

@Entity
@Table(name = "app_users")
public class UserAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 254)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 60)
    private String passwordHash;

    protected UserAccount() {}

    UserAccount(String email, String passwordHash) {
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public String getPasswordHash() { return passwordHash; }
}
