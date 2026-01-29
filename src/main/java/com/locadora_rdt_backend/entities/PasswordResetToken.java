package com.locadora_rdt_backend.entities;

import java.time.Instant;

import javax.persistence.*;

@Entity
@Table(name = "tb_password_reset_token")
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Instant expiration;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public PasswordResetToken() {}

    public Long getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public Instant getExpiration() {
        return expiration;
    }

    public User getUser() {
        return user;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setExpiration(Instant expiration) {
        this.expiration = expiration;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
