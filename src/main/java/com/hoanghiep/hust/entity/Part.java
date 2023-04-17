package com.hoanghiep.hust.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hoanghiep.hust.enums.PartType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Calendar;
import java.util.List;

@Entity
@Table(name = "part")
@Setter
@Getter
public class Part extends BaseModel {

    @OneToOne
    @JsonIgnore
    private User createdBy;

    @Column(name = "part_number")
    @Size(min = 1, max = 7, message = "part number must be between 1 and 7")
    private int partNumber;

    @Column(name = "part_type")
    @Enumerated(EnumType.STRING)
    private PartType partType;

    @Size(max = 500, message = "The description can't be longer than 500 characters.")
    @NotNull(message = "Please, provide a description")
    private String description;

    @Column(name = "numberOfQuestions")
    private Integer numberOfQuestions;

    @OneToMany(mappedBy = "part", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Question> questions;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", insertable = false, updatable = false)
    private Calendar createdDate;

    @Column(name = "times")
    @Size(max = 120, message = "total time is 120 minutes")
    private int times;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "unit_test_id", referencedColumnName = "id", nullable = false)
    private UnitTest unitTest;

    @Column(name = "audio")
    private String audio;
}
