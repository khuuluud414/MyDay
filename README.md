# MyDay
Application web de gestion des tâches (Spring Boot + HTML/CSS/JS).

## Architecture
- Frontend: index.html, tasks.html, style.css, script.js
- Backend: Java 17 / Spring Boot (Task, TaskController, TaskRepository)
- Base de données: H2 (fichier)

## Prérequis
Java 17, Maven, Git

## Installation
git clone https://github.com/khuuluud414/MyDay.git
cd MyDay

## Exécution
mvn spring-boot:run
Puis ouvrir http://localhost:8081

## Tests
mvn test
## Conteneurisation Docker

### Prérequis
Java 17, Maven, Docker

### Commandes
```bash
mvn clean package
docker build -t myday:1.0 .
docker run -d -p 8081:8081 --name myday myday:1.0
```
Accès : http://localhost:8081

Pour arrêter : `docker stop myday`

### Choix de sécurité
- Image officielle `eclipse-temurin:17-jre-alpine`
- Exécution avec un utilisateur non-root