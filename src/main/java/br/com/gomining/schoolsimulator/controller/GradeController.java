package br.com.gomining.schoolsimulator.controller;

import br.com.gomining.schoolsimulator.model.mapper.GradeMapper;
import br.com.gomining.schoolsimulator.model.request.GradeRequest;
import br.com.gomining.schoolsimulator.model.response.GradeResponse;
import br.com.gomining.schoolsimulator.service.impl.GradeServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static br.com.gomining.schoolsimulator.model.mapper.GradeMapper.toEntity;
import static br.com.gomining.schoolsimulator.model.mapper.GradeMapper.toResponse;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/grades")
@AllArgsConstructor
public class GradeController {
    private final GradeServiceImpl gradeService;

    @GetMapping
    public List<GradeResponse> getAllGrades() {
        return GradeMapper.toListGradeResponse(gradeService.getAllGrades());
    }

    @GetMapping("/{id}")
    public GradeResponse getGradeById(@PathVariable String id) {
        return toResponse(gradeService.getGradeById(id));
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public GradeResponse createGrade(@RequestBody @Valid GradeRequest gradeRequest) {
        return toResponse(gradeService.createGrade(toEntity(gradeRequest)));
    }

    @PutMapping("/{id}")
    public GradeResponse updateGrade(@PathVariable String id, @RequestBody @Valid GradeRequest grade) {
        return toResponse(gradeService.updateGrade(id, toEntity(grade)));
    }

    @DeleteMapping("/{id}")
    public void deleteGrade(@PathVariable String id) {
        gradeService.deleteGrade(id);
    }

    @DeleteMapping
    public void deleteAllGrades() {
        gradeService.deleteAllGrades();
    }
}
