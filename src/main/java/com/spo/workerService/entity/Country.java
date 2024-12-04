package com.spo.workerService.entity;

import lombok.Getter;

@Getter
public enum Country {
    FRANCE("france"),
    THAILAND("thailand"),
    SOUTH_KOREA("south_korea"),
    NORTH_KOREA("north_korea"),
    JAPAN("japan");

    private final String title;

    Country(String title) {
        this.title = title;
    }
}
