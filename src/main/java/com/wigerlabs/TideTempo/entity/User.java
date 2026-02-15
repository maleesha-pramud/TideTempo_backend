package com.wigerlabs.TideTempo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user")
@NamedQuery(name = "User.findById", query = "FROM User u WHERE u.id=:id")
@NamedQuery(name = "User.findByEmail",
        query = "FROM User u WHERE u.email=:email")
@NamedQuery(name = "User.findByEmailAndPassword",
        query = "FROM User u WHERE u.email=:email AND u.password=:password")
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 20)
    private String password;

    public User() {
    }

    public User(int id, String fullName, String email, String password) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

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
