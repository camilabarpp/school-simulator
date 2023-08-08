package br.com.gomining.schoolsimulator.controller;

import br.com.gomining.schoolsimulator.model.entity.Grade;
import br.com.gomining.schoolsimulator.model.mapper.GradeMapper;
import br.com.gomining.schoolsimulator.model.request.StudentRequest;
import br.com.gomining.schoolsimulator.model.response.GradeResponse;
import br.com.gomining.schoolsimulator.model.response.StudentResponse;
import br.com.gomining.schoolsimulator.service.impl.StudentServiceImpl;
import lombok.AllArgsConstructor;
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
    public StudentResponse createStudent(@RequestBody @Valid StudentRequest student) {
        return toResponse(studentService.createStudent(toEntity(student)));
    }

    @PutMapping("/{id}")
    public StudentResponse updateStudent(@PathVariable String id, @RequestBody @Valid StudentRequest student) {
        return toResponse(studentService.updateStudent(id, toEntity(student)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void deleteStudent(@PathVariable String id) {
        studentService.deleteStudent(id);
    }

    @DeleteMapping
    @ResponseStatus(NO_CONTENT)
    public void deleteAllStudents() {
        studentService.deleteAllStudents();
    }

    @PutMapping("/{studentId}/activity/{activityId}")
    public StudentResponse addActivity(@PathVariable String studentId, @PathVariable String activityId) {
        return toResponse(studentService.addActivity(studentId, activityId));
    }

    @PutMapping("/{studentId}/activity/{activityId}/grade")
    public StudentResponse addGrade(@PathVariable String studentId, @PathVariable String activityId, @RequestBody @Valid GradeResponse gradeResponse) {
        return toResponse(studentService.addGrade(studentId, activityId, responseToEntity(gradeResponse)));
    }
}
