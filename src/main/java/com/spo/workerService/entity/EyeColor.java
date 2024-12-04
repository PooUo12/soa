package com.spo.workerService.entity;

import lombok.Getter;

@Getter
public enum EyeColor {
    GREEN ("green"),
    WHITE ("white"),
    BROWN ("brown");

    private final String title;

    EyeColor(String title) {
        this.title = title;
    }

}
