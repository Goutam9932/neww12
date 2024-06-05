package com.smart.goutam.service;

import java.util.List;

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


    @Override
    public ResponseEntity<Group> createGroup(Group group) {
        try {
            Group createdGroup = groupRepository.save(group);
            return new ResponseEntity<>(createdGroup, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace(); // Print stack trace for debugging
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @Override
    public Group getGroupById(Long id) {
        return groupRepository.findById(id).orElseThrow(() -> new RuntimeException("Group not found: " + id));
    }

    @Override
    public Group saveGroup(Group group) {
        // Implement if needed
    	 return groupRepository.save(group);
    }

    
    @Override
    public List<Group> getAllGroups() {
        return groupRepository.findAll(); // Assuming that findAll() method fetches all groups from the database
    }


	@Override
	public void deleteGroupById(Long id) {
		// TODO Auto-generated method stub
		
	}

    
}