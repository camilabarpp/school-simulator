package br.com.gomining.schoolsimulator.model.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@Getter
public class ActivityRequest {
    @NotBlank(message = "title is mandatory")
    private String title;
    @NotBlank(message = "question statement is mandatory")
    private String questionStatement;
    private String registrationDate;
    private String lastUpdateDate;
}
