package com.spo.workerService.dto;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Errors {

    List<Error> errors = new ArrayList<>();

    public void addError(int status, String error){
        for (Error error1: errors){
            if (error1.error_code == status){
                error1.error_message.add(error);
                return;
            }
        }
        List<String> errs = new ArrayList<>();
        errs.add(error);
        errors.add(new Error(status, errs));
    }

}
