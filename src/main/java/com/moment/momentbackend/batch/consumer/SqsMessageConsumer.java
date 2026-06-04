package com.moment.momentbackend.batch.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moment.momentbackend.batch.dto.PublicDataIngestMessage;
import com.moment.momentbackend.batch.parser.GovernmentBenefitParser;
import com.moment.momentbackend.batch.parser.SeoulAcademyParser;
import com.moment.momentbackend.batch.parser.SeoulProgramParser;
import com.moment.momentbackend.batch.reader.S3RawReader;
import com.moment.momentbackend.benefit.entity.BenefitMaster;
import com.moment.momentbackend.benefit.repository.BenefitMasterRepository;
import com.moment.momentbackend.program.entity.Institution;
import com.moment.momentbackend.program.entity.Program;
import com.moment.momentbackend.program.repository.InstitutionRepository;
import com.moment.momentbackend.program.repository.ProgramRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;
import com.moment.momentbackend.batch.parser.SeoulCareParser;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "batch.sqs.consumer", name = "enabled", havingValue = "true")
@RequiredArgsConstructor
public class SqsMessageConsumer {

    private final SqsClient sqsClient;
    private final S3RawReader s3RawReader;
    private final ObjectMapper objectMapper;

    private final SeoulProgramParser seoulProgramParser;
    private final SeoulAcademyParser seoulAcademyParser;
    private final GovernmentBenefitParser governmentBenefitParser;

    private final InstitutionRepository institutionRepository;
    private final ProgramRepository programRepository;
    private final BenefitMasterRepository benefitMasterRepository;

    private final SeoulCareParser seoulCareParser;

    @Value("${sqs.queue.url}")
    private String queueUrl;

    // 10초마다 폴링
    @Scheduled(fixedDelay = 10000)
    public void poll() {
        ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(10)
                .waitTimeSeconds(5)
                .build();

        List<Message> messages = sqsClient.receiveMessage(request).messages();
        if (messages.isEmpty()) return;

        log.info("SQS 메시지 {}건 수신", messages.size());
        for (Message message : messages) {
            try {
                processMessage(message);
                deleteMessage(message);
            } catch (Exception e) {
                log.error("메시지 처리 실패 (DLQ로 이동): {}", e.getMessage(), e);
                // 삭제 안 하면 재시도 후 DLQ로 자동 이동
            }
        }
    }

    private void processMessage(Message message) throws Exception {
        log.info("메시지 body: {}", message.body());

        ObjectMapper jsonMapper = new ObjectMapper();
        PublicDataIngestMessage msg = jsonMapper.readValue(
                message.body(), PublicDataIngestMessage.class);

        log.info("처리 시작: {}/{}", msg.getSourceName(), msg.getSourceDetail());

        JsonNode root = s3RawReader.read(msg.getRawBucketName(), msg.getRawObjectKey());

        switch (msg.getSourceName()) {
            case "seoul_public_program" -> {
                SeoulProgramParser.ParseResult result =
                        seoulProgramParser.parse(root, msg.getSourceDetail());
                upsertInstitutions(result.institutions());
                upsertPrograms(result.programs());
            }
            case "seoul_academy" -> {
                SeoulAcademyParser.ParseResult result = seoulAcademyParser.parse(root);
                upsertInstitutions(result.institutions());
                upsertPrograms(result.programs());
            }
            case "government_benefit" -> {
                List<BenefitMaster> benefits = governmentBenefitParser.parse(root);
                upsertBenefits(benefits);
            }
            case "seoul_care" -> {
                SeoulCareParser.ParseResult result =
                        seoulCareParser.parse(root, msg.getSourceDetail());
                upsertInstitutions(result.institutions());
                upsertPrograms(result.programs());
            }
            default -> log.warn("알 수 없는 sourceName: {}", msg.getSourceName());
        }
    }

    private void upsertInstitutions(List<Institution> list) {
        for (Institution inst : list) {
            Optional<Institution> existing = institutionRepository
                    .findByExternalSourceAndExternalId(
                            inst.getExternalSource(), inst.getExternalId());
            if (existing.isEmpty()) {
                institutionRepository.save(inst);
            }
            // 기존 있으면 skip (필요시 업데이트 로직 추가)
        }
        log.info("institution upsert 완료: {}건", list.size());
    }

    private void upsertPrograms(List<Program> list) {
        for (Program prog : list) {
            Optional<Program> existing = programRepository
                    .findByExternalSourceAndExternalId(
                            prog.getExternalSource(), prog.getExternalId());
            if (existing.isEmpty()) {
                programRepository.save(prog);
            }
        }
        log.info("program upsert 완료: {}건", list.size());
    }

    private void upsertBenefits(List<BenefitMaster> list) {
        for (BenefitMaster benefit : list) {
            Optional<BenefitMaster> existing = benefitMasterRepository
                    .findByExternalSourceAndExternalId(
                            benefit.getExternalSource(), benefit.getExternalId());
            if (existing.isEmpty()) {
                benefitMasterRepository.save(benefit);
            }
        }
        log.info("benefit_master upsert 완료: {}건", list.size());
    }

    private void deleteMessage(Message message) {
        sqsClient.deleteMessage(DeleteMessageRequest.builder()
                .queueUrl(queueUrl)
                .receiptHandle(message.receiptHandle())
                .build());
    }
}