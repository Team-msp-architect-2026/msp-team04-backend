package com.moment.momentbackend.batch.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InstitutionCsvDto {

    private String externalSource;
    private String externalId;
    private String institutionName;
    private String address;
    private String region;
    private String phone;
    private String website;
    private String institutionType;
}