package com.moment.momentbackend.publicdata.service;

import com.moment.momentbackend.publicdata.client.BokjiroLocalClient;
import com.moment.momentbackend.publicdata.dto.BokjiroLocalResponse;
import com.moment.momentbackend.publicdata.dto.BokjiroLocalResponse.LocalServiceItem;
import com.moment.momentbackend.publicdata.entity.BokjiroLocal;
import com.moment.momentbackend.publicdata.repository.BokjiroLocalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class BokjiroLocalSyncService {

    private final BokjiroLocalClient client;
    private final BokjiroLocalRepository repository;

    public void syncAll(int numOfRows) {
        int pageNo = 1;
        int totalSaved = 0;

        while (true) {
            BokjiroLocalResponse response = client.fetchPage(pageNo, numOfRows);

            if (response.getServList() == null || response.getServList().isEmpty()) {
                log.info("[BokjiroLocal] 더 이상 데이터 없음. 종료 (page={})", pageNo);
                break;
            }

            int saved = saveItems(response);
            totalSaved += saved;

            log.info("[BokjiroLocal] page={} 저장 완료: {}건 (누적 {}건)", pageNo, saved, totalSaved);

            if (response.getServList().size() < numOfRows) {
                log.info("[BokjiroLocal] 마지막 페이지. 전체 {}건 저장 완료", totalSaved);
                break;
            }

            pageNo++;
        }
    }

    public void syncPage(int pageNo, int numOfRows) {
        BokjiroLocalResponse response = client.fetchPage(pageNo, numOfRows);
        int saved = saveItems(response);
        log.info("[BokjiroLocal] page={} 저장 완료: {}건", pageNo, saved);
    }

    @Transactional
    public int saveItems(BokjiroLocalResponse response) {
        if (response.getServList() == null) return 0;

        List<BokjiroLocal> entities = response.getServList().stream()
                .filter(Objects::nonNull)
                .filter(item -> !repository.existsByServiceId(item.getServId()))
                .map(this::toEntity)
                .toList();

        repository.saveAll(entities);
        return entities.size();
    }

    private BokjiroLocal toEntity(LocalServiceItem item) {
        String govName = Stream.of(item.getCtpvNm(), item.getSggNm())
                .filter(s -> s != null && !s.isBlank())
                .collect(Collectors.joining(" "));

        return BokjiroLocal.builder()
                .serviceId(item.getServId())
                .serviceName(item.getServNm())
                .serviceSummary(item.getServDgst())
                .targetGroup(item.getLifeNmArray())
                .supportType(item.getSprtCycNm())
                .applyMethod(item.getAplyMtdNm())
                .applyUrl(item.getServDtlLink())
                .department(item.getBizChrDeptNm())
                .localGovName(govName)
                .localGovCode(item.getSggNm())
                .build();
    }
}