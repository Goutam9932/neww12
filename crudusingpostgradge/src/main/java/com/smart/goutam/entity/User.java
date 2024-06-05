package com.smart.goutam.entity;

import java.util.Arrays;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
@Entity
@Table(name = "users")
public class User {
	
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    private String userName;
	    private String firstName;
	    private String lastName;
	    private String country;
	    private String email;
	    private String mobile;
	    private String password;
	    
	    @Column(columnDefinition="BYTEA")
	    private byte[] image;


	    @Enumerated(EnumType.STRING)
	    private Role role;

	    @ManyToOne
	    @JoinColumn(name = "group_id")
	    
	    private Group group;


	    public User(Long id, String userName, String firstName, String lastName, String country, String email,
	            String mobile, String password, byte[] image, Role role, Group group) {
	        super();
	        this.id = id;
	        this.userName = userName;
	        this.firstName = firstName;
	        this.lastName = lastName;
	        this.country = country;
	        this.email = email;
	        this.mobile = mobile;
	        this.password = password;
	        this.image = image; // Ensure that image is properly passed as byte[]
	        this.role = role;
	        this.group = group;
	    }

		public byte[] getImage() {
			return image;
		}


		public void setImage(byte[] image) {
			this.image = image;
		}


		public User(Long id, String userName, String firstName, String lastName, String country, String email,
				String mobile, String password, Role role, Group group) {
			super();
			this.id = id;
			this.userName = userName;
			this.firstName = firstName;
			this.lastName = lastName;
			this.country = country;
			this.email = email;
			this.mobile = mobile;
			this.password = password;
			this.role = role;
			this.group = group;
		}


		@Override
	    public String toString() {
	        return "User [id=" + id + ", userName=" + userName + ", firstName=" + firstName + ", lastName=" + lastName
	                + ", country=" + country + ", email=" + email + ", mobile=" + mobile + ", password=" + password
	                + ", role=" + role + ", group=" + group + "]";
	    }


		public Long getId() {
			return id;
		}


		public void setId(Long id) {
			this.id = id;
		}


		public String getUserName() {
			return userName;
		}


		public void setUserName(String userName) {
			this.userName = userName;
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


		public String getCountry() {
			return country;
		}


		public void setCountry(String country) {
			this.country = country;
		}


		public String getEmail() {
			return email;
		}


		public void setEmail(String email) {
			this.email = email;
		}


		public String getMobile() {
			return mobile;
		}


		public void setMobile(String mobile) {
			this.mobile = mobile;
		}


		public String getPassword() {
			return password;
		}


		public void setPassword(String password) {
			this.password = password;
		}


		public Role getRole() {
			return role;
		}


		public void setRole(Role role) {
			this.role = role;
		}


		public Group getGroup() {
			return group;
		}


		public void setGroup(Group group) {
			this.group = group;
		}


		public enum Role {
	        ADMIN, USER
	    }


		public User() {
			super();
			// TODO Auto-generated constructor stub
		}


		

	
}