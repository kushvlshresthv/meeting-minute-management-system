package com.sep.mmms_backend.service;

import com.sep.mmms_backend.aop.interfaces.CheckCommitteeAccess;
import com.sep.mmms_backend.dto.CommitteeDetailsDto;
import com.sep.mmms_backend.dto.MemberDto;
import com.sep.mmms_backend.dto.NewMembershipRequest;
import com.sep.mmms_backend.entity.AppUser;
import com.sep.mmms_backend.entity.Committee;
import com.sep.mmms_backend.entity.CommitteeMembership;
import com.sep.mmms_backend.entity.Member;
import com.sep.mmms_backend.exceptions.ExceptionMessages;
import com.sep.mmms_backend.exceptions.IllegalOperationException;
import com.sep.mmms_backend.exceptions.MemberDoesNotExistException;
import com.sep.mmms_backend.repository.CommitteeMembershipRepository;
import com.sep.mmms_backend.repository.CommitteeRepository;
import com.sep.mmms_backend.repository.MemberRepository;
import com.sep.mmms_backend.validators.EntityValidator;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CommitteeService {

    private final CommitteeRepository committeeRepository;
    private final EntityValidator entityValidator;
    private final AppUserService appUserService;
    private final MemberRepository memberRepository;
    private final CommitteeMembershipRepository committeeMembershipRepository;


    public CommitteeService(CommitteeRepository committeeRepository,  AppUserService appUserService, EntityValidator entityValidator, MemberRepository memberRepository, CommitteeMembershipRepository committeeMembershipRepository) {
       this.committeeRepository = committeeRepository;
       this.appUserService = appUserService;
       this.entityValidator = entityValidator;
       this.memberRepository = memberRepository;
       this.committeeMembershipRepository = committeeMembershipRepository;
    }

    /**
     *
     * @param committee committee that is to be persisted
     * @param username username that creates the committee
     *
     */
    @Transactional
    public void createCommittee(Committee committee, String username) {
        committee.setCreatedBy(appUserService.loadUserByUsername(username));
        entityValidator.validate(committee);

        committee.getMemberships().forEach(membership-> {
            membership.setCommittee(committee);
            int memberId = membership.getMember().getId();
            membership.setMember(memberRepository.findMemberById(memberId));
            entityValidator.validate(membership);
        });
        committeeRepository.save(committee);
    }


    public Optional<Committee> findCommitteeByIdNoException(int committeeId) {
        return committeeRepository.findById(committeeId);
    }

    /**
     * returns all the members belonging to the committee
     */
    public List<MemberDto> getMembersOfCommittee(Committee committee) {
        List<Integer> memberIds = committee.getMemberships().stream()
                .map(membership->membership.getMember().getId())
                .collect(Collectors.toList());

        List<Member> members = memberRepository.findAllById(memberIds);

        return members.stream().map(member-> new MemberDto(member, committee.getId()))
                .collect(Collectors.toList());
    }


    /**
     * returns both Committee and Members associated with the committee
     *
     * NOTE: membership is populated in the Members object, not in the committee object
     */

    @CheckCommitteeAccess
    public CommitteeDetailsDto getCommitteeDetails(int committeeId, String username) {
        Committee committee = committeeRepository.findCommitteeById(committeeId);
        return new CommitteeDetailsDto(committee);
    }

    //TODO: This method also loads all the meetings associated with a committee which isn't required
    public List<Committee> getCommittees(String username) {
        AppUser currentUser = appUserService.loadUserByUsername(username);
        List<Committee> committees = currentUser.getMyCommittees();
        committees.forEach(committee-> {
            committee.setMeetings(null);
        });
        return committees;
    }


    public boolean existsById(int committeeId) {
        return committeeRepository.existsById(committeeId);
    }


    //TODO: this method does not check whether the membership already exists, if it does, it simply throws NotUniqueObjectException
    //TODO: thie method also does not check whether the newMemberships has the same member id, it simply throws NotUniqueObjectException in this case as well
    //NOTE: this method also tests whether the members are accessible to the current user with 'createdBy' field
    /*
        BUG REPORT: This method won't work if we try to populate the 'committee' with the new 'memberships' because  when trying to save memberships from cascading, JPA will decide whether the membership is either new or not by checking whether membership's primary key value is null or not(which it isn't.

        Since, not null, JPA will try to merge(), but since the row is not present in the database, it will throw EntityNotExistException.

        Here, since we are using CommitteeMembershipRepository, JPA will decide whether the membership is either new or not by calling isNew() method since CommmiteeMembership implements Presistable interface
     */
    @CheckCommitteeAccess
    @Transactional
    public Set<Member> addMembershipsToCommittee(int committeeId, Set<NewMembershipRequest> newMembershipRequests, String username) {
    Committee committee = committeeRepository.findCommitteeById(committeeId);
    Set<Integer> newMemberIds = newMembershipRequests.stream().map(NewMembershipRequest::memberId).collect(Collectors.toSet());

        Set<Member> validNewMembers = memberRepository.findAllMembersById(newMemberIds);

        if(validNewMembers.size() != newMemberIds.size()) {
            Set<Integer> foundIds = validNewMembers.stream().map(Member::getId).collect(Collectors.toSet());
            Set<Integer> mssingOrInvalidIds = new HashSet<>(newMemberIds);
            mssingOrInvalidIds.removeAll(foundIds);
            throw new MemberDoesNotExistException(ExceptionMessages.MEMBER_DOES_NOT_EXIST, mssingOrInvalidIds);
        }

        validNewMembers.forEach(member ->{
                if(!member.getCreatedBy().equals(username)) {
                    throw new IllegalOperationException(ExceptionMessages.MEMBER_NOT_ACCESSIBLE);
                }
        });

        Map<Integer, String> rolesMap = newMembershipRequests.stream().collect(Collectors.toMap(NewMembershipRequest::memberId, NewMembershipRequest::role));

        List<CommitteeMembership> newMemberships = new ArrayList<>();

        for(Member member: validNewMembers) {
            CommitteeMembership newMembership = new CommitteeMembership();
            newMembership.setCommittee(committee);
            newMembership.setMember(member);
            newMembership.setRole(rolesMap.get(member.getId()));
            newMemberships.add(newMembership);
        }
        committeeMembershipRepository.saveAll(newMemberships);
        return validNewMembers;
    }
}

