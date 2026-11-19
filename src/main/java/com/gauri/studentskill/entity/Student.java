package com.gauri.studentskill.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @NotNull(message = "Student ID is required")
    @Positive(message = "Student ID must be positive")
    @Column(name = "student_id", nullable = false, unique = true)
    private Long studentId;

    @NotBlank(message = "Student name is required")
    @Column(name = "student_name", nullable = false)
    private String studentName;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email address")
    @Column(nullable = false)
    private String email;

    @NotBlank(message = "Course name is required")
    @Column(name = "course_name", nullable = false)
    private String courseName;

    @NotNull(message = "Programming score is required")
    @DecimalMin(value = "0.0", message = "Programming score cannot be below 0")
    @DecimalMax(value = "100.0", message = "Programming score cannot exceed 100")
    @Column(name = "programming_score", nullable = false)
    private Double programmingScore;

    @NotNull(message = "Mathematics score is required")
    @DecimalMin(value = "0.0", message = "Mathematics score cannot be below 0")
    @DecimalMax(value = "100.0", message = "Mathematics score cannot exceed 100")
    @Column(name = "mathematics_score", nullable = false)
    private Double mathematicsScore;

    // A student's skills are stored in a separate table linked by student_id.
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "student_technical_skills",
            joinColumns = @JoinColumn(name = "student_id")
    )
    @OrderColumn(name = "skill_order")
    @Column(name = "skill")
    private List<String> technicalSkills = new ArrayList<>();

    // JPA requires a no-argument constructor.
    public Student() {
    }

    public Student(Long studentId, String studentName, String email,
                   String courseName, Double programmingScore,
                   Double mathematicsScore, List<String> technicalSkills) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.email = email;
        this.courseName = courseName;
        this.programmingScore = programmingScore;
        this.mathematicsScore = mathematicsScore;
        this.technicalSkills = technicalSkills == null
                ? new ArrayList<>() : new ArrayList<>(technicalSkills);
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Double getProgrammingScore() {
        return programmingScore;
    }

    public void setProgrammingScore(Double programmingScore) {
        this.programmingScore = programmingScore;
    }

    public Double getMathematicsScore() {
        return mathematicsScore;
    }

    public void setMathematicsScore(Double mathematicsScore) {
        this.mathematicsScore = mathematicsScore;
    }

    public List<String> getTechnicalSkills() {
        return technicalSkills;
    }

    public void setTechnicalSkills(List<String> technicalSkills) {
        this.technicalSkills = technicalSkills == null
                ? new ArrayList<>() : new ArrayList<>(technicalSkills);
    }

    // Business method: calculate the average of the two subject scores.
    public double getAverageScore() {
        return (programmingScore + mathematicsScore) / 2.0;
    }

    // Business method: assign a category using the question-paper ranges.
    public String getPerformanceCategory() {
        double average = getAverageScore();

        if (average >= 85) {
            return "Excellent";
        } else if (average >= 70) {
            return "Good";
        } else if (average >= 50) {
            return "Satisfactory";
        } else {
            return "Needs Improvement";
        }
    }

    // Business method: identify the subject with the higher score.
    public String getStrongerSubject() {
        if (programmingScore > mathematicsScore) {
            return "Programming";
        } else if (mathematicsScore > programmingScore) {
            return "Mathematics";
        } else {
            return "Both equally strong";
        }
    }

    @Override
    public String toString() {
        return "Student{" +
                "studentId=" + studentId +
                ", studentName='" + studentName + '\'' +
                ", email='" + email + '\'' +
                ", courseName='" + courseName + '\'' +
                ", programmingScore=" + programmingScore +
                ", mathematicsScore=" + mathematicsScore +
                ", technicalSkills=" + technicalSkills +
                '}';
    }
}
