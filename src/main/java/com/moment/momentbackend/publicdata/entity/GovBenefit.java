package com.moment.momentbackend.publicdata.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "gov_benefits",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_gov_benefits_external",
                        columnNames = {"external_source", "external_id"}
                )
        }
)
public class GovBenefit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_source", nullable = false, length = 50)
    private String externalSource;

    @Column(name = "external_id", nullable = false, length = 100)
    private String externalId;

    @Column(name = "service_name", length = 500)
    private String serviceName;

    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;

    @Column(name = "support_content", columnDefinition = "TEXT")
    private String supportContent;

    @Column(name = "target_audience", columnDefinition = "TEXT")
    private String targetAudience;

    @Column(name = "organization", length = 300)
    private String organization;

    @Column(name = "apply_method", columnDefinition = "TEXT")
    private String applyMethod;

    @Column(name = "apply_url", columnDefinition = "TEXT")
    private String applyUrl;

    @Column(name = "service_category", length = 200)
    private String serviceCategory;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public static GovBenefit create(
            String externalSource,
            String externalId,
            String serviceName,
            String summary,
            String supportContent,
            String targetAudience,
            String organization,
            String applyMethod,
            String applyUrl,
            String serviceCategory
    ) {
        GovBenefit benefit = new GovBenefit();
        benefit.externalSource = externalSource;
        benefit.externalId = externalId;
        benefit.serviceName = serviceName;
        benefit.summary = summary;
        benefit.supportContent = supportContent;
        benefit.targetAudience = targetAudience;
        benefit.organization = organization;
        benefit.applyMethod = applyMethod;
        benefit.applyUrl = applyUrl;
        benefit.serviceCategory = serviceCategory;
        benefit.createdAt = LocalDateTime.now();
        benefit.updatedAt = LocalDateTime.now();
        return benefit;
    }

    public void update(
            String serviceName,
            String summary,
            String supportContent,
            String targetAudience,
            String organization,
            String applyMethod,
            String applyUrl,
            String serviceCategory
    ) {
        this.serviceName = serviceName;
        this.summary = summary;
        this.supportContent = supportContent;
        this.targetAudience = targetAudience;
        this.organization = organization;
        this.applyMethod = applyMethod;
        this.applyUrl = applyUrl;
        this.serviceCategory = serviceCategory;
        this.updatedAt = LocalDateTime.now();
    }
}