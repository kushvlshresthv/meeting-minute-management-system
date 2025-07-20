package com.sep.mmms_backend.service;

import com.sep.mmms_backend.aop.interfaces.CheckCommitteeAccess;
import com.sep.mmms_backend.dto.CommitteeDetailsDto;
import com.sep.mmms_backend.dto.MemberDto;
import com.sep.mmms_backend.entity.AppUser;
import com.sep.mmms_backend.entity.Committee;
import com.sep.mmms_backend.entity.CommitteeMembership;
import com.sep.mmms_backend.entity.Member;
import com.sep.mmms_backend.exceptions.ExceptionMessages;
import com.sep.mmms_backend.exceptions.IllegalOperationException;
import com.sep.mmms_backend.exceptions.MemberDoesNotExistException;
import com.sep.mmms_backend.repository.CommitteeRepository;
import com.sep.mmms_backend.repository.MemberRepository;
import com.sep.mmms_backend.validators.EntityValidator;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CommitteeService {

    private final CommitteeRepository committeeRepository;
    private final EntityValidator entityValidator;
    private final AppUserService appUserService;
    private final MemberRepository memberRepository;


    public CommitteeService(CommitteeRepository committeeRepository,  AppUserService appUserService, EntityValidator entityValidator, MemberRepository memberRepository) {
       this.committeeRepository = committeeRepository;
       this.appUserService = appUserService;
       this.entityValidator = entityValidator;
       this.memberRepository = memberRepository;
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


    @CheckCommitteeAccess
    //TODO: this method does not check whether the membership already exists, if it does, it simply throws NotUniqueObjectException
    //TODO: thie method also does not check whether the newMemberships has the same member id, it simply throws NotUniqueObjectException in this case as well
    //NOTE: this method also tests whether the members are accessible to the current user with 'createdBy' field
    public Set<Member> addMembershipsToCommittee(int committeeId, Set<CommitteeMembership> newMemberships, String username) {
    Committee committee = committeeRepository.findCommitteeById(committeeId);
    Set<Integer> newMemberIds = newMemberships.stream().map(membership->  {
       entityValidator.validate(membership);
       membership.setCommittee(committee);
       return membership.getMember().getId();
    }).collect(Collectors.toSet());

        Set<Member> validNewMembers = memberRepository.findAllMembersById(newMemberIds);

        if(validNewMembers.size() != newMemberIds.size()) {
            Set<Integer> foundIds = validNewMembers.stream().map(validNewMember ->{
                if(!validNewMember.getCreatedBy().equals(username)) {
                    throw new IllegalOperationException(ExceptionMessages.MEMBER_NOT_ACCESSIBLE);
                }
                return validNewMember.getId();
            }).collect(Collectors.toSet());

            Set<Integer> mssingOrInvalidIds = new HashSet<>(newMemberIds);
            mssingOrInvalidIds.removeAll(foundIds);

            throw new MemberDoesNotExistException(ExceptionMessages.MEMBER_DOES_NOT_EXIST, mssingOrInvalidIds);
        }

        committee.getMemberships().addAll(newMemberships);
        committeeRepository.save(committee);

        return validNewMembers;
    }
}

