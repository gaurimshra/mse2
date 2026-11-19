# Student Profile and Skill Development Management System

Spring Boot + MySQL REST API project for the OOPJ CS336B mid-semester question.

## Requirements
- JDK 17 or later
- Maven 3.6.3 or later
- MySQL Server
- VS Code (Java Extension Pack recommended)

## Setup
1. Create a MySQL database named `student_skill_db` in MySQL Workbench, or allow the JDBC URL to create it.
2. Open `src/main/resources/application.properties` and replace `YOUR_MYSQL_PASSWORD` with your local MySQL password.
3. Open this folder in VS Code.
4. In the project root terminal run:

   ```powershell
   mvn spring-boot:run
   ```

5. The API runs at `http://localhost:8080`.

## API endpoints
| Method | URL | Purpose |
|---|---|---|
| POST | `/api/students` | Add a student |
| GET | `/api/students` | Retrieve all students |
| GET | `/api/students/{studentId}` | Retrieve by ID |
| GET | `/api/students/search?studentId=101` | Search by ID |
| GET | `/api/students/search?email=gauri@example.com` | Search by email |
| GET | `/api/students/search?course=CSIT` | Search by course |
| PUT | `/api/students/{studentId}/scores-skills` | Update scores and skills |
| DELETE | `/api/students/{studentId}` | Delete a student |
| GET | `/api/students/merit-list` | Merit list, highest average first |
| GET | `/api/students/same-skills` | Groups with identical skill sets |
| GET | `/api/students/programming-needs-improvement` | Programming below 50 and maths >= 75 by default |

## Example POST body
```json
{
  "studentId": 101,
  "studentName": "Gauri Mishra",
  "email": "gauri@example.com",
  "courseName": "B.Tech CSIT",
  "programmingScore": 82,
  "mathematicsScore": 91,
  "technicalSkills": ["Java", "Python", "SQL"]
}
```

## Notes
- Student ID is the primary key and duplicate IDs return HTTP 409.
- Scores are validated from 0 through 100; invalid request data returns HTTP 400.
- The assignment does not define the numeric cutoff for "needs improvement in programming". This implementation assumes a programming score below 50.
- Same-skill grouping compares skill sets case-insensitively and ignores skill order.
