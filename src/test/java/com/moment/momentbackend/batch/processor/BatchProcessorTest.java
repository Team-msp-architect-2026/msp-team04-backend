package com.moment.momentbackend.batch.processor;

import com.moment.momentbackend.batch.dto.BenefitCsvDto;
import com.moment.momentbackend.batch.dto.InstitutionCsvDto;
import com.moment.momentbackend.batch.dto.ProgramCsvDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BatchProcessorTest {

    private final InstitutionItemProcessor institutionProcessor = new InstitutionItemProcessor();
    private final ProgramItemProcessor programProcessor = new ProgramItemProcessor();
    private final BenefitItemProcessor benefitProcessor = new BenefitItemProcessor();

    // -------- Institution --------

    @Test
    @DisplayName("기관 - 정상 데이터 통과")
    void institution_valid_passes() throws Exception {
        InstitutionCsvDto dto = new InstitutionCsvDto();
        dto.setExternalSource("PUBLIC");
        dto.setExternalId("INST-001");
        dto.setInstitutionName("테스트 기관");

        assertThat(institutionProcessor.process(dto)).isNotNull();
    }

    @Test
    @DisplayName("기관 - externalSource만 있으면 null 반환")
    void institution_onlySource_returnsNull() throws Exception {
        InstitutionCsvDto dto = new InstitutionCsvDto();
        dto.setExternalSource("PUBLIC");
        dto.setExternalId(null);
        dto.setInstitutionName("테스트 기관");

        assertThat(institutionProcessor.process(dto)).isNull();
    }

    @Test
    @DisplayName("기관 - 둘 다 null이면 MANUAL + UUID 부여")
    void institution_bothNull_assignsManual() throws Exception {
        InstitutionCsvDto dto = new InstitutionCsvDto();
        dto.setExternalSource(null);
        dto.setExternalId(null);
        dto.setInstitutionName("수기 기관");

        InstitutionCsvDto result = institutionProcessor.process(dto);

        assertThat(result).isNotNull();
        assertThat(result.getExternalSource()).isEqualTo("MANUAL");
        assertThat(result.getExternalId()).isNotBlank();
    }

    // -------- Program --------

    @Test
    @DisplayName("프로그램 - 정상 데이터 통과")
    void program_valid_passes() throws Exception {
        ProgramCsvDto dto = new ProgramCsvDto();
        dto.setExternalSource("PUBLIC");
        dto.setExternalId("PROG-001");
        dto.setTitle("테스트 프로그램");
        dto.setCategory("미술");

        assertThat(programProcessor.process(dto)).isNotNull();
    }

    @Test
    @DisplayName("프로그램 - externalId만 있으면 null 반환")
    void program_onlyId_returnsNull() throws Exception {
        ProgramCsvDto dto = new ProgramCsvDto();
        dto.setExternalSource(null);
        dto.setExternalId("PROG-001");
        dto.setTitle("테스트 프로그램");
        dto.setCategory("미술");

        assertThat(programProcessor.process(dto)).isNull();
    }

    @Test
    @DisplayName("프로그램 - 둘 다 null이면 MANUAL + UUID 부여")
    void program_bothNull_assignsManual() throws Exception {
        ProgramCsvDto dto = new ProgramCsvDto();
        dto.setExternalSource(null);
        dto.setExternalId(null);
        dto.setTitle("수기 프로그램");
        dto.setCategory("음악");

        ProgramCsvDto result = programProcessor.process(dto);

        assertThat(result).isNotNull();
        assertThat(result.getExternalSource()).isEqualTo("MANUAL");
        assertThat(result.getExternalId()).isNotBlank();
    }

    @Test
    @DisplayName("프로그램 - title 누락 시 null 반환")
    void program_noTitle_returnsNull() throws Exception {
        ProgramCsvDto dto = new ProgramCsvDto();
        dto.setExternalSource("PUBLIC");
        dto.setExternalId("PROG-001");
        dto.setTitle(null);
        dto.setCategory("미술");

        assertThat(programProcessor.process(dto)).isNull();
    }

    // -------- Benefit --------

    @Test
    @DisplayName("지원금 - 정상 데이터 통과")
    void benefit_valid_passes() throws Exception {
        BenefitCsvDto dto = new BenefitCsvDto();
        dto.setExternalSource("PUBLIC");
        dto.setExternalId("BEN-001");
        dto.setBenefitName("아동수당");

        assertThat(benefitProcessor.process(dto)).isNotNull();
    }

    @Test
    @DisplayName("지원금 - externalSource만 있으면 null 반환")
    void benefit_onlySource_returnsNull() throws Exception {
        BenefitCsvDto dto = new BenefitCsvDto();
        dto.setExternalSource("PUBLIC");
        dto.setExternalId(null);
        dto.setBenefitName("아동수당");

        assertThat(benefitProcessor.process(dto)).isNull();
    }

    @Test
    @DisplayName("지원금 - 둘 다 null이면 MANUAL + UUID 부여")
    void benefit_bothNull_assignsManual() throws Exception {
        BenefitCsvDto dto = new BenefitCsvDto();
        dto.setExternalSource(null);
        dto.setExternalId(null);
        dto.setBenefitName("수기 지원금");

        BenefitCsvDto result = benefitProcessor.process(dto);

        assertThat(result).isNotNull();
        assertThat(result.getExternalSource()).isEqualTo("MANUAL");
        assertThat(result.getExternalId()).isNotBlank();
    }

    @Test
    @DisplayName("지원금 - benefitName 누락 시 null 반환")
    void benefit_noName_returnsNull() throws Exception {
        BenefitCsvDto dto = new BenefitCsvDto();
        dto.setExternalSource("PUBLIC");
        dto.setExternalId("BEN-001");
        dto.setBenefitName(null);

        assertThat(benefitProcessor.process(dto)).isNull();
    }
}