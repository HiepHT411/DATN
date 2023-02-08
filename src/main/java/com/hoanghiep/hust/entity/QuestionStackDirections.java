package com.hoanghiep.hust.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "question_stack_directions")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionStackDirections {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String directions;

    public QuestionStackDirections(String title, String directions) {
        this.title = title;
        this.directions = directions;
    }
}
