package com.spo.workerService.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class FindPersonDTO {
    String birthday;
    String eyeColor;
    String hairColor;
    String nationality;
}
