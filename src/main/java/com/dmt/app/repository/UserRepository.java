package com.dmt.app.repository;

import com.dmt.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    Optional<User> findByNickname(String nickname);
    
    @Query("SELECT u FROM User u JOIN u.studyGroupMembers sgm WHERE sgm.studyGroup.id = :groupId")
    List<User> findUsersByStudyGroupId(@Param("groupId") Long groupId);
    
    @Query("SELECT u FROM User u JOIN u.studyGroupMembers sgm WHERE sgm.studyGroup.id = :groupId AND sgm.role = 'LEADER'")
    Optional<User> findLeaderByStudyGroupId(@Param("groupId") Long groupId);
} 