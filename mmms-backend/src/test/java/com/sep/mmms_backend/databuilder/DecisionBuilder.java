package com.sep.mmms_backend.databuilder;

import com.sep.mmms_backend.entity.Decision;
import com.sep.mmms_backend.entity.Meeting;

public class DecisionBuilder {
    private String decision = "This is a default decision.";
    // The meeting is left null here. It will be set by the MeetingBuilder
    // to correctly handle OneToMany relationship
    private Meeting meeting;

    public static DecisionBuilder builder() {
        return new DecisionBuilder();
    }

    public DecisionBuilder withDecisionText(String decisionText) {
        this.decision = decisionText;
        return this;
    }

    public Decision build() {
        Decision decisionObj = new Decision();
        decisionObj.setDecision(this.decision);
        return decisionObj;
    }
}
