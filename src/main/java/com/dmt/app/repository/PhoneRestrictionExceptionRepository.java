package com.dmt.app.repository;

import com.dmt.app.entity.PhoneRestrictionException;
import com.dmt.app.entity.PhoneRestrictionException.ExceptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PhoneRestrictionExceptionRepository extends JpaRepository<PhoneRestrictionException, Long> {
    
    List<PhoneRestrictionException> findByUserId(Long userId);
    
    List<PhoneRestrictionException> findByStudySessionId(Long studySessionId);
    
    Optional<PhoneRestrictionException> findByUserIdAndStudySessionId(Long userId, Long studySessionId);
    
    List<PhoneRestrictionException> findByStatus(ExceptionStatus status);
    
    List<PhoneRestrictionException> findByUserIdAndStatus(Long userId, ExceptionStatus status);
    
    @Query("SELECT pre FROM PhoneRestrictionException pre WHERE pre.studySession.studyGroup.id = :groupId")
    List<PhoneRestrictionException> findByStudyGroupId(@Param("groupId") Long groupId);
    
    @Query("SELECT pre FROM PhoneRestrictionException pre WHERE pre.studySession.studyGroup.id = :groupId AND pre.status = 'PENDING'")
    List<PhoneRestrictionException> findPendingExceptionsByGroupId(@Param("groupId") Long groupId);
    
    @Query("SELECT pre FROM PhoneRestrictionException pre WHERE pre.approvedBy.id = :leaderId AND pre.status = 'PENDING'")
    List<PhoneRestrictionException> findPendingExceptionsByLeaderId(@Param("leaderId") Long leaderId);
    
    @Query("SELECT pre FROM PhoneRestrictionException pre WHERE pre.status = 'APPROVED' AND pre.exceptionStartTime <= :now AND pre.exceptionEndTime >= :now")
    List<PhoneRestrictionException> findActiveExceptions(@Param("now") LocalDateTime now);
    
    @Query("SELECT pre FROM PhoneRestrictionException pre WHERE pre.status = 'APPROVED' AND pre.exceptionEndTime < :now")
    List<PhoneRestrictionException> findExpiredExceptions(@Param("now") LocalDateTime now);
} 