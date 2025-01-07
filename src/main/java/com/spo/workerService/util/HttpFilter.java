package com.spo.workerService.util;

import com.spo.workerService.repository.WorkerRepository;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


@Provider
@PreMatching
public class HttpFilter implements ContainerRequestFilter {

    private static final Log log = LogFactory.getLog(WorkerRepository.class);

    public void filter(ContainerRequestContext ctx){

        String url = ctx.getUriInfo().getBaseUri().toString();
        System.out.println(url);
        if (!url.contains("https://")){
            log.info("Http request rejected");
            ctx.abortWith(Response.status(405).entity("Http not allowed").build());
        }
    }
}
