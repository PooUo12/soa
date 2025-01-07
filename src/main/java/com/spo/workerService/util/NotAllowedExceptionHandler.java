package com.spo.workerService.util;

import com.spo.workerService.dto.Errors;
import jakarta.ws.rs.NotAllowedException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class NotAllowedExceptionHandler implements ExceptionMapper<NotAllowedException> {

    @Context
    private HttpHeaders headers;

    public Response toResponse(NotAllowedException ex){
        Errors err = new Errors();
        err.addError(405, "Method not allowed");
        return Response.status(405).entity(err).type(MediaType.APPLICATION_JSON_TYPE).build();
    }


}