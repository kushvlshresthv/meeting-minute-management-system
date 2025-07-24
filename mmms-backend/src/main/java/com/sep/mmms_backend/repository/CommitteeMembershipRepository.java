package com.sep.mmms_backend.repository;

import com.sep.mmms_backend.entity.CommitteeMembership;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommitteeMembershipRepository extends JpaRepository<CommitteeMembership, Integer> {
}
