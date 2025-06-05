package com.server.user_service.repository;

import com.server.user_service.model.Role;
import com.server.user_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.id = :id")
    @NonNull
    Optional<User> findById(@Param("id") @NonNull Long id);

    Optional<User> findByUsername(String username);

    List<User> findByRole(Role role);

    void deleteById(@NonNull Long id);
}
