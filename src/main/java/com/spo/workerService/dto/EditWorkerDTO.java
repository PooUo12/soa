package com.spo.workerService.dto;

import com.spo.workerService.entity.Coordinates;
import com.spo.workerService.entity.Person;
import com.spo.workerService.entity.Status;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
