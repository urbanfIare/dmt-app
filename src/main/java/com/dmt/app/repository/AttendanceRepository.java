package com.dmt.app.repository;

import com.dmt.app.entity.Attendance;
import com.dmt.app.entity.Attendance.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    
    List<Attendance> findByUserId(Long userId);
    
    List<Attendance> findByStudySessionId(Long studySessionId);
    
    Optional<Attendance> findByUserIdAndStudySessionId(Long userId, Long studySessionId);
    
    List<Attendance> findByUserIdAndStatus(Long userId, AttendanceStatus status);
    
    @Query("SELECT a FROM Attendance a WHERE a.studySession.studyGroup.id = :groupId")
    List<Attendance> findByStudyGroupId(@Param("groupId") Long groupId);
    
    @Query("SELECT a FROM Attendance a WHERE a.studySession.studyGroup.id = :groupId AND a.user.id = :userId")
    List<Attendance> findByStudyGroupIdAndUserId(@Param("groupId") Long groupId, @Param("userId") Long userId);
    
    @Query("SELECT a FROM Attendance a WHERE a.studySession.id = :sessionId AND a.status = 'PRESENT'")
    List<Attendance> findPresentAttendancesBySessionId(@Param("sessionId") Long sessionId);
    
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.studySession.studyGroup.id = :groupId AND a.status = 'PRESENT'")
    Long countPresentAttendancesByGroupId(@Param("groupId") Long groupId);
    
    @Query("SELECT a FROM Attendance a WHERE a.studySession.startTime BETWEEN :startDate AND :endDate")
    List<Attendance> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
} 