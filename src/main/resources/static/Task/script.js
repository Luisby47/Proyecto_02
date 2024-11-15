window.onload = function() {
    getAllTasks();
    loadWeatherConditions();
    loadTasksForDependencies();
};




let backendUrl = 'http://localhost:8080/api/task';
let session = {
    tasks : [],
    task : {
        taskId: null,
        name: '',
        priority: '',
        estimatedTime: 0,
        deadline: '',
        weatherRequirement: {},
        dependencies: []
    },
    tasksOptimize: [],
    weatherConditions: [],
    dependencies: []
};


// Cargar todas las tareas para el selector de dependencias
async function loadTasksForDependencies() {
    try {
        const request = new Request(backendUrl, { method: 'GET' });
        const response = await fetch(request);
        session.dependencies = await response.json();

        //const dependenciesSelect = document.getElementById('dependencies');

        const dependenciesDiv = document.getElementById('dependencies');
        dependenciesDiv.innerHTML = ''; // Limpiar las opciones anteriores

        session.dependencies.forEach(task => {
            const checkbox = document.createElement('div');
            checkbox.classList.add('form-check');

            checkbox.innerHTML = `
                <input class="form-check-input" type="checkbox" id="dep-${task.taskId}" value="${task.taskId}">
                <label class="form-check-label" for="dep-${task.taskId}">${task.name}</label>
            `;
            dependenciesDiv.appendChild(checkbox);
        });
    } catch (error) {
        console.error('Error al cargar las tareas para dependencias:', error);
    }
}
async function loadWeatherConditions() {
    try {
        const request = new Request(backendUrl + `/weather`, { method: 'GET' });
        const response = await fetch(request);
        session.weatherConditions = await response.json();

        //console.log(session.weatherConditions);
        const weatherSelect = document.getElementById('weatherCondition');
        session.weatherConditions.forEach(condition => {
            const option = document.createElement('option');
            option.value = condition.condition_Id;  // ID de la condición climática
            option.textContent = `${condition.condition} (${condition.temperature}°C)`;
            weatherSelect.appendChild(option);
        });
    } catch (error) {
        console.error('Error al cargar condiciones climáticas:', error);
    }
}
async function getAllTasks() {
    try {
        const request = new Request(backendUrl, {method:'GET', headers :{}});
        const response = await fetch(request);
        session.tasks = await response.json();

        displayTasks();
    } catch (error) {
        console.error('Error al obtener tareas:', error);
    }
}

async function createOrUpdateTask() {


    // Obtener las dependencias seleccionadas por el usuario (checkboxes)
    const checkboxes = document.querySelectorAll('#dependencies .form-check-input');


    const selectedDependencies = [];

    for (const checkbox of checkboxes) {
        if (checkbox.checked) {
            const taskId = parseInt(checkbox.value);
            const task = session.dependencies.find(task => task.taskId === taskId);
            if (task) {
                selectedDependencies.push(task);
            }
        }
    }



    const taskData = {
        name: document.getElementById('taskName').value,
        priority: document.getElementById('priority').value,
        estimatedTime: parseInt(document.getElementById('estimatedTime').value),
        deadline: document.getElementById('deadline').value,
        weatherRequirement: { condition_Id: parseInt(document.getElementById('weatherCondition').value) },
        dependencies: selectedDependencies
    };

    try {
        let method = 'POST';
        let url = backendUrl;

        if (session.task.taskId) {
            // Actualizar tarea
            method = 'PUT';
            url += `/${session.task.taskId}`;
        }


        console.log(taskData);
        const response = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(taskData)
        });

        if(!response.ok){ console.log(response.status); return; }

        // Refrescar lista de tareas
        await getAllTasks();
        resetForm();
    } catch (error) {
        console.error('Error al crear/actualizar tarea:', error);
    }
}

function resetForm() {
    document.getElementById('taskForm').reset();
    const checkboxes = document.querySelectorAll('#dependencies .form-check-input');
    checkboxes.forEach(checkbox => {
        checkbox.checked = false;
    });
    session.task.taskId = null;
}

function displayTasks() {
    const tasksBody = document.getElementById('tasksBody');
    tasksBody.innerHTML = ''; // Limpiar la tabla

    session.tasks.forEach(task => {
        const row = document.createElement('tr');
        const formattedDeadline = task.deadline ? task.deadline.replace('T', ' ').slice(0, 19) : '';
        const dependencies = task.dependencies.map(dep => dep.name).join(', ') || 'Ninguna';
        row.innerHTML = `
            <td>${task.taskId}</td>
            <td>${task.name}</td>
            <td>${task.priority}</td>
            <td>${task.estimatedTime}</td>
            <td>${formattedDeadline}</td>
            <td>${task.weatherRequirement ? task.weatherRequirement.condition : 'N/A'}</td>
            <td>${dependencies}</td>
            <td>
                <button class="btn btn-warning btn-sm" onclick="editTask(${task.taskId})">Editar</button>
                <button class="btn btn-danger btn-sm" onclick="deleteTask(${task.taskId})">Eliminar</button>
            </td>
        `;
        tasksBody.appendChild(row);
    });
}

async function deleteTask(taskId) {
    try {
        const request = new Request(backendUrl + `/${taskId}`, { method: 'DELETE' });
        const response = await fetch(request);
        if(!response.ok){ console.log(response.status); return; }

        await getAllTasks();
    } catch (error) {
        console.error('Error al eliminar tarea:', error);
    }
}
async function editTask(taskId) {
    try {
        resetForm();
        const request = new Request(backendUrl + `/${taskId}`, { method: 'GET' });
        const response = await fetch(request);
        session.task = await response.json();
        //console.log(session.task);

        document.getElementById('taskName').value = session.task.name;
        document.getElementById('priority').value = session.task.priority;
        document.getElementById('estimatedTime').value = session.task.estimatedTime;

        if (session.task.deadline) {
            const deadline = new Date(session.task.deadline);
            const year = deadline.getFullYear();
            const month = String(deadline.getMonth() + 1).padStart(2, '0');
            const day = String(deadline.getDate()).padStart(2, '0');
            const hours = String(deadline.getHours()).padStart(2, '0');
            const minutes = String(deadline.getMinutes()).padStart(2, '0');
            document.getElementById('deadline').value = `${year}-${month}-${day}T${hours}:${minutes}`;
        } else {
            document.getElementById('deadline').value = '';
        }
        document.getElementById('weatherCondition').value = session.task.weatherRequirement ? session.task.weatherRequirement.condition_Id : '';

        session.task.dependencies.forEach(dep => {
            document.getElementById(`dep-${dep.taskId}`).checked = true;
        });
        session.task.taskId = taskId;

    } catch (error) {
        console.error('Error al obtener detalles de la tarea:', error);
    }
}

async function getOptimizedTasks() {
    const availableTime = document.getElementById('availableTime').value;
    try {
        const request = new Request(backendUrl + `/optimize?availableTime=${availableTime}`, { method: 'GET' });
        const response = await fetch(request);
        session.tasksOptimize = await response.json();
        displayTasksOptimize();
    } catch (error) {
        console.error('Error al obtener tareas optimizadas:', error);
    }
}

function displayTasksOptimize() {
    const tasksBody = document.getElementById('tasksOptimizeBody');
    tasksBody.innerHTML = ''; // Limpiar la tabla

    session.tasksOptimize.forEach(task => {
        const row = document.createElement('tr');
        const formattedDeadline = task.deadline ? task.deadline.replace('T', ' ').slice(0, 19) : '';
        const dependencies = task.dependencies.map(dep => dep.name).join(', ') || 'Ninguna';

        row.innerHTML = `
            <td>${task.taskId}</td>
            <td>${task.name}</td>
            <td>${task.priority}</td>
            <td>${task.estimatedTime}</td>
            <td>${formattedDeadline}</td>
         

           <td>${task.weatherRequirement ? task.weatherRequirement.condition : 'N/A'}</td>
           <td>${dependencies}</td>
        `;
        tasksBody.appendChild(row);
    });
}

// ---------------------------- Aun sin probrar -------------------------------- //


// <td>${entry.name}</td>