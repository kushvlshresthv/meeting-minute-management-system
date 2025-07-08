package com.sep.mmms_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "decisions")
public class Decision {
    @Id
    @Column(name="decision_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int decisionId;

    @ManyToOne
    @JoinColumn(name="meeting_id", referencedColumnName = "meeting_id")
    @JsonIgnore
    private Meeting meeting;

    @Column(name =  "decision")
    @JsonIgnore
    private String decision;
}
