
package com.smart.goutam.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.smart.goutam.entity.Group;
import com.smart.goutam.repo.GroupRepository;

@Service
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;

    @Autowired
    public GroupServiceImpl(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    public ResponseEntity<Group> createGroup(Group group) {
        Group createdGroup = groupRepository.save(group);
        return new ResponseEntity<>(createdGroup, HttpStatus.CREATED);
    }

	@Override
	public Group getGroupById(Long id) {
		return groupRepository.findById(id).orElseThrow(()  -> new RuntimeException("Group not found: " +id));
	}

	@Override
	public Group saveGroup(Group group) {
		// TODO Auto-generated method stub
		return null;
	}

	

	}
