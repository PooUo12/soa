package com.spo.workerService;



import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Application;

@ApplicationPath("/")
@Produces({"application/json"})
public class main extends Application {

}
