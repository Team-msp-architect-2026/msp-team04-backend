package com.moment.momentbackend.program.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class HomeProgramsResponseDto {

    private List<ProgramListResponseDto> freePrograms;
    private List<ProgramListResponseDto> urgentPrograms;
    private List<ProgramListResponseDto> onlinePrograms;

    public HomeProgramsResponseDto(
            List<ProgramListResponseDto> freePrograms,
            List<ProgramListResponseDto> urgentPrograms,
            List<ProgramListResponseDto> onlinePrograms
    ) {
        this.freePrograms = freePrograms;
        this.urgentPrograms = urgentPrograms;
        this.onlinePrograms = onlinePrograms;
    }
}