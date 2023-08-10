package br.com.gomining.schoolsimulator.controller;

import br.com.gomining.schoolsimulator.model.mapper.GradeMapper;
import br.com.gomining.schoolsimulator.model.request.StudentRequest;
import br.com.gomining.schoolsimulator.model.response.GradeResponse;
import br.com.gomining.schoolsimulator.model.response.StudentResponse;
import br.com.gomining.schoolsimulator.service.impl.StudentServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static br.com.gomining.schoolsimulator.model.mapper.GradeMapper.responseToEntity;
import static br.com.gomining.schoolsimulator.model.mapper.StudentMapper.*;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("/students")
@AllArgsConstructor
public class StudentController {
    private final StudentServiceImpl studentService;

    @GetMapping
    public List<StudentResponse> getAllStudents() {
        return toListResponse(studentService.getAllStudents());
    }

    @GetMapping("/{id}")
    public StudentResponse getStudentById(@PathVariable String id) {
        return toResponse(studentService.getStudentById(id));
    }

    @PostMapping
    @ResponseStatus(CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public StudentResponse createStudent(@RequestBody @Valid StudentRequest student) {
        return toResponse(studentService.createStudent(toEntity(student)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public StudentResponse updateStudent(@PathVariable String id, @RequestBody @Valid StudentRequest student) {
        return toResponse(studentService.updateStudent(id, toEntity(student)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteStudent(@PathVariable String id) {
        studentService.deleteStudent(id);
    }

    @DeleteMapping
    @ResponseStatus(NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteAllStudents() {
        studentService.deleteAllStudents();
    }

    @PutMapping("/{studentId}/activity/{activityId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public StudentResponse addActivity(@PathVariable String studentId, @PathVariable String activityId) {
        return toResponse(studentService.addActivity(studentId, activityId));
    }

    @PutMapping("/{studentId}/activity/{activityId}/grade")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public StudentResponse addGrade(@PathVariable String studentId, @PathVariable String activityId, @RequestBody @Valid GradeResponse gradeResponse) {
        return toResponse(studentService.addGrade(studentId, activityId, responseToEntity(gradeResponse)));
    }

    @GetMapping("/{studentId}/activity/{activityId}/average")
    public Double calculateStudentAverageInActivity(@PathVariable String studentId, @PathVariable String activityId) {
        return studentService.calculateStudentAverageInActivity(studentId, activityId);
    }

    @GetMapping("/{studentId}/average")
    public Double calculateStudentAverageBasedOnAllActivity(@PathVariable String studentId) {
        return studentService.calculateStudentAverageBasedOnAllActivity(studentId);
    }

    @GetMapping("/activity/{activityId}/average")
    public Double calculateOverallAverageForActivity(@PathVariable String activityId) {
        return studentService.calculateOverallAverageForActivity(activityId);
    }

    @GetMapping("/search")
    public StudentResponse getStudentByCpfEmailOrPhone(@RequestParam String identifier) {
        return toResponse(studentService.getStudentByCpfEmailOrPhone(identifier));
    }

    @GetMapping("/search/grades")
    public List<GradeResponse> getStudentGradesByCpfEmailOrPhone(@RequestParam String identifier) {
        return GradeMapper.toListGradeResponse(studentService.getStudentGradesByCpfEmailOrPhone(identifier));
    }
}
