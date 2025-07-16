package com.sep.mmms_backend.service;

import com.sep.mmms_backend.aop.interfaces.CheckCommitteeAccess;
import com.sep.mmms_backend.entity.Committee;
import com.sep.mmms_backend.entity.CommitteeMembership;
import com.sep.mmms_backend.entity.Member;
import com.sep.mmms_backend.exceptions.ExceptionMessages;
import com.sep.mmms_backend.exceptions.InvalidRequestException;
import com.sep.mmms_backend.exceptions.MemberDoesNotExistException;
import com.sep.mmms_backend.repository.CommitteeRepository;
import com.sep.mmms_backend.repository.MemberRepository;
import com.sep.mmms_backend.validators.EntityValidator;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final EntityValidator entityValidator;
    private final CommitteeRepository committeeRepository;

    public MemberService(MemberRepository memberRepository, CommitteeRepository committeeRepository, EntityValidator entityValidator)  {
        this.memberRepository = memberRepository;
        this.entityValidator = entityValidator;
        this.committeeRepository = committeeRepository;
    }

    /**
     * Searches members based on a keyword.
     * - If the keyword is one word, it searches both first and last names.
     * - If the keyword is two or more words, it uses the first two words to search
     * for a match in the first and last name fields.
     */
    public List<Member> searchMemberByName(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return Collections.emptyList();
        }

        String[] parts = keyword.trim().split("\\s+");

        if (parts.length == 1) {
            return memberRepository.findByFirstNameOrLastName(parts[0]);
        } else {
            return memberRepository.findByFullName(parts[0], parts[1]);
        }
    }

    /**
     *
     * @param member member data to be persisted
     * @param committeeId committeeId to which the member is assocated with
     * @param username username that created committee
     * NOTE: This method only establishes a single membership for the new Member ie with the committeeId provided as the second argument.
     * Even if there are other memberships in the Member parameter, they are discarded
     */

    //TODO: check whether the Committee belongs to this particular user;
    @Transactional
    @CheckCommitteeAccess
    public void saveNewMember(Member member, int committeeId, String username) {
        entityValidator.validate(member);
        Committee committee = committeeRepository.findCommitteeById(committeeId);
        if(member.getMemberships() == null || member.getMemberships().size() != 1) {
            throw new InvalidRequestException(ExceptionMessages.INVALID_MEMBERSHIP_FOR_NEW_MEMBER);
        }

        //validate that the ROLE fields is populated
        member.getMemberships().forEach(entityValidator::validate);

        //even if there are multiple memberships, only the first one is considered the valid one
        CommitteeMembership membership = member.getMemberships().iterator().next();
        membership.setCommittee(committee);
        membership.setMember(member);

        memberRepository.save(member);
    }


    public boolean existsById(int memberId) {
        return memberRepository.existsById(memberId);
    }


    public Member findById(int memberId) {
        return memberRepository.findById(memberId).orElseThrow(()->
                new MemberDoesNotExistException(ExceptionMessages.MEMBER_DOES_NOT_EXIST, memberId));
    }

    /**
     *
     * @param member: shouldn't be null
     * @param committeeId: committeeId for which the member's role is required
     * @return returns role of the user in the committee if found, else returns null
     */
    public CommitteeMembership getMembership(Member member, int committeeId) {
        Set <CommitteeMembership> memberships = member.getMemberships();
        for(CommitteeMembership membership : memberships) {
            if(membership.getId().getCommitteeId() == committeeId) {
                return membership;
            }
        }
        return null;
    }
}
