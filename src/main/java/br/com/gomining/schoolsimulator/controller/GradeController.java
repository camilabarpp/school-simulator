package br.com.gomining.schoolsimulator.controller;

import br.com.gomining.schoolsimulator.model.mapper.GradeMapper;
import br.com.gomining.schoolsimulator.model.request.GradeRequest;
import br.com.gomining.schoolsimulator.model.response.GradeResponse;
import br.com.gomining.schoolsimulator.service.impl.GradeServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static br.com.gomining.schoolsimulator.model.mapper.GradeMapper.toEntity;
import static br.com.gomining.schoolsimulator.model.mapper.GradeMapper.toResponse;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("/grades")
@AllArgsConstructor
@Tag(name = "Grade", description = "Grade Controller")
public class GradeController {
    private final GradeServiceImpl gradeService;

    @GetMapping
    @Operation(summary = "Get all grades")
    public List<GradeResponse> getAllGrades() {
        return GradeMapper.toListGradeResponse(gradeService.getAllGrades());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get grade by id")
    public GradeResponse getGradeById(@PathVariable String id) {
        return toResponse(gradeService.getGradeById(id));
    }

    @PostMapping
    @ResponseStatus(CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Create grade")
    public GradeResponse createGrade(@RequestBody @Valid GradeRequest gradeRequest) {
        return toResponse(gradeService.createGrade(toEntity(gradeRequest)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Update grade")
    public GradeResponse updateGrade(@PathVariable String id, @RequestBody @Valid GradeRequest grade) {
        return toResponse(gradeService.updateGrade(id, toEntity(grade)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Delete grade by id")
    public void deleteGrade(@PathVariable String id) {
        gradeService.deleteGrade(id);
    }

    @DeleteMapping
    @ResponseStatus(NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Delete all grades")
    public void deleteAllGrades() {
        gradeService.deleteAllGrades();
    }
}
