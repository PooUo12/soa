package com.spo.workerService.controller;

import com.spo.workerService.dto.CreateWorkerDTO;
import com.spo.workerService.dto.EditWorkerDTO;
import com.spo.workerService.dto.FindPersonDTO;
import com.spo.workerService.dto.GetRequest;
import com.spo.workerService.repository.WorkerRepository;
import com.spo.workerService.service.WorkerService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

@Path("api/workers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WorkerController {
    @Inject
    WorkerService workerService;

    private static final Log log = LogFactory.getLog(WorkerRepository.class);


    @GET
    public Response getWorker(@QueryParam("page_size") Optional<Integer> pageSize,
                              @QueryParam("page_offset") Optional<Integer> pageOffset,
                              @QueryParam("sort") List<String> sort,
                              @QueryParam("filter") List<String> filter){

        GetRequest req = new GetRequest();
        req.setPageSize(pageSize.orElse(0));
        req.setPageOffset(pageOffset.orElse(0));
        req.setSort(sort);
        req.setFilter(filter);
        if (req.getSort() == null) {
            req.setSort(new ArrayList<>());
        }
        if (req.getFilter() == null) {
            req.setFilter(new ArrayList<>());
        }

        var res = workerService.getWorkers(req);
        return createResponse(res);
        
    }

    @POST
    public Response createWorker(CreateWorkerDTO worker){
        log.info("Get post request");
        var res = workerService.createWorker(worker);
        return createResponse(res);
    }
    @GET
    @Path("/{worker-id}")
    public Response getWorker(@PathParam("worker-id") int workerId){
        log.info("Get get request");
        var res = workerService.getWorker(workerId);
        return createResponse(res);
    }

    @DELETE
    @Path("/{worker-id}")
    public Response deleteWorker(@PathParam("worker-id") int workerId){
        log.info("Get delete request");
        var res = workerService.deleteWorker(workerId);
        return createResponse(res);
    }

    @PATCH
    @Path("/{worker-id}")
    public Response patchWorker(@PathParam("worker-id") int workerId, EditWorkerDTO worker){
        log.info("Get patch request");
        var res = workerService.patchWorker(workerId, worker);
        return createResponse(res);
    }

    @GET
    @Path("/salary")
    public Response getSalary(){
        log.info("Get salary request");
        var res = workerService.getSalary();
        return createResponse(res);
    }

    @POST
    @Path("/person")
    public Response findCountWorkers(FindPersonDTO personDTO){
        log.info("Get person request");
        var res = workerService.findCountWorkers(personDTO);
        return createResponse(res);
    }

    public Response createResponse(List<Object> res){
        if (res.get(1) != null){
            return Response.status((int) res.get(2)).entity(res.get(1)).build();
        }
        try{
            res.get(3);
        } catch (IndexOutOfBoundsException e){
            return Response.ok().entity(res.get(0)).build();
        }
        Map<String, Boolean> map = new HashMap<>();
        map.put("NextPage", (boolean) res.get(3));
        List<Object> out = new ArrayList<>();
        out.add(res.get(0));
        out.add(map);
        return Response.ok().entity(out).build();
    }
}

