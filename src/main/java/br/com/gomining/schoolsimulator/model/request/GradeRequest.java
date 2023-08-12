package br.com.gomining.schoolsimulator.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@NoArgsConstructor
@Getter
@Builder
@AllArgsConstructor
public class GradeRequest {
    private String studentId;
    @NotNull(message = "gradeValue is mandatory")
    private Double gradeValue;
}
