package br.com.gomining.schoolsimulator.service.impl;

import br.com.gomining.schoolsimulator.common.Exception.ApiNotFoundException;
import br.com.gomining.schoolsimulator.model.entity.Grade;
import br.com.gomining.schoolsimulator.repository.GradeRepository;
import br.com.gomining.schoolsimulator.service.GradeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class GradeServiceImpl implements GradeService {
    private GradeRepository gradeRepository;

    @Override
    public List<Grade> getAllGrades() {
        return this.gradeRepository.findAll();
    }

    @Override
    public Grade getGradeById(String id) {
        Optional<Grade> grade = this.gradeRepository.findById(id);
        return grade.orElseThrow(() -> new ApiNotFoundException("Nota não encontrada com o ID: " + id));
    }

    @Override
    public Grade createGrade(Grade grade) {
        return this.gradeRepository.save(grade);
    }

    @Override
    public Grade updateGrade(String id, Grade updatedGrade) {
        Grade existingGrade = gradeRepository.findById(id)
                .orElseThrow(() -> new ApiNotFoundException("Nota não encontrada com o ID: " + id));

        existingGrade.setStudent(updatedGrade.getStudent());
        existingGrade.setActivity(updatedGrade.getActivity());
        existingGrade.setGradeValue(updatedGrade.getGradeValue());

        return gradeRepository.save(existingGrade);
    }

    @Override
    public void deleteGrade(String id) {
        Grade existingGrade = gradeRepository.findById(id)
                .orElseThrow(() -> new ApiNotFoundException("Nota não encontrada com o ID: " + id));

        gradeRepository.delete(existingGrade);
    }

    @Override
    public double getAverageGradeForStudentInActivity(String studentId, String activityId) {
        List<Grade> grades = gradeRepository.getAllGradesForStudentInActivity(studentId, activityId);
        double sumGrades = grades.stream().mapToDouble(Grade::getGradeValue).sum();
        return grades.isEmpty() ? 0 : sumGrades / grades.size();
    }

    @Override
    public double getAverageGradeForStudent(String studentId) {
        List<Grade> grades = gradeRepository.getAllGradesForStudent(studentId);
        double sumGrades = grades.stream().mapToDouble(Grade::getGradeValue).sum();
        return grades.isEmpty() ? 0 : sumGrades / grades.size();
    }

    @Override
    public double getAverageGradeForActivity(String activityId) {
        List<Grade> grades = gradeRepository.getAllGradesForActivity(activityId);
        double sumGrades = grades.stream().mapToDouble(Grade::getGradeValue).sum();
        return grades.isEmpty() ? 0 : sumGrades / grades.size();
    }

    @Override
    public List<Grade> getAllGradesForStudent(String studentId) {
        return gradeRepository.getAllGradesForStudent(studentId);
    }
}
