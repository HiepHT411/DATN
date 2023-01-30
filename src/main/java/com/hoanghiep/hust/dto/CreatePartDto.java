package com.hoanghiep.hust.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hoanghiep.hust.entity.Question;
import com.hoanghiep.hust.entity.User;
import com.hoanghiep.hust.enums.PartType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Calendar;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePartDto {

    private int unitTestNumber;

    private String year;

    private String unitTestDescription;

    private int partNumber;

    @Enumerated(EnumType.STRING)
    private PartType partType;

    private String partDescription;

    private int numberOfQuestions;

    private int times;
}
