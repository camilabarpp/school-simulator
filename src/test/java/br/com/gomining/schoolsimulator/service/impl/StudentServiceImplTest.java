package br.com.gomining.schoolsimulator.service.impl;

import br.com.gomining.schoolsimulator.model.entity.student.Student;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StudentServiceImplTest {

    @Test
    @DisplayName("Test getting all students")
    void getAllStudents() {

    }

    @Test
    void getStudentById() {
    }

    @Test
    void createStudent() {
    }

    @Test
    void updateStudent() {
    }

    @Test
    void deleteStudent() {
    }

    @Test
    void deleteAllStudents() {
    }

    @Test
    void addActivity() {
    }

    @Test
    void addGrade() {
    }

    @Test
    void calculateStudentAverageBasedOnAllActivity() {
    }

    @Test
    void calculateStudentAverageInActivity() {
    }

    @Test
    void calculateOverallAverageForActivity() {
    }

    @Test
    void getStudentByCpfEmailOrPhone() {
    }

    @Test
    void getStudentGradesByCpfEmailOrPhone() {
    }

    List<Student> listOfStudents = List.of(
            new Student("1", "João", "12345678901", "joao@mail.com", "123456789"),
            new Student("2", "Maria", "12345678902", "maria@mail.com", "123456780")
    );
}