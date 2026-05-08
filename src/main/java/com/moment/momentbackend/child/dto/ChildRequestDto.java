package com.moment.momentbackend.child.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class ChildRequestDto {

    @NotBlank(message = "자녀 이름은 필수입니다.")
    private String childName;

    @NotNull(message = "생년월일은 필수입니다.")
    private LocalDate birthDate;

    private List<String> concerns;
}