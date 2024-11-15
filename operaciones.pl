% Reglas para optimización de tareas
optimize_schedule(Tasks, OptimizedTasks) :-
    sort_tasks_by_priority(Tasks, SortedByPriority),
    apply_dependencies(SortedByPriority, OptimizedTasks),
    check_time_restrictions(OptimizedTasks).

% Ordena las tareas por prioridad
sort_tasks_by_priority(Tasks, SortedTasks) :-
    predsort(compare_tasks, Tasks, SortedTasks).

% Comparación personalizada por prioridad
compare_tasks(<, task(_, _, high, _, _, _), task(_, _, medium, _, _, _)).
compare_tasks(<, task(_, _, high, _, _, _), task(_, _, low, _, _, _)).
compare_tasks(<, task(_, _, medium, _, _, _), task(_, _, low, _, _, _)).
compare_tasks(>, _, _).

% Asegurar que las tareas se ejecuten después de sus dependencias
apply_dependencies([], []).
apply_dependencies([Task | Rest], [Task | OrderedRest]) :-
    can_execute_after_dependencies(Task, OrderedRest),
    apply_dependencies(Rest, OrderedRest).

% Verificar que una tarea solo se ejecute después de sus dependencias
can_execute_after_dependencies(task(TaskId, _, _, _, _, _), OrderedTasks) :-
    findall(DependencyId, dependency(TaskId, DependencyId), Dependencies),
    subset(Dependencies, OrderedTasks).

% Definir dependencias entre tareas
% dependency(Tarea, Dependencia) significa que Tarea depende de Dependencia
dependency(task2, task1). % Ejemplo: la tarea 2 depende de la tarea 1
dependency(task3, task2).

% Verifica que todas las tareas se puedan completar en el tiempo disponible
check_time_restrictions(Tasks) :-
    total_estimated_time(Tasks, TotalTime),
    max_time_available(MaxTime),
    TotalTime =< MaxTime.

% Calculo del tiempo total estimado
total_estimated_time([], 0).
total_estimated_time([task(_, _, _, Time, _, _) | Rest], Total) :-
    total_estimated_time(Rest, RestTotal),
    Total is RestTotal + Time.

% Asume que MaxTime se obtiene desde el entorno de Prolog
max_time_available(1440). % Por ejemplo, 1440 minutos para un día completo
