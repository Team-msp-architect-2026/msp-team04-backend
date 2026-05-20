package com.moment.momentbackend.publicdata.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement(localName = "wantedList")
public class BokjiroLocalResponse {

    @JacksonXmlProperty(localName = "totalCount")
    private int totalCount;

    @JacksonXmlProperty(localName = "pageNo")
    private int pageNo;

    @JacksonXmlProperty(localName = "numOfRows")
    private int numOfRows;

    @JacksonXmlProperty(localName = "resultCode")
    private String resultCode;

    @JacksonXmlProperty(localName = "resultMessage")
    private String resultMessage;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "servList")
    private List<LocalServiceItem> servList;

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LocalServiceItem {

        @JacksonXmlProperty(localName = "servId")
        private String servId;

        @JacksonXmlProperty(localName = "servNm")
        private String servNm;

        @JacksonXmlProperty(localName = "servDgst")
        private String servDgst;

        @JacksonXmlProperty(localName = "lifeNmArray")
        private String lifeNmArray;

        @JacksonXmlProperty(localName = "trgterIndvdlNmArray")
        private String trgterIndvdlNmArray;

        @JacksonXmlProperty(localName = "intrsThemaNmArray")
        private String intrsThemaNmArray;

        @JacksonXmlProperty(localName = "sprtCycNm")
        private String sprtCycNm;

        @JacksonXmlProperty(localName = "srvPvsnNm")
        private String srvPvsnNm;

        @JacksonXmlProperty(localName = "aplyMtdNm")
        private String aplyMtdNm;

        @JacksonXmlProperty(localName = "aplyMtdCn")
        private String aplyMtdCn;

        @JacksonXmlProperty(localName = "ctpvNm")
        private String ctpvNm;

        @JacksonXmlProperty(localName = "sggNm")
        private String sggNm;

        @JacksonXmlProperty(localName = "sggCd")
        private String sggCd;

        @JacksonXmlProperty(localName = "bizChrDeptNm")
        private String bizChrDeptNm;

        @JacksonXmlProperty(localName = "servDtlLink")
        private String servDtlLink;
    }
}