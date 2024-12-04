package com.spo.workerService.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "worker")
public class Worker {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Min(1)
    private int id;

    @Column(nullable = false)
    @Size(min = 1)
    private String name;

    @OneToOne(cascade = {CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinColumn(name = "coordinates_id", nullable = false)
    private Coordinates coordinates;

    @Column(nullable = false)
    private LocalDate creationDate;

    @Column(nullable = false)
    @Min(1)
    private long salary;

    @Column(nullable = false)
    private Date startDate;

    @Column(nullable = true)
    private LocalDateTime endDate;

    @Column(nullable = true)
    private Status status;

    @OneToOne(cascade = {CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    public Worker(String name, Coordinates coordinates, long salary, Date startDate, LocalDateTime endDate, Status status, Person person) {
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate();
        this.salary = salary;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.person = person;
    }

    public LocalDate creationDate(){
        return LocalDate.now();
    }
}
