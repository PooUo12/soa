package com.spo.workerService.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class LocationDTO {
    String name;
    String x;
    String y;
    String z;
}
