package br.com.gomining.schoolsimulator.model.request;

import br.com.gomining.schoolsimulator.model.entity.grade.Grade;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

@NoArgsConstructor
@Getter
public class ActivityRequest {
    @NotBlank(message = "title is mandatory")
    private String title;
    @NotBlank(message = "question statement is mandatory")
    private String questionStatement;
    private String registrationDate;
    private String lastUpdateDate;
    private List<Grade> grade;
}
