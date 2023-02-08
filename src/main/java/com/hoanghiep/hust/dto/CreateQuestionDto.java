package com.hoanghiep.hust.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateQuestionDto {
    private String title;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private int ans;
    private int index;
    private String questionStackDirectionTitle;
    private String questionStackDirectionDirections;
}
