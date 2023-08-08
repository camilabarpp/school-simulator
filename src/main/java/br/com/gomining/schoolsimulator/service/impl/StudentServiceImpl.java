package br.com.gomining.schoolsimulator.service.impl;

import br.com.gomining.schoolsimulator.common.Exception.ApiNotFoundException;
import br.com.gomining.schoolsimulator.model.entity.Activity;
import br.com.gomining.schoolsimulator.model.entity.Grade;
import br.com.gomining.schoolsimulator.model.entity.Student;
import br.com.gomining.schoolsimulator.repository.StudentRepository;
import br.com.gomining.schoolsimulator.service.StudentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class StudentServiceImpl implements StudentService {

    private StudentRepository studentRepository;
    private ActivityServiceImpl activityService;
    private GradeServiceImpl gradeService;

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

    @Override
    public Student addActivity(String studentId, String activityId) {
        var activity = activityService.getActivityById(activityId);
        var student = this.getStudentById(studentId);

        if (!student.getActivities().contains(activity)) {
            student.getActivities().add(activity);
            studentRepository.save(student);
        } else {
            // Aqui você pode lançar uma exceção, retornar uma mensagem de erro, etc.
            throw new RuntimeException("Atividade já adicionada ao estudante");
        }

        return student;
    }

    @Override
    public Student addGrade(String studentId, String activityId, Grade grade) {
        var student = this.getStudentById(studentId);

        Grade newGrade = gradeService.createGrade(grade);

        // Encontrar a atividade específica pelo activityId
        Optional<Activity> optionalActivity = student.getActivities().stream()
                .filter(activity -> activity.getId().equals(activityId))
                .findFirst();

        if (optionalActivity.isPresent()) {
            Activity targetedActivity = optionalActivity.get();

            // Obter a lista de notas da atividade específica
            List<Grade> activityGrades = targetedActivity.getGrade();
            if (activityGrades == null) {
                activityGrades = new ArrayList<>();
            }

            // Adicionar a nova nota à lista de notas da atividade
            activityGrades.add(newGrade);

            // Atualizar a lista de notas da atividade
            targetedActivity.setGrade(activityGrades);

            studentRepository.save(student);

            return student;
        } else {
            throw new ApiNotFoundException("Atividade não encontrada com o ID: " + activityId);
        }
    }


//    @Override
//    public Student addGrade(String studentId, String activityId, Grade grade) {
//        var student = this.getStudentById(studentId);
//
//        Grade newGrade = gradeService.createGrade(grade);
//
//        // Encontrar a atividade específica pelo activityId
//        Activity targetedActivity = student.getActivities().stream()
//                .filter(activity1 -> activity1.getId().equals(activityId))
//                .findFirst()
//                .orElseThrow(() -> new ApiNotFoundException("Atividade não encontrada com o ID: " + activityId));
//
//        // Obter a lista de notas da atividade específica
//        List<Grade> activityGrades = targetedActivity.getGrade();
//        if (activityGrades == null) {
//            activityGrades = new ArrayList<>();
//        }
//
//        // Adicionar a nova nota à lista de notas da atividade
//        activityGrades.add(newGrade);
//
//        // Atualizar a lista de notas da atividade
//        targetedActivity.setGrade(activityGrades);
//
//        studentRepository.save(student);
//
//        return student;
//    }
}
