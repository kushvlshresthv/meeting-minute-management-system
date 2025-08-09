package com.sep.mmms_backend.dto;

import com.sep.mmms_backend.entity.Agenda;
import lombok.Getter;

@Getter
public class AgendaDto {
    private final int agendaId;
    private final String agenda;

    public AgendaDto(Agenda agenda) {
        this.agendaId = agenda.getAgendaId();
        this.agenda = agenda.getAgenda();
    }
}
