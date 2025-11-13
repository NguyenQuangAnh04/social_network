package com.example.socialnetwork.repository;

import com.example.socialnetwork.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByRole(com.example.socialnetwork.enums.Role role);
}
