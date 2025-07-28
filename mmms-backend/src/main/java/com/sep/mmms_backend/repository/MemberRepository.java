package com.sep.mmms_backend.repository;

import com.sep.mmms_backend.entity.Meeting;
import com.sep.mmms_backend.entity.Member;
import com.sep.mmms_backend.exceptions.ExceptionMessages;
import com.sep.mmms_backend.exceptions.MemberDoesNotExistException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface MemberRepository extends JpaRepository<Member, Integer> {

    /**
     * Searches for a single keyword in either the first name or the last name.
     */
    @Query("SELECT m FROM members m WHERE LOWER(m.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(m.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Member> findByFirstNameOrLastName(@Param("keyword") String keyword);

    /**
     *If a member's first name matches key1 and their last name matches key2, the first and last conditions will be true, and the member will be returned.

     *If a member's first name matches key1 but their last name doesn't match key2 (e.g., searching for "John Doe" finds a member named "John Smith"), the first condition (LOWER(m.firstName) LIKE ... :key1 ...) is still true, so the member is returned.
     */
    @Query("SELECT m FROM members m WHERE " +
            "LOWER(m.firstName) LIKE LOWER(CONCAT('%', :key1, '%')) OR " +
            "LOWER(m.lastName)  LIKE LOWER(CONCAT('%', :key1, '%')) OR " +
            "LOWER(m.firstName) LIKE LOWER(CONCAT('%', :key2, '%')) OR " +
            "LOWER(m.lastName)  LIKE LOWER(CONCAT('%', :key2, '%'))")
    List<Member> findByFullName(
            @Param("key1") String key1,
            @Param("key2") String key2
    );


    /*
        This query acts as join table in SQL.

        It selects a member and joins member with each committee membership the member has. Now, for every member, we're examining all their committee memberships

        Then we filter the members to those whose Ids are in the given list. Then filter only those who have at least one membership in the given committee
     */

    /**
     * This method returns all the Member entites from the list of ids that belong to the provided committee id
     *
     * @param memberIds the ids of the member that need to be fetched
     * @param committeeId the id of the committee to which the member should belong to
     * @return set of member objects
     */
    @Query("SELECT m FROM members m JOIN m.memberships cm WHERE m.id IN :memberIds AND cm.committee.id = :committeeId")
    Set<Member> findExistingMembersInCommittee(@Param("memberIds") Set<Integer> memberIds, @Param("committeeId") int committeeId);



    default Member findMemberById(int memberId) {
        Optional<Member> member = this.findById(memberId);
        if(member.isEmpty()) {
            throw new MemberDoesNotExistException(ExceptionMessages.MEMBER_DOES_NOT_EXIST, memberId);
        }
        return member.get();
    }


    default Optional<Member> findMemberByIdNoException(int memberId) {
        return this.findById(memberId);
    }


    /**
     * returns the set of members. If no elements are available, it returns an empty set, never null
     */
    default Set<Member> findAllMembersById(Set<Integer> memberIds) {
        return new HashSet<>(findAllById(memberIds));
    }


    default List<Member> findAllMembersById(List<Integer> memberIds) {
        return this.findAllById(memberIds);
    }

    public List<Member> findAllMembersByCreatedBy(String username);
}
