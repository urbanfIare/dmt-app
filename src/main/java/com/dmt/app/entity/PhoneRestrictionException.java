package com.dmt.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "phone_restriction_exceptions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhoneRestrictionException {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_session_id", nullable = false)
    private StudySession studySession;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExceptionStatus status;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String reason;
    
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;
    
    @Column(name = "exception_start_time")
    private LocalDateTime exceptionStartTime;
    
    @Column(name = "exception_end_time")
    private LocalDateTime exceptionEndTime;
    
    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    public enum ExceptionStatus {
        PENDING, APPROVED, REJECTED, EXPIRED
    }
} 