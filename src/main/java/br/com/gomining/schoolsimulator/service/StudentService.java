package br.com.gomining.schoolsimulator.service;

import br.com.gomining.schoolsimulator.model.entity.grade.Grade;
import br.com.gomining.schoolsimulator.model.entity.student.Student;

import java.util.List;

public interface StudentService {
    List<Student> getAllStudents();
    Student getStudentById(String id);
    Student createStudent(Student student);
    Student updateStudent(String id, Student student);
    void deleteStudent(String id);
    void deleteAllStudents();

    Student addActivity(String studentId, String activityId);
    Student addGrade(String studentId, String activityId, Grade grade);
    Double calculateStudentAverageBasedOnAllActivity(String studentId);
    Double calculateStudentAverageInActivity(String studentId, String activityId);
    Double calculateOverallAverageForActivity(String activityId);
    Student getStudentByCpfEmailOrPhone(String identifier);
    List<Grade> getStudentGradesByCpfEmailOrPhone(String identifier);
}
