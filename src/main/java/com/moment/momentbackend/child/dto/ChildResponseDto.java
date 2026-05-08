package com.moment.momentbackend.child.dto;

import com.moment.momentbackend.child.entity.ChildProfile;
import lombok.Getter;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ChildResponseDto {

    private Long childId;
    private String childName;
    private LocalDate birthDate;
    private int age;
    private String ageGroup;
    private List<String> concerns;

    public ChildResponseDto(ChildProfile childProfile) {
        this.childId = childProfile.getId();
        this.childName = childProfile.getChildName();
        this.birthDate = childProfile.getBirthDate();
        this.age = Period.between(childProfile.getBirthDate(), LocalDate.now()).getYears();
        this.ageGroup = calculateAgeGroup(this.age);
        this.concerns = childProfile.getConcerns().stream()
                .map(c -> c.getConcern())
                .collect(Collectors.toList());
    }

    private String calculateAgeGroup(int age) {
        if (age <= 5) return "PRESCHOOL";
        if (age <= 9) return "ELEMENTARY_LOW";
        return "ELEMENTARY_HIGH";
    }
}