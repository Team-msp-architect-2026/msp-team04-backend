package com.moment.momentbackend.recommendation.dto;

import com.moment.momentbackend.recommendation.enums.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class PreferenceRequestDto {

    @NotNull(message = "childId는 필수입니다.")
    private Long childId;

    private String region;

    @NotNull(message = "monthlyBudget은 필수입니다.")
    private MonthlyBudget monthlyBudget;

    @NotNull(message = "transportType은 필수입니다.")
    private TransportType transportType;

    @NotNull(message = "moveTime은 필수입니다.")
    private MoveTime moveTime;

    @NotNull(message = "onlinePreference는 필수입니다.")
    private OnlinePreference onlinePreference;

    @NotNull(message = "classType은 필수입니다.")
    private ClassType classType;

    private List<String> concerns = List.of();

    private List<String> subjectDetails = List.of();
}