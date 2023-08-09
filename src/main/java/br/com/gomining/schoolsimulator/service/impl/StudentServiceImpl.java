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
import java.util.stream.Collectors;

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
            throw new RuntimeException("Atividade já adicionada ao estudante");
        }

        return student;
    }

    @Override
    public Student addGrade(String studentId, String activityId, Grade grade) {
        var student = this.getStudentById(studentId);

        Grade newGrade = gradeService.createGrade(grade);

        Optional<Activity> optionalActivity = student.getActivities().stream()
                .filter(activity -> activity.getId().equals(activityId))
                .findFirst();

        if (optionalActivity.isPresent()) {
            Activity targetedActivity = optionalActivity.get();

            List<Grade> activityGrades = targetedActivity.getGrade();
            if (activityGrades == null) {
                activityGrades = new ArrayList<>();
            }

            activityGrades.add(newGrade);

            targetedActivity.setGrade(activityGrades);

            studentRepository.save(student);

            return student;
        } else {
            throw new ApiNotFoundException("Atividade não encontrada com o ID: " + activityId);
        }
    }

    @Override
    public Double calculateStudentAverageBasedOnAllActivity(String studentId) {
        Student student = this.getStudentById(studentId);
        List<Activity> activities = student.getActivities();

        if (activities.isEmpty()) {
            throw new ApiNotFoundException("O estudante não possui atividades.");
        }

        double totalGrade = activities.stream()
                .mapToDouble(this::calculateActivityAverage)
                .sum();

        return totalGrade / activities.size();
    }

    private Double calculateActivityAverage(Activity activity) {
        List<Grade> grades = activity.getGrade();
        if (grades == null || grades.isEmpty()) {
            return 0.0;
        }

        double totalGradeValue = grades.stream()
                .mapToDouble(Grade::getGradeValue)
                .sum();

        return totalGradeValue / grades.size();
    }

    @Override
    public Double calculateStudentAverageInActivity(String studentId, String activityId) {
        Student student = this.getStudentById(studentId);
        Activity activity = activityService.getActivityById(activityId);

        List<Grade> grades = student.getActivities().stream()
                .filter(studentActivity -> studentActivity.getId().equals(activity.getId()))
                .flatMap(studentActivity -> studentActivity.getGrade().stream())
                .collect(Collectors.toList());

        if (grades.isEmpty()) {
            throw new ApiNotFoundException("A atividade não possui notas.");
        }

        List<Grade> studentGradesInActivity = grades.stream()
                .filter(grade -> studentHasGrade(student, grade))
                .collect(Collectors.toList());

        if (studentGradesInActivity.isEmpty()) {
            throw new ApiNotFoundException("O estudante não possui notas nessa atividade.");
        }

        double totalGradeValue = studentGradesInActivity.stream()
                .mapToDouble(Grade::getGradeValue)
                .sum();

        return totalGradeValue / studentGradesInActivity.size();
    }

    private boolean studentHasGrade(Student student, Grade grade) {
        return student.getActivities().stream()
                .flatMap(activity -> activity.getGrade().stream())
                .anyMatch(studentGrade -> studentGrade.getId().equals(grade.getId()));
    }

    @Override
    public Double calculateOverallAverageForActivity(String activityId) {
        List<Student> students = getAllStudents();
        if (students.isEmpty()) {
            throw new ApiNotFoundException("Não há estudantes cadastrados.");
        }

        Activity activity = activityService.getActivityById(activityId);

        double totalGrade = students.stream()
                .mapToDouble(student -> calculateStudentActivityAverage(student, activity))
                .sum();

        return totalGrade / students.size();
    }

    private Double calculateStudentActivityAverage(Student student, Activity activity) {
        List<Grade> grades = student.getActivities().stream()
                .filter(act -> act.getId().equals(activity.getId()))
                .flatMap(act -> act.getGrade().stream())
                .collect(Collectors.toList());

        if (grades.isEmpty()) {
            return 0.0;
        }

        double totalGradeValue = grades.stream()
                .mapToDouble(Grade::getGradeValue)
                .sum();

        return totalGradeValue / grades.size();
    }

    @Override
    public Student getStudentByCpfEmailOrPhone(String identifier) {
        return Optional.ofNullable(studentRepository.findByCpfOrEmailOrTelephone(identifier, identifier, identifier))
                .orElseThrow(() -> new ApiNotFoundException("Estudante não encontrado com o CPF, email ou telefone: " + identifier));
    }

    @Override
    public List<Grade> getStudentGradesByCpfEmailOrPhone(String identifier) {
        Student student = getStudentByCpfEmailOrPhone(identifier);

        return student.getActivities().stream()
                .flatMap(activity -> activity.getGrade().stream())
                .collect(Collectors.toList());
    }

}
