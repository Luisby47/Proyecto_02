package una.ac.cr.proyecto_02.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import una.ac.cr.proyecto_02.entity.DailySchedule;
import una.ac.cr.proyecto_02.service.Service;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/schedule")
public class ScheduleController {

    @Autowired
    private Service service;


    @GetMapping("/{date}")
    public List<DailySchedule> getDailySchedule(@PathVariable("date") LocalDate date) {
        return service.getDailyScheduleForDate(date);
    }
}
