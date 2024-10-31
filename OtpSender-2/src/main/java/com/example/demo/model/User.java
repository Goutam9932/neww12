package com.example.demo.model;

import java.util.Arrays;
import java.util.Base64;
import jakarta.persistence.*;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;
    private String username;
    private String password;
    private String name;
    private String role;

    // New image field
    @Lob
    private byte[] image;

    // Constructors
    public User(Long id, String email, String password, String name, String role, byte[] image) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
        this.image = image;
    }

    public User() {
        // Default constructor
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public User(Long id, String email, String username, String password, String name, String role, byte[] image) {
		super();
		this.id = id;
		this.email = email;
		this.username = username;
		this.password = password;
		this.name = name;
		this.role = role;
		this.image = image;
	}

	public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public byte[] getImage() {
        return image;
    }

    public String getImageBase64() {
        return image != null ? Base64.getEncoder().encodeToString(image) : "";
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    @Override
	public String toString() {
		return "User [id=" + id + ", email=" + email + ", username=" + username + ", password=" + password + ", name="
				+ name + ", role=" + role + ", image=" + Arrays.toString(image) + "]";
	}
}
