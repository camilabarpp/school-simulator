package br.com.gomining.schoolsimulator.service;

import br.com.gomining.schoolsimulator.model.entity.Grade;
import br.com.gomining.schoolsimulator.model.entity.Student;

import java.util.List;

public interface StudentService {
    List<Student> getAllStudents();
    Student getStudentById(String id);
    Student createStudent(Student student);
    Student updateStudent(String id, Student student);
    void deleteStudent(String id);
//    double calcularMediaGeralAtividade(String estudanteId, String atividadeId);
//    double calcularMediaGeralEstudante(String estudanteId);
//    List<Grade> listarNotasEstudante(String estudanteId);
}
