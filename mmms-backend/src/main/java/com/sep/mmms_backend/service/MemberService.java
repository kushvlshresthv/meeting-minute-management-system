package com.sep.mmms_backend.service;

import com.sep.mmms_backend.entity.Committee;
import com.sep.mmms_backend.entity.Member;
import com.sep.mmms_backend.exceptions.*;
import com.sep.mmms_backend.repository.CommitteeRepository;
import com.sep.mmms_backend.repository.MemberRepository;
import com.sep.mmms_backend.validators.EntityValidator;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final CommitteeRepository committeeRepository;
    private final EntityValidator entityValidator;

    public MemberService(MemberRepository memberRepository,CommitteeRepository committeeRepository,  EntityValidator entityValidator) {
        this.memberRepository = memberRepository;
        this.committeeRepository = committeeRepository;
        this.entityValidator = entityValidator;
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
    public void saveNewMember(Member member, int committeeId, String username) {

        entityValidator.validate(member);

        Committee committee = committeeRepository.findById(committeeId).orElseThrow(() ->
            new CommitteeDoesNotExistException(ExceptionMessages.COMMITTEE_DOES_NOT_EXIST, committeeId)
        );

        //check if the committee is created by the username
        if(!committee.getCreatedBy().getUsername().equals(username)) {
            throw new IllegalOperationException(ExceptionMessages.COMMITTEE_NOT_ACCESSIBLE);
        }

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

    public Member findById(int memberId) {
        return memberRepository.findById(memberId).orElseThrow(()->
                new MemberDoesNotExistException(ExceptionMessages.MEMBER_DOES_NOT_EXIST, memberId));
    }
}
