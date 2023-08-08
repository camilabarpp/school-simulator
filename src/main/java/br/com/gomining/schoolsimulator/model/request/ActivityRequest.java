package br.com.gomining.schoolsimulator.model.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@Getter
public class ActivityRequest {
    @NotNull
    @NotBlank(message = "title is mandatory")
    private String title;
    @NotNull
    @NotBlank(message = "description is mandatory")
    private String description;
    private String registrationDate;
    private String lastUpdateDate;
}
