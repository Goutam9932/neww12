package com.smart.goutam.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "groups")
public class Group {
	
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @Column(name = "name")
	    private String groupName;



	    @OneToMany(mappedBy = "group")
	    private List<User> users;



		public Long getId() {
			return id;
		}



		public void setId(Long id) {
			this.id = id;
		}



		public String getGroupName() {
			return groupName;
		}



		public void setGroupName(String groupName) {
			this.groupName = groupName;
		}



		public List<User> getUsers() {
			return users;
		}



		public void setUsers(List<User> users) {
			this.users = users;
		}



		public Group(Long id, String groupName, List<User> users) {
			super();
			this.id = id;
			this.groupName = groupName;
			this.users = users;
		}



		public Group() {
			super();
			// TODO Auto-generated constructor stub
		}



		@Override
		public String toString() {
			return "Group [id=" + id + ", groupName=" + groupName + ", users=" + users + "]";
		}
	    
	    
	
}