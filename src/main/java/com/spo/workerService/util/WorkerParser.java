package com.spo.workerService.util;

import com.spo.workerService.dto.CreateWorkerDTO;
import com.spo.workerService.dto.EditWorkerDTO;
import com.spo.workerService.dto.FindPersonDTO;
import com.spo.workerService.entity.*;
import jakarta.enterprise.context.Dependent;
import lombok.NoArgsConstructor;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.*;

@Dependent
@NoArgsConstructor
public class WorkerParser {
    public List<Object> createWorker(CreateWorkerDTO worker) {
        List<String> errors = new ArrayList<>();
        List<Object> out = new ArrayList<>();
        Date date = null;
        Integer salary = null;
        LocalDateTime endDate = null;
        Status status = null;
        Long coordinates_x = null;
        Float coordinates_y = null;
        Double location_x = null;
        Double location_y = null;
        Float location_z = null;
        Date birthday = null;
        EyeColor eyeColor = null;
        HairColor hairColor = null;
        Country nationality = null;

        if (worker.getPerson() == null){
            errors.add("Person can not be null");
        } else if (worker.getPerson().getLocation() == null) {
            errors.add("Location can not be null");
        }
        if (worker.getCoordinates() == null){
            errors.add("Coordinates can not be null");
        }
        if (!errors.isEmpty()) {
            out.add(null);
            out.add(errors);
            return out;
        }

        if (worker.getName() == null) {
            errors.add("Name can not be null");
        }
        if (worker.getSalary() == null) {
            errors.add("Salary can not be null");
        }
        if (worker.getStartDate() == null) {
            errors.add("StartDate can not be null");
        }
        if (worker.getPerson().getHairColor() == null){
            errors.add("Person hair color can not be null");
        }
        if (worker.getPerson().getLocation().getY() == null){
            errors.add("Location Y can not be null");
        }
        if (worker.getPerson().getLocation().getName() == null){
            errors.add("Location name can not be null");
        }

        if (!errors.isEmpty()) {
            out.add(null);
            out.add(errors);
            return out;
        }

        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'");
            df.setTimeZone(TimeZone.getTimeZone(ZoneId.of("Europe/London")));
            date = df.parse(worker.getStartDate());
        } catch (ParseException e) {
            errors.add("Illegal Start-date format. It should be yyyy-MM-dd'T'HH:mm:ss.S'Z'");
        }
        try {
            salary = Integer.valueOf(worker.getSalary());
            if (salary < 1) {
                errors.add("Salary can not be less then 1");
            }
        } catch (NumberFormatException e) {
            errors.add("Salary can not be string");
        }
        try {
            if (worker.getStatus() != null) {
                status = Status.valueOf(worker.getStatus().toUpperCase());
            }
        } catch (IllegalArgumentException e) {
            errors.add("Illegal status");
        }
        if (worker.getEndDate() != null) {
            try {
                endDate = LocalDateTime.parse(worker.getEndDate());
            } catch (DateTimeParseException e) {
                errors.add("Illegal End-date format. It should be yyyy-MM-dd'T'HH:mm:ss.S");
            }
        }
        if (worker.getCoordinates().getX() != null){
            try{
                coordinates_x = Long.parseLong(worker.getCoordinates().getX());
            } catch (NumberFormatException e){
                errors.add("Coordinates x can not be string");
            }
        }

        if (worker.getCoordinates().getY() != null){
            try{
                coordinates_y = Float.parseFloat(worker.getCoordinates().getY());
            } catch (NumberFormatException e){
                errors.add("Coordinates y can not be string");
            }
        }

        if (worker.getPerson().getLocation().getX() != null){
            try{
                location_x = Double.parseDouble(worker.getPerson().getLocation().getX());
            } catch (NumberFormatException e){
                errors.add("Location x can not be string");
            }
        }

        if (worker.getPerson().getLocation().getZ() != null){
            try{
                location_z = Float.parseFloat(worker.getPerson().getLocation().getZ());
            } catch (NumberFormatException e){
                errors.add("Location z can not be string");
            }
        }

        try{
            location_y = Double.parseDouble(worker.getPerson().getLocation().getY());
        } catch (NumberFormatException e){
            errors.add("Location y can not be string");
        }

        if (worker.getPerson().getBirthday() != null) {
            try {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'");
                birthday = df.parse(worker.getPerson().getBirthday());
            } catch (ParseException e) {
                errors.add("Illegal birthday-date format. It should be yyyy-MM-dd'T'HH:mm:ss.S'Z'");
            }
        }
        if (worker.getPerson().getEyeColor() != null) {
            try {
                eyeColor = EyeColor.valueOf(worker.getPerson().getEyeColor().toUpperCase());
            } catch (IllegalArgumentException e) {
                errors.add("Illegal eyeColor type");
            }
        }
        if (worker.getPerson().getHairColor() != null) {
            try {
                hairColor = HairColor.valueOf(worker.getPerson().getHairColor().toUpperCase());
            } catch (IllegalArgumentException e) {
                errors.add("Illegal hairColor type");
            }
        }
        if (worker.getPerson().getNationality() != null) {
            try {
                nationality = Country.valueOf(worker.getPerson().getNationality().toUpperCase());
            } catch (IllegalArgumentException e) {
                errors.add("Illegal nationality type");
            }
        }


        if (!errors.isEmpty()) {
            out.add(null);
            out.add(errors);
            return out;
        }

        Coordinates coordinates = new Coordinates(coordinates_x, coordinates_y);
        Location location = new Location(location_x, location_y, location_z, worker.getPerson().getLocation().getName());
        Person person = new Person(birthday.toString(), eyeColor, hairColor, nationality, location);
        Worker worker1 = new Worker(worker.getName(), coordinates, salary, date, endDate, status, person);

        out.add(worker1);
        out.add(null);
        return out;
    }

    public List<Object> patchWorker(int id, EditWorkerDTO worker) {
        Date date = null;
        LocalDate creationDate = null;
        Integer salary = null;
        LocalDateTime endDate = null;
        Status status = null;
        List<String> errors = new ArrayList<>();
        List<Object> out = new ArrayList<>();
        if (worker.getCreationDate() == null) {
            errors.add("CreationDate can not be null");
        }
        if (worker.getName() == null) {
            errors.add("Name can not be null");
        }
        if (worker.getSalary() == null) {
            errors.add("Salary can not be null");
        }
        if (worker.getStartDate() == null) {
            errors.add("StartDate can not be null");
        }
        if (!errors.isEmpty()) {
            out.add(null);
            out.add(errors);
            return out;
        }
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'");
            df.setTimeZone(TimeZone.getTimeZone(ZoneId.of("Europe/London")));
            date = df.parse(worker.getStartDate());
        } catch (ParseException e) {
            errors.add("Illegal Start-date format. It should be yyyy-MM-dd'T'HH:mm:ss.S'Z'");
        }
        try {
            creationDate = LocalDate.parse(worker.getCreationDate());
        } catch (DateTimeParseException e) {
            errors.add("Illegal Creation-date format. It should be yyyy-MM-dd");
        }
        try {
            salary = Integer.valueOf(worker.getSalary());
            if (salary < 1) {
                errors.add("Salary can not be less then 1");
            }
        } catch (NumberFormatException e) {
            errors.add("Salary can not be string");
        }
        try {
            if (worker.getStatus() != null) {
                status = Status.valueOf(worker.getStatus().toUpperCase());
            }
        } catch (IllegalArgumentException e) {
            errors.add("Illegal status");
        }
        if (worker.getEndDate() != null) {
            try {
                endDate = LocalDateTime.parse(worker.getEndDate());
            } catch (DateTimeParseException e) {
                errors.add("Illegal End-date format. It should be yyyy-MM-dd'T'HH:mm:ss.S");
            }
        }
        if (!errors.isEmpty()) {
            out.add(null);
            out.add(errors);
            return out;
        }
        out.add(new Worker(id, worker.getName(), null, creationDate, salary, date, endDate, status, null));
        out.add(null);
        return out;
    }


    public List<Object> findCountWorkers(FindPersonDTO personDTO) {
        List<String> errors = new ArrayList<>();
        Map<String, Object> filters = new HashMap<>();
        List<Object> out = new ArrayList<>();
        if (personDTO.getBirthday() != null) {
            try {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'");
                Date date = df.parse(personDTO.getBirthday());
                String birthday = "'" + date + "'";
                filters.put("birthday", birthday);
            } catch (ParseException e) {
                errors.add("Illegal birthday-date format. It should be yyyy-MM-dd'T'HH:mm:ss.S'Z'");
            }
        }
        if (personDTO.getEyeColor() != null) {
            try {
                EyeColor eyeColor = EyeColor.valueOf(personDTO.getEyeColor().toUpperCase());
                filters.put("eyeColor", eyeColor.name());
            } catch (IllegalArgumentException e) {
                errors.add("Illegal eyeColor type");
            }
        }
        if (personDTO.getHairColor() != null) {
            try {
                HairColor hairColor = HairColor.valueOf(personDTO.getHairColor().toUpperCase());
                filters.put("hairColor", hairColor.name());
            } catch (IllegalArgumentException e) {
                errors.add("Illegal hairColor type");
            }
        }
        if (personDTO.getNationality() != null) {
            try {
                Country nationality = Country.valueOf(personDTO.getNationality().toUpperCase());
                filters.put("nationality", nationality.name());
            } catch (IllegalArgumentException e) {
                errors.add("Illegal nationality type");
            }
        }
        if (!errors.isEmpty()) {
            out.add(null);
            out.add(errors);
            return out;
        }
        if (filters.isEmpty()) {
            out.add(null);
            out.add(errors.add("You should specify at least one filter"));
            return out;
        }
        out.add(filters);
        out.add(null);
        return out;
    }

    public List<Object> parseFilters(List<String> filters){
        List<String> errors = new ArrayList<>();
        List<Object> out = new ArrayList<>();
        List<String> fltrs = new ArrayList<>();
        String oldFilter;
        for (String filter: filters){
            oldFilter = filter;
            if (!filter.contains("[")){
                errors.add("Illegal filter: " + oldFilter);
            }
            filter = filter.replace("[l]"," < ").replace("[le]", " <= ")
                    .replace("[g]"," > ").replace("[ge]"," >= ")
                    .replace("[cmp]", " = ").replace("[cmpn]"," != ");
            if (filter.contains("[")){
                filter = filter.replace("[con]", " similar to '%");
                filter += "%'";
            }
            if (filter.contains("[")){
                errors.add("Illegal filter command: " + oldFilter);
            }
            fltrs.add(filter);

        }
        if (!errors.isEmpty()){
            out.add(null);
            out.add(errors);
            return out;
        }

        out.add(fltrs);
        out.add(null);
        return out;
    }

    public List<Object> parseSorts(List<String> sorts){
        List<String> errors = new ArrayList<>();
        List<Object> out = new ArrayList<>();
        List<String> srts = new ArrayList<>();
        String oldSort;
        for (String sort: sorts) {
            oldSort = sort;
            if (!sort.contains("[")) {
                errors.add("Illegal sort: " + oldSort);
            }
            sort = sort.replace("[asc]", " ASC ").replace("[desc]", " DESC ");
            if (sort.contains("[")) {
                errors.add("Illegal sort command: " + oldSort);
            }
            srts.add(sort);
        }

        if (!errors.isEmpty()){
            out.add(null);
            out.add(errors);
            return out;
        }

        out.add(srts);
        out.add(null);
        return out;
    }
}

