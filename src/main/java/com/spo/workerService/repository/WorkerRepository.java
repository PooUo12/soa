package com.spo.workerService.repository;
import com.spo.workerService.dto.GetRequest;
import com.spo.workerService.entity.Worker;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.ext.Provider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.hibernate.exception.ConstraintViolationException;

import java.util.*;


@Provider
@RequestScoped
public class WorkerRepository {

    @PersistenceContext(unitName = "default")
    private EntityManager entityManager;

    private static final Log log = LogFactory.getLog(WorkerRepository.class);

    @Transactional
    public List<Object> get(GetRequest getRequest){

        TypedQuery<Worker> psqlQuery;
        var errors = new ArrayList<>();
        var out = new ArrayList<>();

        String query = getQuery(getRequest);
        log.info(query);
        try {
            psqlQuery = entityManager.createQuery(query, Worker.class);
        } catch (Exception e){
            errors.add("Sorting and filtering led to error");
            out.add(null);
            out.add(errors);
            out.add(422);
            return out;
        }
        if (getRequest.getPageOffset() > psqlQuery.getResultList().size()){
            out.add("Offset too large, no elements");
        } else if(getRequest.getPageOffset() + getRequest.getPageSize() > psqlQuery.getResultList().size()){
            out.add(psqlQuery.getResultList().subList(getRequest.getPageOffset(), psqlQuery.getResultList().size()));
        } else if (getRequest.getPageSize() != 0) {
            out.add(psqlQuery.getResultList().subList(getRequest.getPageOffset(), getRequest.getPageOffset() + getRequest.getPageSize()));
        } else if (getRequest.getPageOffset() != 0) {
            out.add(psqlQuery.getResultList().subList(getRequest.getPageOffset(), psqlQuery.getResultList().size()));
        } else {
            out.add(psqlQuery.getResultList());
        }
        out.add(null);
        out.add(200);
        return out;
    }


    @Transactional
    public List<Object> save(Worker worker){
        var errors = new ArrayList<>();
        var out = new ArrayList<>();
        try {
        log.info(worker.toString());
            entityManager.persist(worker.getPerson().getLocation());
            log.info("Save location");
            entityManager.persist(worker.getCoordinates());
            log.info("Save Coordinates");
            entityManager.persist(worker.getPerson());
            log.info("Save person");
            entityManager.persist(worker);
            log.info("Save worker");
        } catch (Exception e){
            errors.add("Internal server error");
            out.add(null);
            out.add(errors);
            out.add(500);
            return out;
        }
        out.add(worker);
        out.add(null);
        out.add(200);
        return out;
    }

    @Transactional
    public List<Object> getWorker(int id){
        var errors = new ArrayList<>();
        var out = new ArrayList<>();
        var w = entityManager.find(Worker.class, id);
        if (w == null) {
            errors.add("Worker with id " + id + " not found");
            out.add(null);
            out.add(errors);
            out.add(400);
            return out;
        }
        out.add(w);
        out.add(null);
        out.add(200);
        return out;

    }

    @Transactional
    public List<Object> deleteWorker(int id){
        var errors = new ArrayList<>();
        var out = new ArrayList<>();
        var w = entityManager.find(Worker.class, id);
        if (w == null) {
            errors.add("Worker with id " + id + " not found");
            out.add(null);
            out.add(errors);
            out.add(400);
            return out;
        }
        entityManager.remove(w);
        out.add(null);
        out.add(null);
        out.add(200);
        return out;
    }

    @Transactional
    public List<Object> patchWorker(int id, Worker worker){
        var errors = new ArrayList<>();
        var out = new ArrayList<>();
        var w = entityManager.find(Worker.class, id);
        if (w == null) {
            errors.add("Worker with id " + id + " not found");
            out.add(null);
            out.add(errors);
            out.add(400);
            return out;
        }

        worker.setPerson(w.getPerson());
        worker.setCoordinates(w.getCoordinates());
        entityManager.merge(worker);
        out.add(worker);
        out.add(null);
        out.add(200);
        return out;
    }

    @Transactional
    public List<Object> getSalary(){
        var errors = new ArrayList<>();
        TypedQuery<Long> psqlQuery;
        var out = new ArrayList<>();
        var query = "select sum(salary) from Worker where status != 0";
        try {
            psqlQuery = entityManager.createQuery(query, Long.class);
        } catch (Exception e){
            errors.add("Internal server error");
            out.add(null);
            out.add(errors);
            out.add(500);
            return out;
        }
        out.add(psqlQuery.getResultList());
        out.add(null);
        out.add(200);
        return out;
    }

    @Transactional
    public List<Object> findCountWorkers(Map<String, Object> filters){
        var errors = new ArrayList<>();
        TypedQuery<Long> psqlQuery;
        var out = new ArrayList<>();
        String query = createQuery(filters);
        try {
            psqlQuery = entityManager.createQuery(query, Long.class);
        } catch (Exception e){
            errors.add("Internal server error");
            out.add(null);
            out.add(errors);
            out.add(500);
            return out;
        }
        out.add(psqlQuery.getResultList());
        out.add(null);
        out.add(200);
        return out;
    }

    private static String createQuery(Map<String, Object> filters) {
        StringBuilder query = new StringBuilder("select count(*) \n" +
                "from Worker w\n" +
                "inner join Person p\n" +
                "on (w.person = p)\n" +
                "where ");
        for (Map.Entry<String, Object> entry: filters.entrySet()){
            query.append(entry.getKey());
            query.append(" = ");
            query.append(entry.getValue());
            query.append(" and ");
        }
        query.delete(query.length() - 5, query.length() - 1);
        return query.toString();
    }

    private static String getQuery(GetRequest getRequest) {
        StringBuilder query = new StringBuilder("select w \n" +
                "from Worker w ");

        if (!getRequest.getFilter().isEmpty()){
            query.append(" where ");
            for (String fltr: getRequest.getFilter()){
                query.append(fltr);
                query.append(" and ");
            }
            query.delete(query.length() - 5, query.length() - 1);
        }
        if (!getRequest.getSort().isEmpty()){
            query.append(" order by ");
            for (String srt: getRequest.getSort()){
                query.append(srt);
                query.append(" , ");
            }
            query.delete(query.length() - 3, query.length() - 1);
        }
        return query.toString();
    }
}
