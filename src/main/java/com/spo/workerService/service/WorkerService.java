package com.spo.workerService.service;

import com.spo.workerService.dto.CreateWorkerDTO;
import com.spo.workerService.dto.EditWorkerDTO;
import com.spo.workerService.dto.FindPersonDTO;
import com.spo.workerService.dto.GetRequest;
import com.spo.workerService.entity.Worker;
import com.spo.workerService.repository.WorkerRepository;
import com.spo.workerService.util.WorkerParser;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Dependent
public class WorkerService {

    @Inject
    WorkerRepository workerRepository;
    @Inject
    WorkerParser workerParser;


    public List<Object> getWorkers(GetRequest getRequest){
        List<Object> out = new ArrayList<>();
        var sortRes = workerParser.parseSorts(getRequest.getSort());
        var filterRes = workerParser.parseFilters(getRequest.getFilter());
        if (sortRes.get(1) != null){
            out.add(null);
            out.add(sortRes.get(1));
            out.add(422);
            return out;
        }
        if (filterRes.get(1) != null){
            out.add(null);
            out.add(sortRes.get(1));
            out.add(422);
            return out;
        }

        getRequest.setSort((List<String>) sortRes.get(0));
        getRequest.setFilter((List<String>) filterRes.get(0));

        return workerRepository.get(getRequest);
    }


    public List<Object> createWorker(CreateWorkerDTO worker){
        var res = workerParser.createWorker(worker);
        List<Object> out = new ArrayList<>();
        if (res.get(1) != null){
            out.add(null);
            out.add(res.get(1));
            out.add(422);
            return out;
        }
        return workerRepository.save((Worker) res.get(0));
    }

    public List<Object> getWorker(int id){
        return workerRepository.getWorker(id);
    }

    public List<Object> deleteWorker(int id){
        return workerRepository.deleteWorker(id);
    }

    public List<Object> patchWorker(int id, EditWorkerDTO worker){
        var res = workerParser.patchWorker(id, worker);
        List<Object> out = new ArrayList<>();
        if (res.get(1) != null){
            out.add(null);
            out.add(res.get(1));
            out.add(422);
            return out;
        }
        return workerRepository.patchWorker(id, (Worker) res.get(0));
    }

    public List<Object> getSalary(){
        return workerRepository.getSalary();
    }

    public List<Object> findCountWorkers(FindPersonDTO personDTO){
        var res = workerParser.findCountWorkers(personDTO);
        List<Object> out = new ArrayList<>();
        if (res.get(1) != null){
            out.add(null);
            out.add(res.get(1));
            out.add(422);
            return out;
        }
        return workerRepository.findCountWorkers((Map<String, Object>) res.get(0));
    }
}
