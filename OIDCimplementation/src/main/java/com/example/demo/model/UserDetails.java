package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class UserDetails {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sub;
    private String displayName;
    private String firstName;
    private String lastName;
    private String email;
    private String username; // Common field for both Gluu & WSO2
    private String userType; // Maps to 'user_role' in Gluu and 'usertype' in WSO2
    private String birthdate;
    private String userStatus; // Gluu-specific
    private String updatedAt;
    private String nickname;
	// Timestamp of last update
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getSub() {
		return sub;
	}
	public void setSub(String sub) {
		this.sub = sub;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
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
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
	public String getBirthdate() {
		return birthdate;
	}
	public void setBirthdate(String birthdate) {
		this.birthdate = birthdate;
	}
	public String getUserStatus() {
		return userStatus;
	}
	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}
	public String getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public UserDetails(Long id, String sub, String displayName, String firstName, String lastName, String email,
			String username, String userType, String birthdate, String userStatus, String updatedAt, String nickname) {
		super();
		this.id = id;
		this.sub = sub;
		this.displayName = displayName;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.username = username;
		this.userType = userType;
		this.birthdate = birthdate;
		this.userStatus = userStatus;
		this.updatedAt = updatedAt;
		this.nickname = nickname;
	}
	public UserDetails() {
		super();
		// TODO Auto-generated constructor stub
	}
	@Override
	public String toString() {
		return "UserDetails [id=" + id + ", sub=" + sub + ", displayName=" + displayName + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", email=" + email + ", username=" + username + ", userType=" + userType
				+ ", birthdate=" + birthdate + ", userStatus=" + userStatus + ", updatedAt=" + updatedAt + ", nickname="
				+ nickname + "]";
	}
    
    
    
    
    
}