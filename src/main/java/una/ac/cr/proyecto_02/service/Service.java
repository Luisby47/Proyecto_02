package una.ac.cr.proyecto_02.service;


import org.springframework.beans.factory.annotation.Autowired;
import una.ac.cr.proyecto_02.entity.DailySchedule;
import una.ac.cr.proyecto_02.entity.Priority;
import una.ac.cr.proyecto_02.entity.TaskEntity;
import una.ac.cr.proyecto_02.entity.WeatherCondition;
import una.ac.cr.proyecto_02.repository.DailyScheduleRepository;
import una.ac.cr.proyecto_02.repository.TaskRepository;

import org.jpl7.Query;
import org.jpl7.Term;
import org.jpl7.Variable;
import una.ac.cr.proyecto_02.repository.WeatherConditionRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@org.springframework.stereotype.Service
public class Service {


    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private WeatherConditionRepository weatherConditionRepository;

    @Autowired
    private DailyScheduleRepository dailyScheduleRepository;


    // Obtener todas las tareas
    public List<TaskEntity> getAllTasks() {
        return taskRepository.findAll();
    }

    // Crear una tarea
    public TaskEntity createTask(TaskEntity task) {
        return taskRepository.save(task);
    }

    // Actualizar una tarea
    public TaskEntity updateTask(Long taskId, TaskEntity task) {
        TaskEntity existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        existingTask.setName(task.getName());
        existingTask.setPriority(task.getPriority());
        existingTask.setEstimatedTime(task.getEstimatedTime());
        existingTask.setDeadline(task.getDeadline());
        existingTask.setWeatherRequirement(task.getWeatherRequirement());
        return taskRepository.save(existingTask);
    }

    // Eliminar una tarea
    public void deleteTask(Long taskId) {
        taskRepository.deleteById(taskId);
    }

    // Optimizar el plan de tareas utilizando Prolog
    public List<TaskEntity> getOptimizedSchedule() {
        List<TaskEntity> tasks = taskRepository.findAll();

        // Cargar el archivo de reglas de Prolog (asume que tienes un archivo .pl con la lógica)
        Query loadQuery = new Query("consult", new Term[]{new org.jpl7.Atom("path/to/rules.pl")});
        if (!loadQuery.hasSolution()) {
            throw new RuntimeException("Failed to load Prolog rules.");
        }

        // Crear las consultas y las variables necesarias
        Variable optimizedTasksVar = new Variable("OptimizedTasks");

        // Generar las restricciones y la lista de tareas en formato Prolog
        String tasksPrologList = tasksToPrologList(tasks);
        String queryStr = "optimize_schedule(" + tasksPrologList + ", " + optimizedTasksVar + ")";

        // Ejecutar la consulta Prolog
        Query query = new Query(queryStr);

        return parsePrologResponse(query.oneSolution().get(optimizedTasksVar.name()));
    }

    // Convierte la lista de tareas en un formato compatible con Prolog
    private String tasksToPrologList(List<TaskEntity> tasks) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < tasks.size(); i++) {
            TaskEntity task = tasks.get(i);
            sb.append("task(")
                    .append(task.getTaskId()).append(", ")
                    .append("'").append(task.getName()).append("', ")
                    .append(task.getPriority()).append(", ")
                    .append(task.getEstimatedTime()).append(", ")
                    .append(task.getDeadline()).append(", ")
                    .append(task.getWeatherRequirement() != null ? "'" + task.getWeatherRequirement().getCondition() + "'" : "''")
                    .append(")");
            if (i < tasks.size() - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }


    // Convierte la respuesta de Prolog a una lista de objetos Task en Java
    private List<TaskEntity> parsePrologResponse(Term prologResponse) {
        // Implementa la lógica para convertir el resultado de Prolog a objetos Task en Java
        // Ejemplo: Prolog devuelve una lista de IDs ordenados, tú buscas las tareas correspondientes
        List<TaskEntity> optimizedTasks = new ArrayList<>();

        // Verificar si la respuesta es una lista
        if (prologResponse.isList()) {
            Term[] taskTerms = prologResponse.listToTermArray();

            for (Term term : taskTerms) {
                if (term.isCompound() && term.name().equals("task")) {
                    // Extraer argumentos en el orden de task(ID, Name, Priority, EstimatedTime, WeatherRequirement)
                    Long id = Long.parseLong(term.arg(1).toString());
                    String name = term.arg(2).name();
                    Priority priority = Priority.valueOf(term.arg(3).name().toUpperCase());
                    int estimatedTime = Integer.parseInt(term.arg(4).toString());
                    LocalDateTime deadline = LocalDateTime.from(LocalDate.parse(term.arg(5).toString()));
                    String weatherConditionName = term.arg(6).name();

                    // Buscar el WeatherCondition en la base de datos por nombre (si existe)
                    WeatherCondition weatherCondition = weatherConditionRepository
                            .findByCondition(weatherConditionName)
                            .orElse(null);  // si no existe, establece null

                    // Crear el objeto TaskEntity con los valores obtenidos
                    TaskEntity task = new TaskEntity();
                    task.setTaskId(id);
                    task.setName(name);
                    task.setPriority(priority);
                    task.setEstimatedTime(estimatedTime);
                    task.setDeadline(deadline);
                    task.setWeatherRequirement(weatherCondition);

                    optimizedTasks.add(task);
                }
            }
        } else {
            System.out.println("La respuesta de Prolog no es una lista válida de tareas.");
        }

        return optimizedTasks;
    }

    public List<DailySchedule> getDailyScheduleForDate(LocalDate date) {
        return dailyScheduleRepository.findByDate(date);
    }
}
