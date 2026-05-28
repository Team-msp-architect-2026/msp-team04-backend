package com.moment.momentbackend.publicdata.service;

import com.moment.momentbackend.publicdata.client.BokjiroCentralClient;
import com.moment.momentbackend.publicdata.dto.BokjiroCentralResponse;
import com.moment.momentbackend.publicdata.entity.BokjiroCentral;
import com.moment.momentbackend.publicdata.repository.BokjiroCentralRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class BokjiroCentralSyncService {

    private static final String EXTERNAL_SOURCE = "BOKJIRO_CENTRAL";

    private final BokjiroCentralClient bokjiroCentralClient;
    private final BokjiroCentralRepository bokjiroCentralRepository;

    public int syncAll(int numOfRows) {
        BokjiroCentralResponse firstResponse = bokjiroCentralClient.fetchWelfareList(1, numOfRows);

        if (firstResponse == null || firstResponse.getServList() == null) {
            log.warn("복지로 중앙부처 API 첫 페이지 응답이 비어 있습니다.");
            return 0;
        }

        int totalCount = firstResponse.getTotalCount();
        int totalPages = (int) Math.ceil((double) totalCount / numOfRows);

        log.info("복지로 중앙부처 전체 동기화 시작 - totalCount: {}, totalPages: {}", totalCount, totalPages);

        int totalSaved = saveItems(firstResponse);

        for (int page = 2; page <= totalPages; page++) {
            BokjiroCentralResponse response = bokjiroCentralClient.fetchWelfareList(page, numOfRows);

            if (response == null || response.getServList() == null) {
                log.warn("복지로 중앙부처 API 응답 없음 - page={}", page);
                continue;
            }

            totalSaved += saveItems(response);

            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("복지로 중앙부처 동기화 중 인터럽트 발생", e);
            }
        }

        log.info("복지로 중앙부처 전체 동기화 완료 - 저장/갱신 건수: {}", totalSaved);
        return totalSaved;
    }

    @Transactional
    protected int saveItems(BokjiroCentralResponse response) {
        int savedCount = 0;

        for (BokjiroCentralResponse.ServItem item : response.getServList()) {
            if (!StringUtils.hasText(item.getServId())) {
                log.warn("servId 없는 데이터 스킵 - servNm={}", item.getServNm());
                continue;
            }

            BokjiroCentral entity = bokjiroCentralRepository
                    .findByExternalSourceAndExternalId(EXTERNAL_SOURCE, item.getServId())
                    .map(existing -> {
                        existing.update(item.getServNm(), item.getServDgst(),
                                item.getJurMnofNm(), item.getJurOrgNm(),
                                item.getLifeArray(), item.getTrgterIndvdlArray(),
                                item.getIntrsThemaArray(), item.getOnapPsbltYn(),
                                item.getServDtlLink(), item.getSprtCycNm(),
                                item.getSrvPvsnNm());
                        return existing;
                    })
                    .orElseGet(() -> BokjiroCentral.create(
                            EXTERNAL_SOURCE, item.getServId(),
                            item.getServNm(), item.getServDgst(),
                            item.getJurMnofNm(), item.getJurOrgNm(),
                            item.getLifeArray(), item.getTrgterIndvdlArray(),
                            item.getIntrsThemaArray(), item.getOnapPsbltYn(),
                            item.getServDtlLink(), item.getSprtCycNm(),
                            item.getSrvPvsnNm()));

            bokjiroCentralRepository.save(entity);
            savedCount++;
        }

        return savedCount;
    }
}