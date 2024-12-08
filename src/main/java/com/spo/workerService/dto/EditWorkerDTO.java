package com.spo.workerService.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Data
public class EditWorkerDTO {
    private String name;
    private String creationDate;
    private String salary;
    private String startDate;
    private String endDate;
    private String status;
}
