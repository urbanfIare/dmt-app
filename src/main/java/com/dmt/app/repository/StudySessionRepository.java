package com.dmt.app.repository;

import com.dmt.app.entity.StudySession;
import com.dmt.app.entity.StudySession.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StudySessionRepository extends JpaRepository<StudySession, Long> {
    
    List<StudySession> findByStudyGroupId(Long studyGroupId);
    
    List<StudySession> findByStudyGroupIdAndStatus(Long studyGroupId, SessionStatus status);
    
    List<StudySession> findByStatus(SessionStatus status);
    
    @Query("SELECT ss FROM StudySession ss WHERE ss.startTime BETWEEN :startDate AND :endDate")
    List<StudySession> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT ss FROM StudySession ss WHERE ss.studyGroup.id = :groupId AND ss.startTime >= :now ORDER BY ss.startTime ASC")
    List<StudySession> findUpcomingSessionsByGroupId(@Param("groupId") Long groupId, @Param("now") LocalDateTime now);
    
    @Query("SELECT ss FROM StudySession ss WHERE ss.status = 'IN_PROGRESS' AND ss.startTime <= :now AND ss.endTime >= :now")
    List<StudySession> findCurrentSessions(@Param("now") LocalDateTime now);
    
    @Query("SELECT ss FROM StudySession ss WHERE ss.status = 'SCHEDULED' AND ss.startTime <= :now")
    List<StudySession> findSessionsToStart(@Param("now") LocalDateTime now);
} 