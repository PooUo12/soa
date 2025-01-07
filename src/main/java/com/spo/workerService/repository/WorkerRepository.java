package com.spo.workerService.repository;
import com.spo.workerService.dto.*;
import com.spo.workerService.entity.EyeColor;
import com.spo.workerService.entity.Worker;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;


import jakarta.ws.rs.ext.Provider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;


@Provider
@RequestScoped
public class WorkerRepository {

    @PersistenceContext(unitName = "default")
    private EntityManager entityManager;

    private static final Log log = LogFactory.getLog(WorkerRepository.class);

    @Transactional
    public List<Object> get(GetRequest getRequest) {

        Query psqlQuery;
        var errors = new ArrayList<>();
        var out = new ArrayList<>();
        boolean nextPage = false;
        if (getRequest.getFilter().isEmpty() && getRequest.getSort().isEmpty()) {
            psqlQuery = entityManager.createQuery("Select w from Worker w");
        } else {
            LocalDate dateCreate = null;
            String fltrCreate = null;
            LocalDateTime dateEnd = null;
            Date birthDate = null;
            Date dateStart = null;
            String[] splitted = null;
            StringBuilder query = new StringBuilder("""
                    select w \s
                    from Worker w
                    inner join Person p
                    on(w.person = p)
                    inner join Location l
                    on(p.location = l)
                    inner join Coordinates c
                    on(w.coordinates = c)""");

            if (!getRequest.getFilter().isEmpty()) {
                query.append(" where ");
                DateFormat df = new SimpleDateFormat("E MMM d HH:mm:ss 'MSD' yyyy", Locale.US);
                for (String fltr : getRequest.getFilter()) {
                    if (fltr.contains("creationDate")) {
                        splitted = fltr.split(" ");
                        try {
                            dateCreate = LocalDate.parse(splitted[2]);
                        } catch (DateTimeParseException e){
                            errors.add("Illegal creationDate format(filter)");
                            out.add(null);
                            out.add(errors);
                            out.add(422);
                            return out;
                        }
                        fltrCreate = splitted[0] + " " + splitted[1] + " " + ":creationdate";
                        query.append(fltrCreate);
                        query.append(" and ");
                        log.info(dateCreate);
                        log.info(fltrCreate);
                        continue;
                    }
                    if (fltr.contains("endDate")) {
                        splitted = fltr.split(" ");
                        try{
                        dateEnd = LocalDateTime.parse(splitted[2]);
                        } catch (DateTimeParseException e){
                            errors.add("Illegal creationDate format(filter)");
                            out.add(null);
                            out.add(errors);
                            out.add(422);
                            return out;
                        }
                        fltrCreate = splitted[0] + " " + splitted[1] + " " + ":enddate";
                        query.append(fltrCreate);
                        query.append(" and ");
                        log.info(dateEnd);
                        log.info(fltrCreate);
                        continue;
                    }
                    if (fltr.contains("birthday")) {
                        splitted = fltr.split(" ");
                        try {
                            birthDate = df.parse(splitted[2] + " " + splitted[3] + " " + splitted[4] + " " + splitted[5] + " " + splitted[6] + " " + splitted[7]);
                        } catch (ParseException | ArrayIndexOutOfBoundsException e){
                            errors.add("Illegal birthday format(filter)");
                            e.printStackTrace();
                            out.add(null);
                            out.add(errors);
                            out.add(422);
                            return out;
                        }
                        fltrCreate = splitted[0] + " " + splitted[1] + " " + ":birthday";
                        query.append(fltrCreate);
                        query.append(" and ");
                        log.info(birthDate);
                        log.info(fltrCreate);
                        continue;
                    }
                    if (fltr.contains("startDate")) {
                        splitted = fltr.split(" ");
                        try {
                            dateStart = df.parse(splitted[2] + " " + splitted[3] + " " + splitted[4] + " " + splitted[5] + " " + splitted[6] + " " + splitted[7]);
                        } catch (ParseException | ArrayIndexOutOfBoundsException e){
                            errors.add("Illegal startDate format(filter)");
                            out.add(null);
                            out.add(errors);
                            out.add(422);
                            return out;
                        }
                        fltrCreate = splitted[0] + " " + splitted[1] + " " + ":startdate";
                        query.append(fltrCreate);
                        query.append(" and ");
                        log.info(dateStart);
                        log.info(fltrCreate);
                        continue;
                    }
                    query.append(fltr);
                    query.append(" and ");
                }
                query.delete(query.length() - 5, query.length() - 1);
            }
            if (!getRequest.getSort().isEmpty()) {
                query.append(" order by ");
                for (String srt : getRequest.getSort()) {
                    query.append(srt);
                    query.append(" , ");
                }
                query.delete(query.length() - 3, query.length() - 1);
            }
            log.info(query.toString());

            try {
                psqlQuery = entityManager.createQuery(query.toString());
            } catch (Exception e) {
                errors.add("Internal server error");
                out.add(null);
                out.add(errors);
                out.add(500);
                return out;
            }
            if (dateCreate != null) {
                psqlQuery.setParameter("creationdate", dateCreate);
            }
            if (dateEnd != null) {
                psqlQuery.setParameter("enddate", dateEnd);
            }
            if (birthDate != null) {
                psqlQuery.setParameter("birthday", birthDate);
            }
            if (dateStart != null) {
                psqlQuery.setParameter("startdate", dateStart);
            }
        }
            List<WorkerDTO> workers = new ArrayList<>();
            for (Object w : psqlQuery.getResultList()) {
                workers.add(covertToDTO((Worker) w));
            }
            if (getRequest.getPageOffset() > psqlQuery.getResultList().size()) {
                out.add("Offset too large, no elements");
            } else if (getRequest.getPageOffset() + getRequest.getPageSize() > psqlQuery.getResultList().size()) {
                out.add(workers.subList(getRequest.getPageOffset(), psqlQuery.getResultList().size()));
            } else if (getRequest.getPageSize() != 0) {
                out.add(workers.subList(getRequest.getPageOffset(), getRequest.getPageOffset() + getRequest.getPageSize()));
                nextPage = true;
            } else if (getRequest.getPageOffset() != 0) {
                out.add(workers.subList(getRequest.getPageOffset(), psqlQuery.getResultList().size()));
            } else {
                out.add(workers);
            }
            out.add(null);
            out.add(200);
            out.add(nextPage);
            return out;
    }


    @Transactional
    public List<Object> save(Worker worker){
        var errors = new ArrayList<>();
        var out = new ArrayList<>();
        try {
        log.info(worker.toString());
        if (worker.getPerson() != null) {
            entityManager.persist(worker.getPerson().getLocation());
            entityManager.persist(worker.getPerson());

        }
            log.info("Save location");
            entityManager.persist(worker.getCoordinates());
            log.info("Save Coordinates");
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
        out.add(covertToDTO(worker));
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
        log.info(w.toString());
        out.add(covertToDTO(w));
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
        out.add(covertToDTO(worker));
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
        Query psqlQuery;
        var out = new ArrayList<>();
        String query = createQuery(filters);
        try {
        psqlQuery = entityManager.createQuery(query);
        } catch (Exception e){
            errors.add("Internal server error");
            out.add(null);
            out.add(errors);
            out.add(500);
            return out;
        }
        for (Map.Entry<String, Object> entry: filters.entrySet()){
            if (Objects.equals(entry.getKey(), "birthday")) {
                psqlQuery.setParameter("birth", entry.getValue());
            }
        }
        log.info(psqlQuery.getResultList());
        out.add(psqlQuery.getResultList());
        out.add(null);
        out.add(200);
        return out;
    }

    private String createQuery(Map<String, Object> filters) {
        StringBuilder query = new StringBuilder("""
                select count(*)\s
                from Worker w
                inner join Person p
                on (w.person = p)
                where\s""");
        for (Map.Entry<String, Object> entry: filters.entrySet()){
            if (Objects.equals(entry.getKey(), "birthday")){
                query.append(entry.getKey());
                query.append(" = ");
                query.append(":").append("birth");
                query.append(" and ");
            } else {
                query.append(entry.getKey());
                query.append(" = ");
                query.append(entry.getValue());
                query.append(" and ");
            }
        }
        query.delete(query.length() - 5, query.length() - 1);
        log.info(query.toString());
        return query.toString();
    }


    private WorkerDTO covertToDTO(Worker worker){
        DateFormat df = new SimpleDateFormat("E MMM d HH:mm:ss 'MSD' yyyy", Locale.US);
        WorkerDTO dto = new WorkerDTO();
        CoordinatesDTO coordinatesDTO = new CoordinatesDTO();
        LocationDTO locationDTO = new LocationDTO();
        PersonDTO personDTO = new PersonDTO();
        coordinatesDTO.setX(String.valueOf(worker.getCoordinates().getX()));
        coordinatesDTO.setY(String.valueOf(worker.getCoordinates().getY()));
        if (worker.getPerson() != null) {
            locationDTO.setName(worker.getPerson().getLocation().getName());
            locationDTO.setX(String.valueOf(worker.getPerson().getLocation().getX()));
            locationDTO.setY(String.valueOf(worker.getPerson().getLocation().getY()));
            locationDTO.setZ(String.valueOf(worker.getPerson().getLocation().getZ()));
            personDTO.setLocation(locationDTO);
            personDTO.setEyeColor(String.valueOf(worker.getPerson().getEyeColor()));
            personDTO.setHairColor(String.valueOf(worker.getPerson().getHairColor()));
            personDTO.setNationality(String.valueOf(worker.getPerson().getNationality()));
            personDTO.setBirthday(df.format(worker.getPerson().getBirthday()));
        } else {
            personDTO = null;
        }
        dto.setCoordinates(coordinatesDTO);
        dto.setPerson(personDTO);
        dto.setName(worker.getName());
        dto.setSalary(String.valueOf(worker.getSalary()));
        dto.setStatus(String.valueOf(worker.getStatus()));
        dto.setCreationDate(worker.getCreationDate());
        dto.setEndDate(String.valueOf(worker.getEndDate()));
        dto.setStartDate(df.format(worker.getStartDate()));
        dto.setId(worker.getId());
        return dto;

    }
}
