package com.hoanghiep.hust.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.persistence.*;

@Component
@Entity
@Table(name = "result_test")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResultTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String username;

    private int unitTestNumber;

    private String year;

    private int totalPoint = 0;

    private int part1Point = 0;

    private int part2Point = 0;

    private int part3Point = 0;

    private int part4Point = 0;

    private int part5Point = 0;

    private int part6Point = 0;

    private int part7Point = 0;

    @JsonFormat(
            pattern = "yyyy-MM-dd HH:mm:ss.SSS",
            shape = JsonFormat.Shape.STRING
    )
    private String dateTime;
}
