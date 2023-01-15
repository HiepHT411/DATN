package com.hoanghiep.hust.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "unit_test")
@Setter
@Getter
public class UnitTest extends BaseModel{

//    @OneToOne
//    @JoinColumn(name = "part_1", referencedColumnName = "id", nullable = false)
//    private Part part1;
//
//    @OneToOne
//    @JoinColumn(name = "part_2", referencedColumnName = "id", nullable = false)
//    private Part part2;
//
//    @OneToOne
//    @JoinColumn(name = "part_3", referencedColumnName = "id", nullable = false)
//    private Part part3;
//
//    @OneToOne
//    @JoinColumn(name = "part_4", referencedColumnName = "id", nullable = false)
//    private Part part4;
//
//    @OneToOne
//    @JoinColumn(name = "part_5", referencedColumnName = "id", nullable = false)
//    private Part part5;
//
//    @OneToOne
//    @JoinColumn(name = "part_6", referencedColumnName = "id", nullable = false)
//    private Part part6;
//
//    @OneToOne
//    @JoinColumn(name = "part_7", referencedColumnName = "id", nullable = false)
//    private Part part7;

    @OneToMany(mappedBy = "unitTest")
    private List<Part> parts;

    private String year;

    @Column(name = "unit_test_number")
    @Size(min = 1, max = 10, message = "The unit test number must be between 1 and 10.")
    private int unitTestNumber;

    private String description;

    @CreatedBy
    @Column(
            name = "created_by",
            nullable = false,
            updatable = false,
            insertable = true
    )
    private String createdBy;

    @CreatedDate
    @Column(
            name = "created_date",
            nullable = false,
            updatable = true,
            insertable = true
    )
    @JsonFormat(
            pattern = "yyyy-MM-dd HH:mm:ss.SSS'Z'",
            shape = JsonFormat.Shape.STRING
    )
    private LocalDateTime createdDate;


}
