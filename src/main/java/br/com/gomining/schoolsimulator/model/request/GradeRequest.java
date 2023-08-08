package br.com.gomining.schoolsimulator.model.request;

import br.com.gomining.schoolsimulator.model.entity.Activity;
import br.com.gomining.schoolsimulator.model.entity.Student;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class GradeRequest {
    private Student student;
    private Activity activity;
    private Double gradeValue;
}
