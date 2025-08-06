package com.sep.mmms_backend.service;

import com.sep.mmms_backend.aop.interfaces.CheckCommitteeAccess;
import com.sep.mmms_backend.dto.CommitteeCreationDto;
import com.sep.mmms_backend.dto.CommitteeDetailsDto;
import com.sep.mmms_backend.dto.MemberSummaryDto;
import com.sep.mmms_backend.dto.NewMembershipRequest;
import com.sep.mmms_backend.entity.AppUser;
import com.sep.mmms_backend.entity.Committee;
import com.sep.mmms_backend.entity.CommitteeMembership;
import com.sep.mmms_backend.entity.Member;
import com.sep.mmms_backend.exceptions.*;
import com.sep.mmms_backend.repository.CommitteeMembershipRepository;
import com.sep.mmms_backend.repository.CommitteeRepository;
import com.sep.mmms_backend.repository.MemberRepository;
import com.sep.mmms_backend.validators.EntityValidator;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
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
     * @param committeeCreationDto committee that is to be persisted
     * @param username username that creates the committee
     *
     */
    @Transactional
    public Committee saveNewCommittee(CommitteeCreationDto committeeCreationDto, String username) {
        entityValidator.validate(committeeCreationDto);

        if(!committeeCreationDto.getMembers().values().stream().allMatch(Objects::nonNull)) {
            throw new InvalidMembershipException(ExceptionMessages.MEMBERSHIP_ROLE_MISSING);
        }

        Set<Integer> requestedMemberIds = committeeCreationDto.getMembers().keySet();
        List<Member> foundMembers = memberRepository.findAndValidateMembers(requestedMemberIds);

        Committee committee = new Committee();
        committee.setCreatedBy(appUserService.loadUserByUsername(username));
        committee.setName(committeeCreationDto.getName());
        committee.setDescription(committeeCreationDto.getDescription());
        committee.setStatus(committeeCreationDto.getStatus());

        if(committeeCreationDto.getMaximumNumberOfMeetings() != null)
            committee.setMaxNoOfMeetings(committeeCreationDto.getMaximumNumberOfMeetings());

        foundMembers.forEach(member-> {
            CommitteeMembership membership = new CommitteeMembership();
            membership.setMember(member);
            membership.setRole(committeeCreationDto.getMembers().get(member.getId()));
            committee.addMembership(membership);
        });

        return committeeRepository.save(committee);
    }


    public Optional<Committee> findCommitteeByIdNoException(int committeeId) {
        return committeeRepository.findById(committeeId);
    }

    /**
     * returns all the members belonging to the committee
     */
    public List<MemberSummaryDto> getMembersOfCommittee(Committee committee) {
        List<Integer> memberIds = committee.getMemberships().stream()
                .map(membership->membership.getMember().getId())
                .collect(Collectors.toList());

        List<Member> members = memberRepository.findAllById(memberIds);

        return members.stream().map(member-> new MemberSummaryDto(member, committee.getId()))
                .collect(Collectors.toList());
    }



    /**
     * returns both Committee and Members associated with the committee
     *
     * NOTE: membership is populated in the Members object, not in the committee object
     */

    @CheckCommitteeAccess
    public CommitteeDetailsDto getCommitteeDetails(Committee committee, String username) {
        return new CommitteeDetailsDto(committee);
    }

    //TODO: This method also loads all the meetings associated with a committee which isn't required(probably lazy loaded though)
    public List<Committee> getCommittees(String username) {
        AppUser currentUser = appUserService.loadUserByUsername(username);
        return currentUser.getMyCommittees();
    }


    public boolean existsById(int committeeId) {
        return committeeRepository.existsById(committeeId);
    }


    //TODO: this method does not check whether the membership already exists, if it does, it simply throws NotUniqueObjectException
    //TODO: thie method also does not check whether the newMemberships has the same member id, it simply throws NotUniqueObjectException in this case as well
    //NOTE: this method also tests whether the members are accessible to the current user with 'createdBy' field
    /*
        BUG FIX: This method won't work IF we try to populate the 'committee' with the new 'memberships' because  when trying to save memberships from cascading, JPA will decide whether the membership is either new or not by checking whether membership's primary key value is null or not(which it isn't.

        Since, not null, JPA will try to merge(), but since the row is not present in the database, it will throw EntityNotExistException.

        Here, since we are using CommitteeMembershipRepository, JPA will decide whether the membership is either new or not by calling isNew() method since CommmiteeMembership implements Presistable interface
     */
    @CheckCommitteeAccess
    @Transactional
    public List<Member> addMembershipsToCommittee(Committee committee, Set<NewMembershipRequest> newMembershipRequests, String username) {
    Set<Integer> newMemberIds = newMembershipRequests.stream().map(NewMembershipRequest::memberId).collect(Collectors.toSet());


        List<Member> validNewMembers = memberRepository.findAndValidateMembers(newMemberIds);

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

    public Committee findCommitteeById(int committeeId) {
        return committeeRepository.findCommitteeById(committeeId);
    }
}

