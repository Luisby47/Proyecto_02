

% Reglas de optimización de tareas
optimize_schedule(Tasks, OptimizedTasks) :-
    sort_tasks_by_priority(Tasks, SortedByPriority),
    apply_dependencies(SortedByPriority, OptimizedTasks).
% Ordena las tareas por prioridad
sort_tasks_by_priority(Tasks, SortedTasks) :-
    % Ordena por prioridad y otras restricciones
    predsort(compare_tasks, Tasks, SortedTasks).

% Comparación personalizada por prioridad
compare_tasks(<, task(_, _, high, _, _), task(_, _, medium, _, _)).
compare_tasks(<, task(_, _, high, _, _), task(_, _, low, _, _)).
compare_tasks(<, task(_, _, medium, _, _), task(_, _, low, _, _)).
compare_tasks(>, _, _).
