package com.spo.workerService.entity;

import lombok.Getter;

@Getter
public enum Status {
    FIRED("fired"),
    HIRED("hired"),
    RECOMMENDED_FOR_PROMOTION("recommended_for_promotion"),
    REGULAR("regular"),
    PROBATION("probation");

    private final String title;

    Status(String title) {
        this.title = title;
    }
}
