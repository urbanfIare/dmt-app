package com.dmt.app.service;

import com.dmt.app.dto.StudyGroupDto;
import com.dmt.app.entity.StudyGroup;
import com.dmt.app.entity.StudyGroupMember;
import com.dmt.app.entity.User;
import com.dmt.app.repository.StudyGroupRepository;
import com.dmt.app.repository.StudyGroupMemberRepository;
import com.dmt.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class StudyGroupService {
    
    private final StudyGroupRepository studyGroupRepository;
    private final StudyGroupMemberRepository studyGroupMemberRepository;
    private final UserRepository userRepository;
    
    @Transactional
    public StudyGroupDto.Response createStudyGroup(Long userId, StudyGroupDto.CreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        // 스터디 그룹 생성
        StudyGroup studyGroup = StudyGroup.builder()
                .name(request.getName())
                .description(request.getDescription())
                .maxMembers(request.getMaxMembers())
                .minMembers(2) // 기본값
                .status(StudyGroup.StudyGroupStatus.ACTIVE)
                .build();
        
        StudyGroup savedGroup = studyGroupRepository.save(studyGroup);
        
        // 생성자를 리더로 설정
        StudyGroupMember leaderMember = StudyGroupMember.builder()
                .user(user)
                .studyGroup(savedGroup)
                .role(StudyGroupMember.MemberRole.LEADER)
                .isActive(true)
                .build();
        
        studyGroupMemberRepository.save(leaderMember);
        
        log.info("새로운 스터디 그룹이 생성되었습니다: {} (리더: {})", savedGroup.getName(), user.getNickname());
        
        return StudyGroupDto.Response.from(savedGroup);
    }
    
    public StudyGroupDto.Response getStudyGroupById(Long groupId) {
        StudyGroup studyGroup = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("스터디 그룹을 찾을 수 없습니다."));
        
        return StudyGroupDto.Response.from(studyGroup);
    }
    
    public List<StudyGroupDto.Response> getAllStudyGroups() {
        return studyGroupRepository.findAll().stream()
                .map(StudyGroupDto.Response::from)
                .collect(Collectors.toList());
    }
    
    public List<StudyGroupDto.Response> getActiveStudyGroups() {
        return studyGroupRepository.findByStatus(StudyGroup.StudyGroupStatus.ACTIVE).stream()
                .map(StudyGroupDto.Response::from)
                .collect(Collectors.toList());
    }
    
    public List<StudyGroupDto.Response> getStudyGroupsByUserId(Long userId) {
        return studyGroupRepository.findActiveGroupsByUserId(userId).stream()
                .map(StudyGroupDto.Response::from)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public StudyGroupDto.Response updateStudyGroup(Long groupId, Long userId, StudyGroupDto.UpdateRequest request) {
        StudyGroup studyGroup = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("스터디 그룹을 찾을 수 없습니다."));
        
        // 리더만 수정 가능
        StudyGroupMember member = studyGroupMemberRepository.findByUserIdAndStudyGroupId(userId, groupId)
                .orElseThrow(() -> new IllegalArgumentException("그룹 멤버가 아닙니다."));
        
        if (member.getRole() != StudyGroupMember.MemberRole.LEADER) {
            throw new IllegalArgumentException("그룹 리더만 수정할 수 있습니다.");
        }
        
        studyGroup.setName(request.getName());
        studyGroup.setDescription(request.getDescription());
        studyGroup.setMaxMembers(request.getMaxMembers());
        
        StudyGroup updatedGroup = studyGroupRepository.save(studyGroup);
        log.info("스터디 그룹이 수정되었습니다: {}", updatedGroup.getName());
        
        return StudyGroupDto.Response.from(updatedGroup);
    }
    
    @Transactional
    public void deleteStudyGroup(Long groupId, Long userId) {
        StudyGroup studyGroup = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("스터디 그룹을 찾을 수 없습니다."));
        
        // 리더만 삭제 가능
        StudyGroupMember member = studyGroupMemberRepository.findByUserIdAndStudyGroupId(userId, groupId)
                .orElseThrow(() -> new IllegalArgumentException("그룹 멤버가 아닙니다."));
        
        if (member.getRole() != StudyGroupMember.MemberRole.LEADER) {
            throw new IllegalArgumentException("그룹 리더만 삭제할 수 있습니다.");
        }
        
        studyGroup.setStatus(StudyGroup.StudyGroupStatus.DELETED);
        studyGroupRepository.save(studyGroup);
        
        log.info("스터디 그룹이 삭제되었습니다: {}", studyGroup.getName());
    }
    
    @Transactional
    public void joinStudyGroup(Long groupId, Long userId) {
        StudyGroup studyGroup = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("스터디 그룹을 찾을 수 없습니다."));
        
        if (studyGroup.getStatus() != StudyGroup.StudyGroupStatus.ACTIVE) {
            throw new IllegalArgumentException("비활성화된 그룹에는 참여할 수 없습니다.");
        }
        
        // 이미 멤버인지 확인
        if (studyGroupMemberRepository.findByUserIdAndStudyGroupId(userId, groupId).isPresent()) {
            throw new IllegalArgumentException("이미 그룹 멤버입니다.");
        }
        
        // 최대 인원 체크
        long currentMemberCount = studyGroup.getMembers().stream()
                .filter(StudyGroupMember::getIsActive)
                .count();
        
        if (studyGroup.getMaxMembers() != null && currentMemberCount >= studyGroup.getMaxMembers()) {
            throw new IllegalArgumentException("그룹 인원이 가득 찼습니다.");
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        StudyGroupMember newMember = StudyGroupMember.builder()
                .user(user)
                .studyGroup(studyGroup)
                .role(StudyGroupMember.MemberRole.MEMBER)
                .isActive(true)
                .build();
        
        studyGroupMemberRepository.save(newMember);
        log.info("사용자 {}가 그룹 {}에 참여했습니다.", user.getNickname(), studyGroup.getName());
    }
    
    @Transactional
    public void leaveStudyGroup(Long groupId, Long userId) {
        StudyGroupMember member = studyGroupMemberRepository.findByUserIdAndStudyGroupId(userId, groupId)
                .orElseThrow(() -> new IllegalArgumentException("그룹 멤버가 아닙니다."));
        
        if (member.getRole() == StudyGroupMember.MemberRole.LEADER) {
            throw new IllegalArgumentException("그룹 리더는 그룹을 떠날 수 없습니다. 먼저 리더를 위임하거나 그룹을 삭제하세요.");
        }
        
        member.setIsActive(false);
        studyGroupMemberRepository.save(member);
        
        log.info("사용자 {}가 그룹 {}에서 나갔습니다.", member.getUser().getNickname(), member.getStudyGroup().getName());
    }
} 