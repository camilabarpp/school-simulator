package br.com.gomining.schoolsimulator.model.mapper;

import br.com.gomining.schoolsimulator.model.entity.Grade;
import br.com.gomining.schoolsimulator.model.request.GradeRequest;
import br.com.gomining.schoolsimulator.model.response.GradeResponse;
import lombok.experimental.UtilityClass;

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
}
