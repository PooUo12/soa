package com.spo.workerService.util;

import com.spo.workerService.dto.CreateWorkerDTO;
import com.spo.workerService.dto.EditWorkerDTO;
import com.spo.workerService.dto.FindPersonDTO;
import com.spo.workerService.entity.*;
import jakarta.enterprise.context.Dependent;
import lombok.NoArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import java.sql.Timestamp;
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
    private static final Log log = LogFactory.getLog(WorkerParser.class);

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
        Person person = null;

        if (worker.getPerson() != null){
            if (worker.getPerson().getLocation() == null) {
                errors.add("Location can not be null");
            }
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
        if (worker.getPerson() != null) {
            if (worker.getPerson().getHairColor() == null) {
                errors.add("Person hair color can not be null");
            }
            if (worker.getPerson().getLocation().getY() == null) {
                errors.add("Location Y can not be null");
            }
            if (worker.getPerson().getLocation().getName() == null) {
                errors.add("Location name can not be null");
            }
        }

        if (!errors.isEmpty()) {
            out.add(null);
            out.add(errors);
            return out;
        }

        try {
            DateFormat df = new SimpleDateFormat("E MMM d HH:mm:ss 'MSD' yyyy", Locale.US);
            df.setTimeZone(TimeZone.getTimeZone(ZoneId.of("Europe/London")));
            date = df.parse(worker.getStartDate());
        } catch (ParseException e) {
            errors.add("Illegal Start-date format. It should be E MMM d HH:mm:ss 'MSD' yyyy");
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
                if (coordinates_x <= -106){
                    errors.add("Coordinates x should be bigger than -106");
                }
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

        if (worker.getPerson() != null) {

            if (worker.getPerson().getLocation().getX() != null) {
                try {
                    location_x = Double.parseDouble(worker.getPerson().getLocation().getX());
                } catch (NumberFormatException e) {
                    errors.add("Location x can not be string");
                }
            }

            if (worker.getPerson().getLocation().getZ() != null) {
                try {
                    location_z = Float.parseFloat(worker.getPerson().getLocation().getZ());
                } catch (NumberFormatException e) {
                    errors.add("Location z can not be string");
                }
            }

            try {
                location_y = Double.parseDouble(worker.getPerson().getLocation().getY());
            } catch (NumberFormatException e) {
                errors.add("Location y can not be string");
            }

            if (worker.getPerson().getBirthday() != null) {
                try {
                    DateFormat df = new SimpleDateFormat("E MMM d HH:mm:ss 'MSD' yyyy", Locale.US);
                    birthday = df.parse(worker.getPerson().getBirthday());
                } catch (ParseException e) {
                    errors.add("Illegal Start-date format. It should be E MMM d HH:mm:ss 'MSD' yyyy");
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
        }


        if (!errors.isEmpty()) {
            out.add(null);
            out.add(errors);
            return out;
        }

        Coordinates coordinates = new Coordinates(coordinates_x, coordinates_y);
        if (worker.getPerson() != null) {
            Location location = new Location(location_x, location_y, location_z, worker.getPerson().getLocation().getName());
            person = new Person(birthday, eyeColor, hairColor, nationality, location);
        }
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
            DateFormat df = new SimpleDateFormat("E MMM d HH:mm:ss 'MSD' yyyy", Locale.US);
            df.setTimeZone(TimeZone.getTimeZone(ZoneId.of("Europe/London")));
            date = df.parse(worker.getStartDate());
        } catch (ParseException e) {
            errors.add("Illegal Start-date format. It should be E MMM d HH:mm:ss 'MSD' yyyy");
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
                DateFormat df = new SimpleDateFormat("E MMM d HH:mm:ss 'MSD' yyyy", Locale.US);
                Date date = df.parse(personDTO.getBirthday());
                Timestamp birthday = new Timestamp(date.getTime());
//                String birthday = "'" + date + "'";
                filters.put("birthday", birthday);
            } catch (ParseException e) {
                errors.add("Illegal Start-date format. It should be E MMM d HH:mm:ss 'MSD' yyyy");
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
            errors.add("You should specify at least one filter");
            out.add(errors);
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
            log.info(filter);
            if (!filter.contains("[")){
                errors.add("Illegal filter: " + oldFilter);
            }
            filter = filter.replace("[l]"," < ").replace("[le]", " <= ")
                    .replace("[g]"," > ").replace("[ge]"," >= ")
                    .replace("[cmp]", " = ").replace("[cmpn]"," != ")
                    .replace("workername", "w.name").replace("id", "w.id")
                    .replace("coordinatesy", "c.y").replace("coordinatesx", "c.x")
                    .replace("birthday", "p.birthday")
                    .replace("creationdate", "w.creationDate").replace("startdate", "w.startDate")
                    .replace("enddate", "w.endDate")
                    .replace("eyecolor", "p.eyeColor").replace("haircolor", "p.hairColor")
                    .replace("nationality", "p.nationality").replace("locationx", "l.x")
                    .replace("locationy", "l.y").replace("locationz", "l.z")
                    .replace("locationname", "l.name");

            if (filter.contains("[con]")){
                filter = filter.replace("[con]", " like '%");
                filter += "%'";
                if (!filter.contains("name")){
                    errors.add("Contains can only be applied to name: " + oldFilter);
                    continue;
                } else {
                    fltrs.add(filter);
                    continue;
                }
            }
            if (filter.contains("[")){
                errors.add("Illegal filter command: " + oldFilter);
                out.add(null);
                out.add(errors);
                return out;
            }
            if (filter.contains("name")){
                var nameFilter = filter;
                int index = filter.lastIndexOf(" ");
                filter = nameFilter.substring(0, index) + "'" + nameFilter.substring(index+1) + "'";
            }
            if (filter.contains("status")){
                var nameFilter = filter;
                int index = filter.lastIndexOf(" ");
                try{
                filter = nameFilter.substring(0, index) + Status.valueOf(nameFilter.substring(index+1).toUpperCase());

                } catch (IllegalArgumentException e){
                    errors.add("Illegal status value");
                }
            }
            if (filter.contains("hairColor")){
                var nameFilter = filter;
                int index = filter.lastIndexOf(" ");
                try {
                    filter = nameFilter.substring(0, index) + HairColor.valueOf(nameFilter.substring(index + 1).toUpperCase());
                } catch (IllegalArgumentException e){
                    errors.add("Illegal hairColor value");
                }
            }
            if (filter.contains("eyeColor")){
                var nameFilter = filter;
                int index = filter.lastIndexOf(" ");
                try{
                filter = nameFilter.substring(0, index) + EyeColor.valueOf(nameFilter.substring(index+1).toUpperCase());
                } catch (IllegalArgumentException e){
                    errors.add("Illegal eyeColor value");
                }
            }
            if (filter.contains("nationality")){
                var nameFilter = filter;
                int index = filter.lastIndexOf(" ");
                try {
                    filter = nameFilter.substring(0, index) + Country.valueOf(nameFilter.substring(index + 1).toUpperCase());
                } catch (IllegalArgumentException e){
                    errors.add("Illegal nationality value");
                }
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
            sort = sort.replace("[asc]", " ASC ").replace("[desc]", " DESC ")
                    .replace("workername", "w.name").replace("id", "w.id")
                    .replace("coordinatesy", "c.y").replace("coordinatesx", "c.x")
                    .replace("eyecolor", "p.eyeColor").replace("haircolor", "p.hairColor")
                    .replace("nationality", "p.nationality").replace("locationx", "l.x")
                    .replace("locationy", "l.y").replace("locationz", "l.z")
                    .replace("locationname", "l.name").replace("birthday", "p.birthday")
                    .replace("creationdate", "w.creationDate").replace("startdate", "w.startDate")
                    .replace("enddate", "w.endDate");
            if (sort.contains("[")) {
                errors.add("Illegal sort command: " + oldSort);
            }
            srts.add(sort);
            log.info(sort);
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

