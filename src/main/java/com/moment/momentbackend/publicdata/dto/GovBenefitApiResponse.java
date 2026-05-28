package com.moment.momentbackend.publicdata.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
public class GovBenefitApiResponse {

    @JsonProperty("currentCount")
    private int currentCount;

    @JsonProperty("totalCount")
    private int totalCount;

    @JsonProperty("perPage")
    private int perPage;

    @JsonProperty("page")
    private int page;

    @JsonProperty("data")
    private List<ServiceItem> data;

    @Getter
    @NoArgsConstructor
    public static class ServiceItem {
        @JsonProperty("서비스ID")
        private String serviceId;

        @JsonProperty("서비스명")
        private String serviceName;

        @JsonProperty("서비스목적요약")
        private String summary;

        @JsonProperty("지원내용")
        private String supportContent;

        @JsonProperty("지원대상")
        private String targetAudience;

        @JsonProperty("소관기관명")
        private String organization;

        @JsonProperty("신청방법")
        private String applyMethod;

        @JsonProperty("온라인신청사이트URL")
        private String applyUrl;

        @JsonProperty("서비스분야")
        private String serviceCategory;
    }
}