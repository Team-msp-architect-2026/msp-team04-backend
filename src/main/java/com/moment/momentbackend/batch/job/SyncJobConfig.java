package com.moment.momentbackend.batch.job;

import com.moment.momentbackend.batch.dto.BenefitCsvDto;
import com.moment.momentbackend.batch.dto.InstitutionCsvDto;
import com.moment.momentbackend.batch.dto.ProgramCsvDto;
import com.moment.momentbackend.batch.processor.BenefitItemProcessor;
import com.moment.momentbackend.batch.processor.InstitutionItemProcessor;
import com.moment.momentbackend.batch.processor.ProgramItemProcessor;
import com.moment.momentbackend.batch.writer.BenefitItemWriter;
import com.moment.momentbackend.batch.writer.InstitutionItemWriter;
import com.moment.momentbackend.batch.writer.ProgramItemWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SyncJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final ProgramItemProcessor programItemProcessor;
    private final ProgramItemWriter programItemWriter;
    private final BenefitItemProcessor benefitItemProcessor;
    private final BenefitItemWriter benefitItemWriter;
    private final InstitutionItemProcessor institutionItemProcessor;
    private final InstitutionItemWriter institutionItemWriter;

    @Bean
    public Job syncJob() {
        return new JobBuilder("syncJob", jobRepository)
                .start(institutionSyncStep())
                .next(programSyncStep())
                .next(benefitSyncStep())
                .build();
    }

    @Bean
    public Step institutionSyncStep() {
        return new StepBuilder("institutionSyncStep", jobRepository)
                .<InstitutionCsvDto, InstitutionCsvDto>chunk(10, transactionManager)
                .reader(institutionCsvReader())
                .processor(institutionItemProcessor)
                .writer(institutionItemWriter)
                .faultTolerant()
                .skipLimit(100)
                .skip(Exception.class)
                .build();
    }

    @Bean
    public Step programSyncStep() {
        return new StepBuilder("programSyncStep", jobRepository)
                .<ProgramCsvDto, ProgramCsvDto>chunk(10, transactionManager)
                .reader(programCsvReader())
                .processor(programItemProcessor)
                .writer(programItemWriter)
                .faultTolerant()
                .skipLimit(100)
                .skip(Exception.class)
                .build();
    }

    @Bean
    public Step benefitSyncStep() {
        return new StepBuilder("benefitSyncStep", jobRepository)
                .<BenefitCsvDto, BenefitCsvDto>chunk(10, transactionManager)
                .reader(benefitCsvReader())
                .processor(benefitItemProcessor)
                .writer(benefitItemWriter)
                .faultTolerant()
                .skipLimit(100)
                .skip(Exception.class)
                .build();
    }

    @Bean
    public FlatFileItemReader<InstitutionCsvDto> institutionCsvReader() {
        BeanWrapperFieldSetMapper<InstitutionCsvDto> mapper = new BeanWrapperFieldSetMapper<>();
        mapper.setTargetType(InstitutionCsvDto.class);

        return new FlatFileItemReaderBuilder<InstitutionCsvDto>()
                .name("institutionCsvReader")
                .resource(new ClassPathResource("batch/institutions.csv"))
                .delimited()
                .names("externalSource", "externalId", "institutionName", "address",
                        "region", "phone", "website", "institutionType")
                .fieldSetMapper(mapper)
                .linesToSkip(1)
                .build();
    }

    @Bean
    public FlatFileItemReader<ProgramCsvDto> programCsvReader() {
        BeanWrapperFieldSetMapper<ProgramCsvDto> mapper = new BeanWrapperFieldSetMapper<>();
        mapper.setTargetType(ProgramCsvDto.class);

        return new FlatFileItemReaderBuilder<ProgramCsvDto>()
                .name("programCsvReader")
                .resource(new ClassPathResource("batch/programs.csv"))
                .delimited()
                .names("externalSource", "externalId", "title", "category", "description",
                        "programType", "targetAgeMin", "targetAgeMax", "price", "isFree",
                        "region", "detailAddress", "latitude", "longitude",
                        "classType", "isRecruiting", "maxCapacity", "remainCapacity")
                .fieldSetMapper(mapper)
                .linesToSkip(1)
                .build();
    }

    @Bean
    public FlatFileItemReader<BenefitCsvDto> benefitCsvReader() {
        BeanWrapperFieldSetMapper<BenefitCsvDto> mapper = new BeanWrapperFieldSetMapper<>();
        mapper.setTargetType(BenefitCsvDto.class);

        return new FlatFileItemReaderBuilder<BenefitCsvDto>()
                .name("benefitCsvReader")
                .resource(new ClassPathResource("batch/benefits.csv"))
                .delimited()
                .names("externalSource", "externalId", "benefitName", "benefitType",
                        "supportAmount", "supportDescription", "applyLink",
                        "minAge", "maxAge", "region", "isActive")
                .fieldSetMapper(mapper)
                .linesToSkip(1)
                .build();
    }
}