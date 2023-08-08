package br.com.gomining.schoolsimulator.model.response;

import br.com.gomining.schoolsimulator.model.entity.Activity;
import br.com.gomining.schoolsimulator.model.entity.Student;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GradeResponse {
    private String id;
    private Student student;
    private Activity activity;
    private Double gradeValue;
}
