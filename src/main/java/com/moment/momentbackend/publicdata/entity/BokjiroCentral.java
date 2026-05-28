package com.moment.momentbackend.publicdata.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "bokjiro_central",
        uniqueConstraints = @UniqueConstraint(columnNames = {"external_source", "external_id"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BokjiroCentral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String externalSource;

    @Column(nullable = false)
    private String externalId;

    private String serviceName;
    private String serviceSummary;
    private String ministry;
    private String department;
    private String lifeArray;
    private String targetArray;
    private String themaArray;
    private String onlineApply;
    private String detailLink;
    private String supportCycle;
    private String provisionType;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static BokjiroCentral create(String externalSource, String externalId,
                                        String serviceName, String serviceSummary, String ministry, String department,
                                        String lifeArray, String targetArray, String themaArray,
                                        String onlineApply, String detailLink, String supportCycle, String provisionType) {
        BokjiroCentral b = new BokjiroCentral();
        b.externalSource = externalSource;
        b.externalId = externalId;
        b.serviceName = serviceName;
        b.serviceSummary = serviceSummary;
        b.ministry = ministry;
        b.department = department;
        b.lifeArray = lifeArray;
        b.targetArray = targetArray;
        b.themaArray = themaArray;
        b.onlineApply = onlineApply;
        b.detailLink = detailLink;
        b.supportCycle = supportCycle;
        b.provisionType = provisionType;
        b.createdAt = LocalDateTime.now();
        b.updatedAt = LocalDateTime.now();
        return b;
    }

    public void update(String serviceName, String serviceSummary, String ministry, String department,
                       String lifeArray, String targetArray, String themaArray,
                       String onlineApply, String detailLink, String supportCycle, String provisionType) {
        this.serviceName = serviceName;
        this.serviceSummary = serviceSummary;
        this.ministry = ministry;
        this.department = department;
        this.lifeArray = lifeArray;
        this.targetArray = targetArray;
        this.themaArray = themaArray;
        this.onlineApply = onlineApply;
        this.detailLink = detailLink;
        this.supportCycle = supportCycle;
        this.provisionType = provisionType;
        this.updatedAt = LocalDateTime.now();
    }
}