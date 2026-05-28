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
public class BokjiroCentralResponse {

    @JacksonXmlProperty(localName = "totalCount")
    private int totalCount;

    @JacksonXmlProperty(localName = "pageNo")
    private int pageNo;

    @JacksonXmlProperty(localName = "numOfRows")
    private int numOfRows;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "servList")
    private List<ServItem> servList;

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ServItem {
        @JacksonXmlProperty(localName = "servId")
        private String servId;

        @JacksonXmlProperty(localName = "servNm")
        private String servNm;

        @JacksonXmlProperty(localName = "servDgst")
        private String servDgst;

        @JacksonXmlProperty(localName = "jurMnofNm")
        private String jurMnofNm;

        @JacksonXmlProperty(localName = "jurOrgNm")
        private String jurOrgNm;

        @JacksonXmlProperty(localName = "lifeArray")
        private String lifeArray;

        @JacksonXmlProperty(localName = "trgterIndvdlArray")
        private String trgterIndvdlArray;

        @JacksonXmlProperty(localName = "intrsThemaArray")
        private String intrsThemaArray;

        @JacksonXmlProperty(localName = "onapPsbltYn")
        private String onapPsbltYn;

        @JacksonXmlProperty(localName = "servDtlLink")
        private String servDtlLink;

        @JacksonXmlProperty(localName = "sprtCycNm")
        private String sprtCycNm;

        @JacksonXmlProperty(localName = "srvPvsnNm")
        private String srvPvsnNm;
    }
}