package com.gauri.studentskill.controller;

import com.gauri.studentskill.dto.UpdateScoresSkillsRequest;
import com.gauri.studentskill.entity.Student;
import com.gauri.studentskill.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // 1. Add a student
    @PostMapping
    public ResponseEntity<Student> addStudent(@Valid @RequestBody Student student) {
        Student savedStudent = studentService.addStudent(student);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedStudent);
    }

    // 2. Retrieve all students
    @GetMapping
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    // 3. Search a student by primary ID
    @GetMapping("/{studentId}")
    public Student getStudentById(@PathVariable Long studentId) {
        return studentService.getStudentById(studentId);
    }

    // Q2 search: provide exactly one of studentId, email, or course.
    @GetMapping("/search")
    public List<Student> searchStudents(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String course) {

        int numberOfFilters = (studentId == null ? 0 : 1)
                + (email == null || email.isBlank() ? 0 : 1)
                + (course == null || course.isBlank() ? 0 : 1);

        if (numberOfFilters != 1) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Provide exactly one search parameter: studentId, email, or course"
            );
        }

        if (studentId != null) {
            return List.of(studentService.getStudentById(studentId));
        }
        if (email != null && !email.isBlank()) {
            return List.of(studentService.searchByEmail(email));
        }
        return studentService.searchByCourse(course);
    }

    // 4. Update only the scores and technical skills
    @PutMapping("/{studentId}/scores-skills")
    public Student updateScoresAndSkills(
            @PathVariable Long studentId,
            @Valid @RequestBody UpdateScoresSkillsRequest request) {
        return studentService.updateScoresAndSkills(studentId, request);
    }

    // 5. Delete a student
    @DeleteMapping("/{studentId}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long studentId) {
        studentService.deleteStudent(studentId);
        return ResponseEntity.noContent().build();
    }

    // 6. Display merit list in descending average-score order
    @GetMapping("/merit-list")
    public List<Student> getMeritList() {
        return studentService.getMeritList();
    }

    // Q2 extra requirement: students with exactly the same technical skill set
    @GetMapping("/same-skills")
    public List<List<Student>> getStudentsWithSameSkills() {
        return studentService.findStudentsWithSameSkills();
    }

    // Q2 extra requirement: programming score below 50 and mathematics >= threshold
    @GetMapping("/programming-needs-improvement")
    public List<Student> getProgrammingImprovementStudents(
            @RequestParam(defaultValue = "75.0") Double minMathScore) {
        if (minMathScore < 0 || minMathScore > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "minMathScore must be between 0 and 100"
            );
        }
        return studentService.findProgrammingImprovementStudents(minMathScore);
    }
}
