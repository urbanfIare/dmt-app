package com.dmt.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "study_groups")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyGroup {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "max_members")
    private Integer maxMembers;
    
    @Column(name = "min_members", nullable = false)
    private Integer minMembers = 2;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StudyGroupStatus status;
    
    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "studyGroup", cascade = CascadeType.ALL)
    private List<StudyGroupMember> members = new ArrayList<>();
    
    @OneToMany(mappedBy = "studyGroup", cascade = CascadeType.ALL)
    private List<StudySession> studySessions = new ArrayList<>();
    
    public enum StudyGroupStatus {
        ACTIVE, INACTIVE, DELETED
    }
} 