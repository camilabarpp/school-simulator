package br.com.gomining.schoolsimulator.model.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@Getter
public class GradeRequest {
//    @NotBlank(message = "activityId is mandatory")
    private String activityId;

//    @NotBlank(message = "studentId is mandatory")
    private String studentId;
//    @NotBlank(message = "gradeValue is mandatory")
    private Double gradeValue;
}
