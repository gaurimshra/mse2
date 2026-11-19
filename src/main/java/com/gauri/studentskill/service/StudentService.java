package com.gauri.studentskill.service;

import com.gauri.studentskill.dto.UpdateScoresSkillsRequest;
import com.gauri.studentskill.entity.Student;
import com.gauri.studentskill.repository.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
public class StudentService {

    // Assumption for this paper: a programming score below 50 means
    // "needs improvement in programming".
    private static final double PROGRAMMING_IMPROVEMENT_THRESHOLD = 50.0;

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Transactional
    public Student addStudent(Student student) {
        if (studentRepository.existsById(student.getStudentId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A student with ID " + student.getStudentId() + " already exists"
            );
        }

        student.setTechnicalSkills(cleanSkills(student.getTechnicalSkills()));
        return studentRepository.save(student);
    }

    @Transactional(readOnly = true)
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Student getStudentById(Long studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Student not found with ID: " + studentId
                ));
    }

    @Transactional(readOnly = true)
    public Student searchByEmail(String email) {
        return studentRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No student found with email: " + email
                ));
    }

    @Transactional(readOnly = true)
    public List<Student> searchByCourse(String course) {
        List<Student> matches = studentRepository
                .findByCourseNameContainingIgnoreCase(course);

        if (matches.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "No students found for course: " + course
            );
        }
        return matches;
    }

    @Transactional
    public Student updateScoresAndSkills(Long studentId,
                                         UpdateScoresSkillsRequest request) {
        Student existingStudent = getStudentById(studentId);
        existingStudent.setProgrammingScore(request.getProgrammingScore());
        existingStudent.setMathematicsScore(request.getMathematicsScore());
        existingStudent.setTechnicalSkills(cleanSkills(request.getTechnicalSkills()));
        return studentRepository.save(existingStudent);
    }

    @Transactional
    public void deleteStudent(Long studentId) {
        Student existingStudent = getStudentById(studentId);
        studentRepository.delete(existingStudent);
    }

    // Uses a List and Comparator to sort in descending order by average score.
    @Transactional(readOnly = true)
    public List<Student> getMeritList() {
        List<Student> meritList = new ArrayList<>(studentRepository.findAll());
        meritList.sort(
                Comparator.comparingDouble(Student::getAverageScore).reversed()
        );
        return meritList;
    }

    // Groups students with exactly the same skill set. Set comparison ignores order.
    // The Map key is the normalized skill set; the value is the students in that group.
    @Transactional(readOnly = true)
    public List<List<Student>> findStudentsWithSameSkills() {
        Map<Set<String>, List<Student>> groups = new HashMap<>();

        for (Student student : studentRepository.findAll()) {
            Set<String> normalizedSkills = new TreeSet<>();

            if (student.getTechnicalSkills() != null) {
                normalizedSkills = student.getTechnicalSkills().stream()
                        .filter(Objects::nonNull)
                        .map(String::trim)
                        .filter(skill -> !skill.isEmpty())
                        .map(String::toLowerCase)
                        .collect(Collectors.toCollection(TreeSet::new));
            }

            // Do not group students whose skill list is empty.
            if (!normalizedSkills.isEmpty()) {
                groups.computeIfAbsent(normalizedSkills, key -> new ArrayList<>())
                        .add(student);
            }
        }

        return groups.values().stream()
                .filter(studentGroup -> studentGroup.size() > 1)
                .collect(Collectors.toList());
    }

    // Assumption: "needs improvement in programming" means programming < 50.
    @Transactional(readOnly = true)
    public List<Student> findProgrammingImprovementStudents(Double minMathScore) {
        return studentRepository.findAll().stream()
                .filter(student -> student.getProgrammingScore()
                        < PROGRAMMING_IMPROVEMENT_THRESHOLD)
                .filter(student -> student.getMathematicsScore() >= minMathScore)
                .collect(Collectors.toList());
    }

    private List<String> cleanSkills(List<String> skills) {
        if (skills == null) {
            return new ArrayList<>();
        }

        return skills.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(skill -> !skill.isEmpty())
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
