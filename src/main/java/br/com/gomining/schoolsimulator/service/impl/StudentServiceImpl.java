package br.com.gomining.schoolsimulator.service.impl;

import br.com.gomining.schoolsimulator.common.Exception.ApiNotFoundException;
import br.com.gomining.schoolsimulator.model.entity.Student;
import br.com.gomining.schoolsimulator.repository.StudentRepository;
import br.com.gomining.schoolsimulator.service.StudentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class StudentServiceImpl implements StudentService {

    private StudentRepository studentRepository;

    @Override
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Override
    public Student getStudentById(String id) {
        return studentRepository.findById(id).orElseThrow(
                () -> new ApiNotFoundException("Estudante não encontrado com o ID: " + id));
    }

    @Override
    public Student createStudent(Student student) {
        student.setRegistrationDate(LocalDate.now().toString());
        return studentRepository.save(student);
    }

    @Override
    public Student updateStudent(String id, Student student) {
        return studentRepository.findById(id).map(studentFound -> {
            studentFound.setFullName(student.getFullName());
            studentFound.setCpf(student.getCpf());
            studentFound.setEmail(student.getEmail());
            studentFound.setTelephone(student.getTelephone());
            studentFound.setLastUpdateDate(LocalDate.now().toString());
            return studentRepository.save(studentFound);
        }).orElseThrow(() -> new ApiNotFoundException("Estudante não encontrado com o ID: " + id));
    }

    @Override
    public void deleteStudent(String id) {
        Student student = studentRepository.findById(id).orElseThrow(
                () -> new ApiNotFoundException("Estudante não encontrado com o ID: " + id));
        studentRepository.delete(student);
    }

    @Override
    public void deleteAllStudents() {
        studentRepository.deleteAll();
    }
}
