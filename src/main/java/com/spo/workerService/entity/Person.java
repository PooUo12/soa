package com.spo.workerService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "person")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(nullable = true)
    private String birthday;

    @Column(nullable = true)
    private EyeColor eyeColor;

    @Column(nullable = false)
    private HairColor hairColor;

    @Column(nullable = true)
    private Country nationality;

    @OneToOne(cascade = {CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinColumn(name="location_id", nullable = false)
    private Location location;

    public Person(String date, EyeColor eyeColor, HairColor hairColor, Country nationality, Location location){
        this.birthday = date;
        this.eyeColor = eyeColor;
        this.hairColor = hairColor;
        this.nationality = nationality;
        this.location = location;
    }

}
