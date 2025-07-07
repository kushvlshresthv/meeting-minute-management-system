package com.sep.mmms_backend.service;

import com.sep.mmms_backend.entity.Committee;
import com.sep.mmms_backend.entity.Member;
import com.sep.mmms_backend.exceptions.ExceptionMessages;
import com.sep.mmms_backend.exceptions.MemberDoesNotExistException;
import com.sep.mmms_backend.repository.CommitteeRepository;
import com.sep.mmms_backend.repository.MemberRepository;
import com.sep.mmms_backend.validators.EntityValidator;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
}

