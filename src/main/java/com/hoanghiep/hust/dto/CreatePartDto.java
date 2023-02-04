package com.hoanghiep.hust.dto;

import com.hoanghiep.hust.enums.PartType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePartDto {

    private int unitTestNumber;

    private String unitTestYear;

    private String unitTestDescription;

    private int partNumber;

    @Enumerated(EnumType.STRING)
    private PartType partType;

    private String partDescription;

    private int numberOfQuestions;

    private int times;
}
