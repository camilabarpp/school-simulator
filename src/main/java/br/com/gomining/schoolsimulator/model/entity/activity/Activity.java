package br.com.gomining.schoolsimulator.model.entity.activity;

import br.com.gomining.schoolsimulator.model.entity.grade.Grade;
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
@Document(collection = "Activity")
//@ApiModel
public class Activity {
    @Id
    @Schema(description = "The database generated activity ID")
    private String id;
    @Schema(description = "The activity title")
    private String title;
    @Schema(description = "The activity question statement")
    private String questionStatement;
    @Schema(description = "The activity registration date")
    private String registrationDate;
    @Schema(description = "The activity last update date")
    private String lastUpdateDate;
    @Schema(description = "The activity grade")
    private List<Grade> grade;

    public Activity(String id, String title, String questionStatement, List<Grade> listOfGrades) {
        this.id = id;
        this.title = title;
        this.questionStatement = questionStatement;
        this.grade = listOfGrades;
    }
}
