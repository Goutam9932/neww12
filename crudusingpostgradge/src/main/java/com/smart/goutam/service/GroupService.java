package com.smart.goutam.service;

import org.springframework.http.ResponseEntity;

import com.smart.goutam.entity.Group;

public interface GroupService {

	

	Group getGroupById(Long id);

	ResponseEntity<Group> createGroup(Group group);

	Group saveGroup(Group group);


}