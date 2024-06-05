package com.smart.goutam.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smart.goutam.entity.Group;
import com.smart.goutam.entity.User;

public interface GroupRepository extends JpaRepository<Group, Long> {
}