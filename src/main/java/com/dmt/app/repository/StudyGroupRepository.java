package com.dmt.app.repository;

import com.dmt.app.entity.StudyGroup;
import com.dmt.app.entity.StudyGroup.StudyGroupStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudyGroupRepository extends JpaRepository<StudyGroup, Long> {
    
    List<StudyGroup> findByStatus(StudyGroupStatus status);
    
    List<StudyGroup> findByStatusAndMinMembersLessThanEqual(StudyGroupStatus status, Integer maxMembers);
    
    @Query("SELECT sg FROM StudyGroup sg JOIN sg.members sgm WHERE sgm.user.id = :userId AND sgm.isActive = true")
    List<StudyGroup> findActiveGroupsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT sg FROM StudyGroup sg WHERE sg.status = 'ACTIVE' AND (SELECT COUNT(sgm) FROM StudyGroupMember sgm WHERE sgm.studyGroup.id = sg.id AND sgm.isActive = true) < sg.maxMembers")
    List<StudyGroup> findActiveGroupsWithAvailableSlots();
    
    @Query("SELECT sg FROM StudyGroup sg WHERE sg.status = 'ACTIVE' AND (SELECT COUNT(sgm) FROM StudyGroupMember sgm WHERE sgm.studyGroup.id = sg.id AND sgm.isActive = true) >= sg.minMembers")
    List<StudyGroup> findActiveGroupsWithMinimumMembers();
} 