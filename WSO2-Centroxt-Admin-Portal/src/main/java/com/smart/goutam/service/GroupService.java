package com.smart.goutam.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.smart.goutam.entity.Group;

public interface GroupService {

	

	Group getGroupById(Long id);

	ResponseEntity<Group> createGroup(Group group);

	Group saveGroup(Group group);

	List<Group> getAllGroups();

	void deleteGroupById(Long id);


}
