package br.com.gomining.schoolsimulator.model.mapper;

import br.com.gomining.schoolsimulator.model.entity.Grade;
import br.com.gomining.schoolsimulator.model.request.GradeRequest;
import br.com.gomining.schoolsimulator.model.response.GradeResponse;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class GradeMapper {
    public static Grade toEntity(GradeRequest gradeRequest) {
        return Grade.builder()
                .gradeValue(gradeRequest.getGradeValue())
                .build();
    }

    public static GradeResponse toResponse(Grade grade) {
        return GradeResponse.builder()
                .id(grade.getId())
                .gradeValue(grade.getGradeValue())
                .build();
    }

    public static List<GradeResponse> toListResponse(List<Grade> grades) {
        return grades.stream()
                .map(GradeMapper::toResponse)
                .collect(Collectors.toList());
    }
}
