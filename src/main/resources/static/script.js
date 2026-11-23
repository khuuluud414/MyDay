
const API_URL = "/api/tasks";

const taskForm = document.getElementById("taskForm");
const taskInput = document.getElementById("taskInput");
const taskList = document.getElementById("taskList");
const message = document.getElementById("message");

// Charger les tâches
async function loadTasks() {
    try {
        const response = await fetch(API_URL);

        if (!response.ok) {
            throw new Error("Erreur de chargement");
        }

        const tasks = await response.json();
        taskList.innerHTML = "";

        tasks.forEach(task => displayTask(task));

    } catch (error) {
        message.textContent = "Impossible de charger les tâches.";
    }
}

// Afficher une tâche
function displayTask(task) {
    const li = document.createElement("li");
    li.className = "task-item";

    const span = document.createElement("span");
    span.className = "task-title";
    span.textContent = task.title;

    if (task.completed) {
        span.classList.add("completed");
    }

    const completeButton = document.createElement("button");
    completeButton.textContent = task.completed ? "Undo" : "Done";

    completeButton.addEventListener("click", async () => {
        const response = await fetch(
            `${API_URL}/${task.id}/toggle`,
            { method: "PATCH" }
        );

        if (response.ok) {
            loadTasks();
        }
    });

    const deleteButton = document.createElement("button");
    deleteButton.textContent = "Delete";
    deleteButton.className = "delete-button";

    deleteButton.addEventListener("click", async () => {
        const response = await fetch(
            `${API_URL}/${task.id}`,
            { method: "DELETE" }
        );

        if (response.ok) {
            loadTasks();
        }
    });

    li.appendChild(span);
    li.appendChild(completeButton);
    li.appendChild(deleteButton);

    taskList.appendChild(li);
}

// Ajouter une tâche
taskForm.addEventListener("submit", async (event) => {
    event.preventDefault();

    const title = taskInput.value.trim();

    if (title === "") {
        message.textContent = "Veuillez saisir une tâche.";
        return;
    }

    try {
        const response = await fetch(API_URL, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ title: title })
        });

        if (!response.ok) {
            throw new Error("Erreur d'ajout");
        }

        taskInput.value = "";
        message.textContent = "";
        await loadTasks();

    } catch (error) {
        message.textContent = "Impossible d'ajouter la tâche.";
    }
});

// Charger les tâches au démarrage
loadTasks();
