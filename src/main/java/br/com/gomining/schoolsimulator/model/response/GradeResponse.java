package br.com.gomining.schoolsimulator.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GradeResponse {
    private String id;
    private Double gradeValue;
}
