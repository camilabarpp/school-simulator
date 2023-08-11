package br.com.gomining.schoolsimulator.model.entity.student;

import br.com.gomining.schoolsimulator.model.entity.activity.Activity;
import io.swagger.v3.oas.annotations.media.Schema;
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
//@ApiModel
@Document(collection = "Student")
public class Student {
    @Id
    @Schema(description = "The database generated student ID")
    private String id;
    @Schema(description = "The student full name")
    private String fullName;
    @Schema(description = "The student CPF")
    private String cpf;
    @Schema(description = "The student email")
    private String email;
    @Schema(description = "The student telephone")
    private String telephone;
    @Schema(description = "The student registration date")
    private String registrationDate;
    @Schema(description = "The student last update date")
    private String lastUpdateDate;
    @Schema(description = "The student activities")
    private List<Activity> activities;

    public Student(String id, String fullName, String cpf, String email, String telephone) {
        this.id = id;
        this.fullName = fullName;
        this.cpf = cpf;
        this.email = email;
        this.telephone = telephone;
    }

    public Student(String id, String fullName, String cpf, String email, String telephone, List<Activity> activities) {
        this.id = id;
        this.fullName = fullName;
        this.cpf = cpf;
        this.email = email;
        this.telephone = telephone;
        this.activities = activities;
    }
}
