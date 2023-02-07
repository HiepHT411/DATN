package com.hoanghiep.hust.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "part_directions")
@Setter
@Getter
public class PartDirections {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String directions;
}
