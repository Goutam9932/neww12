package com.smart.goutam.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.goutam.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    
    @Query("select u from User u where u.email = :email")
    public User getUserByName(@Param("email") String email);

    User findByEmail(String email);
    public User findFirstByEmail(String email);
}
