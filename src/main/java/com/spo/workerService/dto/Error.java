package com.spo.workerService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Error {
        int error_code;
        List<String> error_message;

}
