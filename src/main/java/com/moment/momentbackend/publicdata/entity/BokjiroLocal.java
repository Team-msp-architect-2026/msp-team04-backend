// entity/BokjiroLocal.java
package com.moment.momentbackend.publicdata.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "bokjiro_local")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class BokjiroLocal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String serviceId;
    private String serviceName;

    @Column(columnDefinition = "TEXT")
    private String serviceSummary;

    private String serviceCategory;
    private String serviceType;
    private String supportType;

    @Column(length = 500)
    private String targetGroup;

    @Column(length = 500)
    private String applyMethod;

    @Column(length = 1000)
    private String applyUrl;

    private String contact;
    private String department;
    private String localGovName;
    private String localGovCode;
    private Integer interestCount;

    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}