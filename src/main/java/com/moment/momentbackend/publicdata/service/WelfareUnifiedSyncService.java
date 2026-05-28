package com.moment.momentbackend.publicdata.service;

import com.moment.momentbackend.publicdata.entity.WelfareUnified;
import com.moment.momentbackend.publicdata.repository.WelfareUnifiedRepository;
import com.moment.momentbackend.publicdata.repository.GovBenefitRepository;
import com.moment.momentbackend.publicdata.repository.BokjiroCentralRepository;
import com.moment.momentbackend.publicdata.repository.BokjiroLocalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WelfareUnifiedSyncService {

    private final WelfareUnifiedRepository unifiedRepository;
    private final GovBenefitRepository govBenefitRepository;
    private final BokjiroCentralRepository bokjiroCentralRepository;
    private final BokjiroLocalRepository bokjiroLocalRepository;

    @Transactional
    public void syncAll() {
        syncGovBenefits();
        syncBokjiroCentral();
        syncBokjiroLocal();
    }

    private void syncGovBenefits() {
        var list = govBenefitRepository.findAll();
        var entities = list.stream()
                .filter(g -> !unifiedRepository.existsBySourceAndOriginalId("GOV24", String.valueOf(g.getId())))
                .map(g -> WelfareUnified.builder()
                        .source("GOV24")
                        .originalId(String.valueOf(g.getId()))
                        .serviceId(g.getExternalId())
                        .title(g.getServiceName())
                        .description(g.getSummary())
                        .targetGroup(g.getTargetAudience())
                        .supportType(g.getServiceCategory())
                        .applyMethod(g.getApplyMethod())
                        .applyUrl(g.getApplyUrl())
                        .department(g.getOrganization())
                        .isLocal(false)
                        .build())
                .toList();
        unifiedRepository.saveAll(entities);
        log.info("[WelfareUnified] GOV24 {}건 저장", entities.size());
    }

    private void syncBokjiroCentral() {
        var list = bokjiroCentralRepository.findAll();
        var entities = list.stream()
                .filter(b -> !unifiedRepository.existsBySourceAndOriginalId("BOKJIRO_CENTRAL", String.valueOf(b.getId())))
                .map(b -> WelfareUnified.builder()
                        .source("BOKJIRO_CENTRAL")
                        .originalId(String.valueOf(b.getId()))
                        .serviceId(b.getExternalId())
                        .title(b.getServiceName())
                        .description(b.getServiceSummary())
                        .targetGroup(b.getLifeArray())
                        .supportType(b.getSupportCycle())
                        .applyMethod(b.getOnlineApply())
                        .applyUrl(b.getDetailLink())
                        .department(b.getDepartment())
                        .isLocal(false)
                        .build())
                .toList();
        unifiedRepository.saveAll(entities);
        log.info("[WelfareUnified] BOKJIRO_CENTRAL {}건 저장", entities.size());
    }

    private void syncBokjiroLocal() {
        var list = bokjiroLocalRepository.findAll();
        var entities = list.stream()
                .filter(b -> !unifiedRepository.existsBySourceAndOriginalId("BOKJIRO_LOCAL", String.valueOf(b.getId())))
                .map(b -> WelfareUnified.builder()
                        .source("BOKJIRO_LOCAL")
                        .originalId(String.valueOf(b.getId()))
                        .serviceId(b.getServiceId())
                        .title(b.getServiceName())
                        .description(b.getServiceSummary())
                        .targetGroup(b.getTargetGroup())
                        .supportType(b.getSupportType())
                        .applyMethod(b.getApplyMethod())
                        .applyUrl(b.getApplyUrl())
                        .department(b.getDepartment())
                        .localGovName(b.getLocalGovName())
                        .isLocal(true)
                        .build())
                .toList();
        unifiedRepository.saveAll(entities);
        log.info("[WelfareUnified] BOKJIRO_LOCAL {}건 저장", entities.size());
    }
}