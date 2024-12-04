package com.spo.workerService.entity;

import lombok.Getter;

@Getter
public enum HairColor {
    GREEN("green"),
    BLACK("black"),
    BLUE("blue");

    private final String title;

    HairColor(String title) {
        this.title = title;
    }
}
