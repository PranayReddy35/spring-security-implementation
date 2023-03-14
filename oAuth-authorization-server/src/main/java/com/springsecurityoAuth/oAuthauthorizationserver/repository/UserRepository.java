package com.springsecurityoAuth.oAuthauthorizationserver.repository;

import com.springsecurityoAuth.oAuthauthorizationserver.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    User findByEmail(String email);
}
