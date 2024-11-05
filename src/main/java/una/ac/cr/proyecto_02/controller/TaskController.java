package una.ac.cr.proyecto_02.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import una.ac.cr.proyecto_02.entity.DailySchedule;
import una.ac.cr.proyecto_02.entity.TaskEntity;
import una.ac.cr.proyecto_02.repository.DailyScheduleRepository;
import una.ac.cr.proyecto_02.service.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/task")
public class TaskController {

    @Autowired
    private Service service;




    @Autowired
    private DailyScheduleRepository dailyScheduleRepository;

    // Endpoint para obtener todas las tareas
    @GetMapping
    public List<TaskEntity> getAllTasks() {
        return service.getAllTasks();
    }

    // Endpoint para crear una nueva tarea
    @PostMapping
    public TaskEntity createTask(@RequestBody TaskEntity task) {
        return service.createTask(task);
    }

    // Endpoint para actualizar una tarea existente
    @PutMapping("/{taskId}")
    public TaskEntity updateTask(@PathVariable Long taskId, @RequestBody TaskEntity task) {
        return service.updateTask(taskId, task);
    }

    // Endpoint para eliminar una tarea
    @DeleteMapping("/{taskId}")
    public void deleteTask(@PathVariable Long taskId) {
        service.deleteTask(taskId);
    }

    // Endpoint para obtener el plan optimizado de tareas
    @GetMapping("/optimize")
    public List<TaskEntity> getOptimizedSchedule() {
        return service.getOptimizedSchedule();
    }


    // Metodo para obtener el plan optimizado y guardarlo en DailySchedule
    public List<DailySchedule> saveDailySchedule(List<TaskEntity> optimizedTasks) {
        LocalDate today = LocalDate.now();
        LocalDateTime startTime = LocalDateTime.now(); // Tiempo de inicio de la primera tarea

        List<DailySchedule> scheduleEntries = new ArrayList<>();

        for (TaskEntity task : optimizedTasks) {
            DailySchedule schedule = new DailySchedule();
            schedule.setDate(today);
            schedule.setTask(task);
            schedule.setStartTime(startTime);

            // Calcula el tiempo de finalización de la tarea
            LocalDateTime endTime = startTime.plusMinutes(task.getEstimatedTime());
            schedule.setEndTime(endTime);

            // Guarda el registro en la base de datos
            dailyScheduleRepository.save(schedule);
            scheduleEntries.add(schedule);

            // Actualiza el tiempo de inicio para la siguiente tarea
            startTime = endTime;
        }

        return scheduleEntries;
    }
}
