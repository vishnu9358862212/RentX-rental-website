package com.rentx.rentalsystem.repository;

import com.rentx.rentalsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByEmailAndUserType(String email, User.UserType userType);
    
    List<User> findByUserType(User.UserType userType);
    
    List<User> findByUserTypeAndIsBanned(User.UserType userType, Boolean isBanned);
    
    @Query("SELECT u FROM User u WHERE u.userType != :userType")
    List<User> findAllExceptUserType(@Param("userType") User.UserType userType);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.userType = :userType")
    Long countByUserType(@Param("userType") User.UserType userType);
    
    boolean existsByEmail(String email);
}