package com.moment.momentbackend.publicdata.service;

import com.moment.momentbackend.publicdata.client.GovBenefitClient;
import com.moment.momentbackend.publicdata.dto.GovBenefitApiResponse;
import com.moment.momentbackend.publicdata.entity.GovBenefit;
import com.moment.momentbackend.publicdata.repository.GovBenefitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class GovBenefitSyncService {

    private static final String EXTERNAL_SOURCE = "DATA_GO_KR_GOV24_BENEFIT";

    private final GovBenefitClient govBenefitClient;
    private final GovBenefitRepository govBenefitRepository;

    // 특정 페이지 1개만 수집 (테스트용)
    @Transactional
    public int syncBenefits(int page, int perPage) {
        GovBenefitApiResponse response = govBenefitClient.fetchBenefits(page, perPage);

        if (response == null || response.getData() == null) {
            log.warn("공공서비스 혜택 API 응답 데이터가 비어 있습니다.");
            return 0;
        }

        int savedCount = saveItems(response);

        log.info("공공서비스 혜택 데이터 동기화 완료 - page: {}, 저장/갱신 건수: {}", page, savedCount);

        return savedCount;
    }

    // 전체 페이지 수집 (실제 운영용)
    public int syncAllBenefits(int perPage) {
        GovBenefitApiResponse firstResponse = govBenefitClient.fetchBenefits(1, perPage);

        if (firstResponse == null || firstResponse.getData() == null) {
            log.warn("공공서비스 혜택 API 첫 페이지 응답 데이터가 비어 있습니다.");
            return 0;
        }

        int totalCount = firstResponse.getTotalCount();
        int totalPages = (int) Math.ceil((double) totalCount / perPage);

        log.info("공공서비스 혜택 전체 동기화 시작 - totalCount: {}, totalPages: {}, perPage: {}",
                totalCount, totalPages, perPage);

        int totalSavedCount = saveItems(firstResponse);

        for (int page = 2; page <= totalPages; page++) {
            GovBenefitApiResponse response = govBenefitClient.fetchBenefits(page, perPage);

            if (response == null || response.getData() == null) {
                log.warn("공공서비스 혜택 API 응답 데이터가 비어 있습니다. page={}", page);
                continue;
            }

            totalSavedCount += saveItems(response);

            try {
                Thread.sleep(300); // API 서버 부하 방지
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("공공서비스 혜택 동기화 중 인터럽트 발생", e);
            }
        }

        log.info("공공서비스 혜택 전체 동기화 완료 - 저장/갱신 건수: {}", totalSavedCount);

        return totalSavedCount;
    }

    // 공통 저장 로직
    @Transactional
    protected int saveItems(GovBenefitApiResponse response) {
        int savedCount = 0;

        for (GovBenefitApiResponse.ServiceItem item : response.getData()) {
            if (!StringUtils.hasText(item.getServiceId())) {
                log.warn("serviceId가 없는 데이터는 저장하지 않습니다. serviceName={}", item.getServiceName());
                continue;
            }

            GovBenefit benefit = govBenefitRepository
                    .findByExternalSourceAndExternalId(EXTERNAL_SOURCE, item.getServiceId())
                    .map(existing -> {
                        existing.update(
                                item.getServiceName(),
                                item.getSummary(),
                                item.getSupportContent(),
                                item.getTargetAudience(),
                                item.getOrganization(),
                                item.getApplyMethod(),
                                item.getApplyUrl(),
                                item.getServiceCategory()
                        );
                        return existing;
                    })
                    .orElseGet(() -> GovBenefit.create(
                            EXTERNAL_SOURCE,
                            item.getServiceId(),
                            item.getServiceName(),
                            item.getSummary(),
                            item.getSupportContent(),
                            item.getTargetAudience(),
                            item.getOrganization(),
                            item.getApplyMethod(),
                            item.getApplyUrl(),
                            item.getServiceCategory()
                    ));

            govBenefitRepository.save(benefit);
            savedCount++;
        }

        return savedCount;
    }
}