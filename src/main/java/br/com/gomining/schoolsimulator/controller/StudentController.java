package br.com.gomining.schoolsimulator.controller;

import br.com.gomining.schoolsimulator.model.mapper.GradeMapper;
import br.com.gomining.schoolsimulator.model.request.StudentRequest;
import br.com.gomining.schoolsimulator.model.response.GradeResponse;
import br.com.gomining.schoolsimulator.model.response.StudentResponse;
import br.com.gomining.schoolsimulator.service.impl.StudentServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Student", description = "Student Controller")
public class StudentController {
    private final StudentServiceImpl studentService;

    @GetMapping
    @Operation(summary = "Returns a list of students")
    public List<StudentResponse> getAllStudents() {
        return toListResponse(studentService.getAllStudents());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Returns a single student")
    public StudentResponse getStudentById(@PathVariable String id) {
        return toResponse(studentService.getStudentById(id));
    }

    @PostMapping
    @ResponseStatus(CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Creates a new student")
    public StudentResponse createStudent(@RequestBody @Valid StudentRequest student) {
        return toResponse(studentService.createStudent(toEntity(student)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Updates an existing student")
    public StudentResponse updateStudent(@PathVariable String id, @RequestBody @Valid StudentRequest student) {
        return toResponse(studentService.updateStudent(id, toEntity(student)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deletes an existing student")
    public void deleteStudent(@PathVariable String id) {
        studentService.deleteStudent(id);
    }

    @DeleteMapping
    @ResponseStatus(NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deletes all students")
    public void deleteAllStudents() {
        studentService.deleteAllStudents();
    }

    @PutMapping("/{studentId}/activity/{activityId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Adds an activity to a student")
    public StudentResponse addActivity(@PathVariable String studentId, @PathVariable String activityId) {
        return toResponse(studentService.addActivity(studentId, activityId));
    }

    @PutMapping("/{studentId}/activity/{activityId}/grade")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Adds a grade to a student")
    public StudentResponse addGrade(@PathVariable String studentId, @PathVariable String activityId, @RequestBody @Valid GradeResponse gradeResponse) {
        return toResponse(studentService.addGrade(studentId, activityId, responseToEntity(gradeResponse)));
    }

    @GetMapping("/{studentId}/activity/{activityId}/average")
    @Operation(summary = "Calculates the average of a student in an activity")
    public Double calculateStudentAverageInActivity(@PathVariable String studentId, @PathVariable String activityId) {
        return studentService.calculateStudentAverageInActivity(studentId, activityId);
    }

    @GetMapping("/{studentId}/average")
    @Operation(summary = "Calculates the average of a student in all activities")
    public Double calculateStudentAverageBasedOnAllActivity(@PathVariable String studentId) {
        return studentService.calculateStudentAverageBasedOnAllActivity(studentId);
    }

    @GetMapping("/activity/{activityId}/average")
    @Operation(summary = "Calculates the average of all students in an activity")
    public Double calculateOverallAverageForActivity(@PathVariable String activityId) {
        return studentService.calculateOverallAverageForActivity(activityId);
    }

    @GetMapping("/search")
    @Operation(summary = "Searches for a student by CPF, email or phone")
    public StudentResponse getStudentByCpfEmailOrPhone(@RequestParam String identifier) {
        return toResponse(studentService.getStudentByCpfEmailOrPhone(identifier));
    }

    @GetMapping("/search/grades")
    @Operation(summary = "Searches for a student's grades by CPF, email or phone")
    public List<GradeResponse> getStudentGradesByCpfEmailOrPhone(@RequestParam String identifier) {
        return GradeMapper.toListGradeResponse(studentService.getStudentGradesByCpfEmailOrPhone(identifier));
    }
}
