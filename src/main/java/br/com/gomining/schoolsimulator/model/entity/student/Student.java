package br.com.gomining.schoolsimulator.model.entity.student;

import br.com.gomining.schoolsimulator.model.entity.activity.Activity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
@Document(collection = "Student")
public class Student {
    @Id
    @ApiModelProperty(notes = "The database generated student ID")
    private String id;
    @ApiModelProperty(notes = "The student full name")
    private String fullName;
    @ApiModelProperty(notes = "The student CPF")
    private String cpf;
    @ApiModelProperty(notes = "The student email")
    private String email;
    @ApiModelProperty(notes = "The student telephone")
    private String telephone;
    @ApiModelProperty(notes = "The student registration date")
    private String registrationDate;
    @ApiModelProperty(notes = "The student last update date")
    private String lastUpdateDate;
    @ApiModelProperty(notes = "The student activities")
    private List<Activity> activities;
}
