package com.sep.mmms_backend.databuilder;

import com.sep.mmms_backend.entity.*;

import java.util.HashSet;
import java.util.Set;

/*
    Committee is assocated with memberships. However, the memberships owns the relationship and hence the membership must reference  Committee for the foreign key to be populated.

        Furthermore, Membership is a two-id entity. That means, it also must have a valid reference to a 'saved' member.


    Committee is also assocated with meetings. However, it should only reference to 'saved' meetings as it neither owns the relationship or has CascadeType.PERSIST
 */

public class CommitteeBuilder {
    private String name = "committeeName";
    private String description = "committeeDescription";
    private Set<Meeting> meetings = new HashSet<>();
    private Set<CommitteeMembership> memberships = new HashSet<>();
    private AppUser createdBy;

    /**
     *must reference a saved 'createdBy' AppUser entity. A committee can also have 'membership' but not compulsary
     */
    public static CommitteeBuilder builder() {
        return new CommitteeBuilder();
    }

    public CommitteeBuilder withName(String name) {
        this.name = name;
        return this;
    }


    public CommitteeBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    //NOTE: while saving a Committee, even if the meetings field is populated, nothing happens, because there is not CascadeType.PERSIST
    public CommitteeBuilder withMembers(Set<Meeting> meetings) {
        this.meetings = meetings;
        return this;
    }

    public CommitteeBuilder withMemberships(Set<CommitteeMembership> memberships) {
        this.memberships = memberships;
        return this;
    }

    public CommitteeBuilder withCreatedBy(AppUser createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    public Committee build() {
        Committee committee = new Committee();
        committee.setName(this.name);
        committee.setDescription(this.description);
        for(CommitteeMembership membership : memberships) {
            membership.setCommittee(committee);
        }
        committee.setMemberships(this.memberships);
        committee.setMeetings(this.meetings);
        committee.setCreatedBy(this.createdBy);
        return committee;
    }
}
