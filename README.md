# SkillRoute

**SkillRoute** is a web-based career development platform designed to bridge the gap between academic knowledge and real-world industry requirements. The application analyzes a student's current skill set, compares it against specific job vacancy requirements, and automatically generates a personalized **Learning Roadmap** to close the identified **Skill Gap**.

## Core Features

* **GitHub API Verification:** A unique verification system that checks a user's GitHub repositories to validate their technical skills based on actual code activity.
* **Smart Skill Gap Analyzer:** A core engine that calculates the delta between a student's current competence levels and a recruiter's expectations.
* **Roadmap Orchestrator:** Automatically creates a step-by-step learning plan, attaching curated educational resources for each missing skill.
* **Role-Based Access Control:** 
  * **Student:** Browse vacancies, analyze missing skills, and track professional growth.
  * **Company/HR:** Manage vacancy profiles, define tech stacks, and set proficiency levels.
* **Containerized Infrastructure:** Fully orchestrated environment using Docker for seamless deployment.

## Tech Stack

* **Backend:** Java 17, Spring Boot 3.x
* **Security:** Spring Security
* **API Integration:** GitHub REST API (for skill validation)
* **Database:** PostgreSQL
* **Infrastructure:** Docker, Docker Compose
* **ORM & Migrations:** Spring Data JPA, Hibernate, Liquibase
* **View Layer:** FreeMarker (FTL)
* **Logging:** SLF4J / Logback
* **Build Tool:** Gradle

## Architectural Highlights

* **External API Integration:** Uses the GitHub API to fetch repository data and languages, providing a data-driven approach to skill confirmation.
* **Dockerized Environment:** Uses `docker-compose` to manage the multi-container setup (Application + PostgreSQL), ensuring environment consistency.
* **Layered Architecture:** Strict separation of concerns (Controllers -> Services -> Repositories -> DTOs).
* **Service-Orchestrator Pattern:** Complex logic, such as Skill Gap calculation and Roadmap generation, is handled by specialized orchestrators.

## Getting Started

### Prerequisites
* Docker & Docker Compose
* GitHub Personal Access Token (for skill verification feature)

### Installation & Launch

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/Arinkmm/SkillRoute.git
    cd SkillRoute
    ```

2.  **Environment Setup:**
    Create a `.env` file or update `docker-compose.yml` with your database credentials and GitHub API token.

3.  **Run with Docker Compose:**
    ```bash
    docker-compose up --build
    ```
    The application will be available at `http://localhost:8080` and the database will be initialized automatically.

## Development Roadmap (To-Do)

- [x] Database Schema Design & Liquibase Setup
- [x] Docker & Docker Compose orchestration
- [x] Skill Gap Matching Algorithm
- [x] Learning Resource Integration
- [ ] UI Shell & Core Layout (Base templates and navigation)
- [ ] Responsive Web Design (Templates styling and adaptive UI)
- [ ] GitHub API Integration for skill validation

## Support & Contact

Got questions? Need help with setup? Found a bug?
Email: mairabeeva42@gmail.com
