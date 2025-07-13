package com.sep.mmms_backend.service;

import com.sep.mmms_backend.aop.interfaces.CheckCommitteeAccess;
import com.sep.mmms_backend.entity.Committee;
import com.sep.mmms_backend.entity.CommitteeMembership;
import com.sep.mmms_backend.entity.Member;
import com.sep.mmms_backend.exceptions.ExceptionMessages;
import com.sep.mmms_backend.exceptions.InvalidRequestException;
import com.sep.mmms_backend.exceptions.MemberDoesNotExistException;
import com.sep.mmms_backend.repository.MemberRepository;
import com.sep.mmms_backend.validators.EntityValidator;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final CommitteeService committeeService;
    private final EntityValidator entityValidator;

    public MemberService(MemberRepository memberRepository, CommitteeService committeeService, EntityValidator entityValidator)  {
        this.memberRepository = memberRepository;
        this.entityValidator = entityValidator;
        this.committeeService = committeeService;
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
        Committee committee = committeeService.findCommitteeById(committeeId);
        if(member.getMemberships() == null || member.getMemberships().isEmpty()) {
            throw new InvalidRequestException(ExceptionMessages.NO_VALID_MEMBERSHIP);
        }

        //validate that the ROLE fiels is populated
        member.getMemberships().forEach(entityValidator::validate);

        //even if there are multiple memberships, only the first one is considered the valid one
        member.getMemberships().getFirst().setCommittee(committee);
        member.getMemberships().getFirst().setMember(member);

        memberRepository.save(member);
    }


    public boolean existsById(int memberId) {
        return memberRepository.existsById(memberId);
    }

    public List<Member> findAllById(List<Integer> memberIds) {
        return memberRepository.findAllById(memberIds);
    }


    public Set<Member> findAllById(Set<Integer> memberIds) {
        return new HashSet(memberRepository.findAllById(memberIds));
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
        List<CommitteeMembership> memberships = member.getMemberships();
        for(CommitteeMembership membership : memberships) {
            if(membership.getId().getCommitteeId() == committeeId) {
                return membership;
            }
        }
        return null;
    }


    /**
     * This method returns all the Member entites from the list of ids that belong to the provided committee id
     *
     * @param memberIds the ids of the member that need to be fetched
     * @param committeeId the id of the committee to which the member should belong to
     * @return set of member objects
     */
    public Set<Member> findExistingMembersInCommittee(Set<Integer> memberIds, int committeeId) {
        return memberRepository.findExistingMembersInCommittee(memberIds, committeeId);
    }
}
