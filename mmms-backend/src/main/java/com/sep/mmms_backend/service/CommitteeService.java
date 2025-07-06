package com.sep.mmms_backend.service;

import com.sep.mmms_backend.entity.Committee;
import com.sep.mmms_backend.entity.CommitteeMembership;
import com.sep.mmms_backend.entity.Member;
import com.sep.mmms_backend.exceptions.ExceptionMessages;
import com.sep.mmms_backend.exceptions.MemberDoesNotExistException;
import com.sep.mmms_backend.exceptions.ValidationFailureException;
import com.sep.mmms_backend.repository.CommitteeRepository;
import com.sep.mmms_backend.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import java.util.List;
import java.util.Optional;

@Service
public class CommitteeService {

    private final CommitteeRepository committeeRepository;
    private final MemberRepository memberRepository;
    private final Validator validator;
    private final AppUserService appUserService;

    public CommitteeService(CommitteeRepository committeeRepository, MemberRepository memberRepository,AppUserService appUserService, Validator validator) {
       this.committeeRepository = committeeRepository;
       this.memberRepository = memberRepository;
       this.appUserService = appUserService;
       this.validator = validator;
    }

    /**
     *
     * @param committee committee that is to be persisted
     * @param username username that creates the committee
     *
     * This method first saves the Committee without any memberships, and then later tries to save the memberships. If it fails to do so, the earlier Committee is discarded from the database.
     */
    public void createCommittee(Committee committee, String username) {

        //first just save the committee object without the memberships
        committee.setCreatedBy(appUserService.loadUserByUsername(username));
        List<CommitteeMembership> memberships = committee.getMemberships();
        validateCommittee(committee);
        committee.setMemberships(null);
        final Committee savedCommittee = committeeRepository.save(committee);

        //add the memberships to the committee
        try {
            addMembershipsToCommittee(savedCommittee, memberships);
            validateMemberships(savedCommittee.getMemberships());
            validateCommittee(savedCommittee);
            committeeRepository.save(savedCommittee);
        } catch(Exception e) {
            committeeRepository.delete(savedCommittee);
            throw e;
        }
    }


    /**
     *
     * @param committee committee to which the memberships are to be added
     * @param memberships the 'committee'(empty) and 'member'(partially populated) field of this objetc isn't populated properly
     */
    public void addMembershipsToCommittee(Committee committee, List<CommitteeMembership> memberships ) {
        if(memberships != null && !memberships.isEmpty()) {
            committee.setMemberships(memberships);

            //populate the membership>committee object with the above committee
            //populate the membership>member object as the committee object has only the id
            committee.getMemberships().forEach(membership-> {
                membership.setCommittee(committee);
                int memberId = membership.getMember().getMemberId();
                Optional<Member> member = memberRepository.findById(memberId);
                membership.setMember(member.orElseThrow(()->
                        new MemberDoesNotExistException(ExceptionMessages.MEMBER_DOES_NOT_EXIST, memberId)
                ));
            });
        }
    }


    public void validateMemberships(List<CommitteeMembership> memberships) {
        if(memberships != null && !memberships.isEmpty()) {
            BindingResult bindingResult = new BeanPropertyBindingResult(memberships.getFirst(), "membership");
            for(CommitteeMembership membership: memberships) {
                validator.validate(membership, bindingResult);
                if(bindingResult.hasErrors()) {
                    throw new ValidationFailureException(ExceptionMessages.INVALID_MEMBERSHIP, bindingResult);
                }
            };
        }
    }

    public void validateCommittee(Committee committee) {
       if(committee != null) {
           BindingResult bindingResult = new BeanPropertyBindingResult(committee, "committee");
           validator.validate(committee, bindingResult);
           if(bindingResult.hasErrors()) {
               throw new ValidationFailureException(ExceptionMessages.COMMITTEE_VALIDATION_FAILED, bindingResult);
           }
       }
    }
}

