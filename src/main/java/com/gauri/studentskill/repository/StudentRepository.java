package com.gauri.studentskill.repository;

import com.gauri.studentskill.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByEmailIgnoreCase(String email);

    List<Student> findByCourseNameContainingIgnoreCase(String courseName);
}
