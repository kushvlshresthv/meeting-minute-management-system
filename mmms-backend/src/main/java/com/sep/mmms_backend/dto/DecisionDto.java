package com.sep.mmms_backend.dto;

import com.sep.mmms_backend.entity.Decision;
import lombok.Getter;

@Getter
public class DecisionDto {
    private final int decisionId;
    private final String decision;

    public DecisionDto(Decision decision) {
        this.decisionId = decision.getDecisionId();
        this.decision = decision.getDecision();
    }
}
