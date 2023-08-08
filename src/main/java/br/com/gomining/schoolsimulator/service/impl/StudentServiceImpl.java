package br.com.gomining.schoolsimulator.service.impl;

import br.com.gomining.schoolsimulator.common.Exception.ApiNotFoundException;
import br.com.gomining.schoolsimulator.model.entity.Grade;
import br.com.gomining.schoolsimulator.model.entity.Student;
import br.com.gomining.schoolsimulator.repository.GradeRepository;
import br.com.gomining.schoolsimulator.repository.StudentRepository;
import br.com.gomining.schoolsimulator.service.StudentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class StudentServiceImpl implements StudentService {

    private StudentRepository studentRepository;

    private GradeRepository gradeRepository;


    @Override
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Override
    public Student getStudentById(String id) {
        Optional<Student> optionalStudent = studentRepository.findById(id);
        return optionalStudent.orElseThrow(
                () -> new ApiNotFoundException("Estudante não encontrado com o ID: " + id));
    }

    @Override
    public Student createStudent(Student student) {
        return studentRepository.save(student);
    }

    @Override
    public Student updateStudent(String id, Student student) {
        Optional<Student> optionalExistingStudent = studentRepository.findById(id);
        if (optionalExistingStudent.isPresent()) {
            Student existingStudent = optionalExistingStudent.get();
            existingStudent.setFullName(student.getFullName());
            existingStudent.setTelephone(student.getTelephone());
            existingStudent.setEmail(student.getEmail());
            return studentRepository.save(existingStudent);
        } else {
            throw new ApiNotFoundException("Estudante não encontrado com o ID: " + id);
        }
    }

    @Override
    public void deleteStudent(String id) {
        Optional<Student> optionalStudent = studentRepository.findById(id);
        if (optionalStudent.isPresent()) {
            Student student = optionalStudent.get();
            studentRepository.save(student);
        } else {
            throw new ApiNotFoundException("Estudante não encontrado com o ID: " + id);
        }
    }
}
