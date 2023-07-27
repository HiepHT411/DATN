package com.hoanghiep.hust.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PractiseQuestion {
    private String optionA;

    private String optionB;

    private String optionC;

    private String optionD;

    private String answer;

    private String question;

    private String chose = "";
}
