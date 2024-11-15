package una.ac.cr.proyecto_02.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import una.ac.cr.proyecto_02.entity.TaskDependency;
import una.ac.cr.proyecto_02.entity.TaskEntity;
import una.ac.cr.proyecto_02.entity.WeatherCondition;
import una.ac.cr.proyecto_02.service.Service;

import java.util.List;

@RestController
@RequestMapping("/api/task")
public class TaskController {

    @Autowired
    private Service service;




    // ---------------------------  Tareas --------------------------------//
    @GetMapping
    public ResponseEntity<List<TaskEntity>> getAllTasks() {
        List<TaskEntity> tasks = service.getAllTasks();
        return ResponseEntity.ok(tasks);
    }


    @GetMapping("/{taskId}")
    public ResponseEntity<TaskEntity> getTaskById(@PathVariable Long taskId) {
        TaskEntity task = service.getTaskById(taskId);
        return ResponseEntity.ok(task);
    }


    @PostMapping
    public ResponseEntity<TaskEntity> createTask(@RequestBody TaskEntity task) {
        TaskEntity newTask = service.createTask(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(newTask);
    }


    @PutMapping("/{taskId}")
    public ResponseEntity<TaskEntity> updateTask(@PathVariable Long taskId, @RequestBody TaskEntity task) {
        TaskEntity updatedTask = service.updateTask(taskId, task);
        return ResponseEntity.ok(updatedTask);
    }


    @DeleteMapping("/{taskId}")
    public void deleteTask(@PathVariable Long taskId) {
        service.deleteTask(taskId);
    }




    // ---------------------------  Condiciones del clima --------------------------------//
    @GetMapping("/weather")
    public ResponseEntity<List<WeatherCondition>> getAllWeatherConditions() {
        List<WeatherCondition> weatherConditions = service.getAllWeatherConditions();
        return ResponseEntity.ok(weatherConditions);
    }


    // ---------------------------  Dependencias de tareas --------------------------------//
    @GetMapping("/{taskId}/dependencies")
    public List<TaskDependency> getTaskDependencies(@PathVariable Long taskId) {
        return service.getDependencies(taskId);
    }


    // ---------------------------  Optimizar tareas --------------------------------//
    @GetMapping("/optimize")
    public ResponseEntity<List<TaskEntity>> getOptimizedSchedule(
            @RequestParam(name = "availableTime", defaultValue = "480") int availableTime) {
        try {
            List<TaskEntity> optimizedTasks = service.getOptimizedSchedule(availableTime);
            return ResponseEntity.ok(optimizedTasks);
        } catch (RuntimeException e) {
            // Manejar el error si no se pueden completar todas las tareas en el tiempo disponible
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }
    }






}
