package com.dmt.app.repository;

import com.dmt.app.entity.StudyGroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudyGroupMemberRepository extends JpaRepository<StudyGroupMember, Long> {
    
    Optional<StudyGroupMember> findByUserIdAndStudyGroupId(Long userId, Long studyGroupId);
    
    List<StudyGroupMember> findByUserId(Long userId);
    
    List<StudyGroupMember> findByStudyGroupId(Long studyGroupId);
    
    List<StudyGroupMember> findByStudyGroupIdAndIsActiveTrue(Long studyGroupId);
    
    @Query("SELECT sgm FROM StudyGroupMember sgm WHERE sgm.studyGroup.id = :groupId AND sgm.role = 'LEADER'")
    Optional<StudyGroupMember> findLeaderByStudyGroupId(@Param("groupId") Long groupId);
    
    @Query("SELECT COUNT(sgm) FROM StudyGroupMember sgm WHERE sgm.studyGroup.id = :groupId AND sgm.isActive = true")
    Long countActiveMembersByStudyGroupId(@Param("groupId") Long groupId);
} 