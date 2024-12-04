package com.spo.workerService.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class GetRequest {
    private int pageSize;
    private int pageOffset;
    private List<String> sort;
    private List<String> filter;

}
