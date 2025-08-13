package com.dmt.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "study_group_members")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyGroupMember {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_group_id", nullable = false)
    private StudyGroup studyGroup;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRole role;
    
    @Column(name = "joined_at")
    @CreationTimestamp
    private LocalDateTime joinedAt;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    public enum MemberRole {
        LEADER, MEMBER
    }
} 