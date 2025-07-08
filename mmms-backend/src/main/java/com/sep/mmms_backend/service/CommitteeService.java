package com.sep.mmms_backend.service;

import com.sep.mmms_backend.dto.CommitteeDetailsDto;
import com.sep.mmms_backend.dto.MemberDto;
import com.sep.mmms_backend.entity.AppUser;
import com.sep.mmms_backend.entity.Committee;
import com.sep.mmms_backend.entity.Member;
import com.sep.mmms_backend.exceptions.CommitteeDoesNotExistException;
import com.sep.mmms_backend.exceptions.ExceptionMessages;
import com.sep.mmms_backend.exceptions.IllegalOperationException;
import com.sep.mmms_backend.exceptions.MemberDoesNotExistException;
import com.sep.mmms_backend.repository.CommitteeRepository;
import com.sep.mmms_backend.repository.MemberRepository;
import com.sep.mmms_backend.validators.EntityValidator;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommitteeService {

    private final CommitteeRepository committeeRepository;
    private final MemberRepository memberRepository;
    private final EntityValidator entityValidator;
    private final AppUserService appUserService;

    public CommitteeService(CommitteeRepository committeeRepository, MemberRepository memberRepository,AppUserService appUserService, EntityValidator entityValidator) {
       this.committeeRepository = committeeRepository;
       this.memberRepository = memberRepository;
       this.appUserService = appUserService;
       this.entityValidator = entityValidator;
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
            int memberId = membership.getMember().getMemberId();
            Optional<Member> member = memberRepository.findById(memberId);
            membership.setMember(member.orElseThrow(()->
                    new MemberDoesNotExistException(ExceptionMessages.MEMBER_DOES_NOT_EXIST, memberId)
            ));
            entityValidator.validate(membership);
        });
        committeeRepository.save(committee);
    }


    /**
     *
     * @param committeeId the id of the committee to be loaded from the database
     * @param username to check whether username has accesss to this committee
     */
    public Committee findCommitteeById(int committeeId, String username) {
        Committee committee =  committeeRepository.findById(committeeId).orElseThrow(()-> new CommitteeDoesNotExistException(ExceptionMessages.COMMITTEE_DOES_NOT_EXIST, committeeId));

        if(!committee.getCreatedBy().getUsername().equals(username)) {
            throw new IllegalOperationException(ExceptionMessages.COMMITTEE_NOT_ACCESSIBLE);
        }
        return committee;
    }

    /**
     * returns all the members belonging to the committee
     */
    public List<MemberDto> getMembersOfCommittee(Committee committee) {
        List<Integer> memberIds = committee.getMemberships().stream()
                .map(membership->membership.getMember().getMemberId())
                .collect(Collectors.toList());

        List<Member> members = memberRepository.findAllById(memberIds);

        return members.stream().map(MemberDto::new)
                .collect(Collectors.toList()); //this uses a constructor whcih accepts Member object to create List<MemberDto>
    }


    /**
     * returns both Committee and Members associated with the committee
     *
     * NOTE: membership is populated in the Members object, not in the committee object
     */
    public CommitteeDetailsDto getCommitteeDetails(int committeeId, String username) {
        Committee committee = this.findCommitteeById(committeeId, username);
        List<MemberDto> members = this.getMembersOfCommittee(committee);
        committee.setMemberships(null);  //membership details is already present in members object
        return new CommitteeDetailsDto(committee, members);
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
}

