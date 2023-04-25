package com.hoanghiep.hust.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hoanghiep.hust.entity.Question;
import com.hoanghiep.hust.entity.User;
import com.hoanghiep.hust.enums.PartType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Size;
import java.util.Calendar;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PartDto {

    private Long id;

    @JsonIgnore
    private User createdBy;

    private int partNumber;

    @Enumerated(EnumType.STRING)
    private PartType partType;

    private String description;

    private List<Question> questions;

    private Calendar createdDate;

    private int times;

    private Long unitTestId;

    private int unitTestNumber;

    private String year;

    private String audio;
}
