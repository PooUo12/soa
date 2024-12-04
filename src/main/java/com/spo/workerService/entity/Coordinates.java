package com.spo.workerService.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "coordinates")
public class Coordinates {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(nullable = true)
    @Max(-106)
    private Long x;

    @Column(nullable = true)
    private Float y;

    public Coordinates(Long x, Float y){
        this.x = x;
        this.y = y;
    }
}
