package com.microservice_ecommerce.auth.repository;

import com.microservice_ecommerce.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
   public User findByEmail(String email);

   public Optional<User> findByUsername(String username);
}