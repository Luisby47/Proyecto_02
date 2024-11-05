	-- Tabla para las condiciones climáticas específicas
CREATE TABLE weather_condition (
    condition_id INT AUTO_INCREMENT PRIMARY KEY,
    date_time TIMESTAMP NOT NULL,
    `condition` VARCHAR(50),  -- Ej: "soleado", "lluvioso", "nublado"
    temperature INT  -- Temperatura en grados (opcional)
);

-- Tabla principal de tareas con asociación a condición climática
CREATE TABLE tasks (
    task_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    priority ENUM('HIGH', 'MEDIUM', 'LOW'),
    estimated_time INT NOT NULL,  -- en minutos
    deadline TIMESTAMP,
    weather_requirement INT,  -- Referencia a `condition_id`
    CONSTRAINT fk_weather_requirement FOREIGN KEY (weather_requirement) REFERENCES weather_condition(condition_id)
);

-- Tabla para las relaciones de dependencia entre tareas (muchos a muchos)
CREATE TABLE task_dependencies (
    task_id INT NOT NULL,
    dependency_id INT NOT NULL,
    PRIMARY KEY (task_id, dependency_id),
    CONSTRAINT fk_task FOREIGN KEY (task_id) REFERENCES tasks(task_id) ON DELETE CASCADE,
    CONSTRAINT fk_dependency FOREIGN KEY (dependency_id) REFERENCES tasks(task_id) ON DELETE CASCADE,
    CONSTRAINT chk_no_self_dependency CHECK (task_id <> dependency_id)  -- Evita auto-dependencias
);

-- Tabla para el registro de planificación diaria de tareas
CREATE TABLE daily_schedule (
    schedule_id INT AUTO_INCREMENT PRIMARY KEY,
    date DATE NOT NULL,
    task_id INT NOT NULL,
    start_time TIMESTAMP,  -- Hora de inicio de la tarea en el día
    end_time TIMESTAMP,    -- Hora de fin de la tarea en el día
    CONSTRAINT fk_task_in_schedule FOREIGN KEY (task_id) REFERENCES tasks(task_id) ON DELETE CASCADE
);

-- Inserción de datos de ejemplo en weather_condition
INSERT INTO weather_condition (date_time, `condition`, temperature)
VALUES ('2023-11-05 09:00:00', 'soleado', 22),
       ('2023-11-05 11:00:00', 'lluvioso', 18),
       ('2023-11-05 14:00:00', 'nublado', 20);

-- Inserción de datos de ejemplo en tasks, referenciando a `condition_id`
INSERT INTO tasks (name, priority, estimated_time, deadline, weather_requirement)
VALUES ('Comprar comida', 'HIGH', 60, '2023-11-05 09:00:00', 
        (SELECT condition_id FROM weather_condition WHERE `condition` = 'soleado'));

INSERT INTO tasks (name, priority, estimated_time, deadline)
VALUES ('Limpiar la casa', 'MEDIUM', 120, '2023-11-05 11:00:00');

INSERT INTO tasks (name, priority, estimated_time, deadline)
VALUES ('Trabajar en un informe', 'HIGH', 180, '2023-11-05 14:00:00');

INSERT INTO tasks (name, priority, estimated_time, deadline, weather_requirement)
VALUES ('Ejercicio al aire libre', 'LOW', 30, '2023-11-05 07:00:00', 
        (SELECT condition_id FROM weather_condition WHERE `condition` = 'soleado'));


-- Ejemplo de relaciones de dependencia
-- "Trabajar en un informe" depende de "Comprar comida"
INSERT INTO task_dependencies (task_id, dependency_id)
VALUES ((SELECT task_id FROM tasks WHERE name = 'Trabajar en un informe'),
        (SELECT task_id FROM tasks WHERE name = 'Comprar comida'));