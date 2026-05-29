package com.portal.repository;

import com.portal.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    @Query("SELECT DISTINCT a FROM Application a JOIN a.roles r WHERE r.name IN :roleNames")
    List<Application> findByRoles(@Param("roleNames") Collection<String> roleNames);
}
