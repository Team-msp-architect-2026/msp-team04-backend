package com.moment.momentbackend.recommendation.dto;

import com.moment.momentbackend.recommendation.entity.RecommendationPreference;
import com.moment.momentbackend.recommendation.enums.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PreferenceResponseDto {

    private final Long id;
    private final Long childId;
    private final String region;
    private final MonthlyBudget monthlyBudget;
    private final TransportType transportType;
    private final MoveTime moveTime;
    private final OnlinePreference onlinePreference;
    private final ClassType classType;
    private final LocalDateTime createdAt;

    private PreferenceResponseDto(RecommendationPreference entity) {
        this.id = entity.getId();
        this.childId = entity.getChildId();
        this.region = entity.getRegion();
        this.monthlyBudget = entity.getMonthlyBudget();
        this.transportType = entity.getTransportType();
        this.moveTime = entity.getMoveTime();
        this.onlinePreference = entity.getOnlinePreference();
        this.classType = entity.getClassType();
        this.createdAt = entity.getCreatedAt();
    }

    public static PreferenceResponseDto from(RecommendationPreference entity) {
        return new PreferenceResponseDto(entity);
    }
}