package br.com.gomining.schoolsimulator.model.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nonapi.io.github.classgraph.json.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "Activity")
@ApiModel
public class Activity {
    @Id
    @ApiModelProperty(notes = "The database generated activity ID")
    private String id;
    @ApiModelProperty(notes = "The activity title")
    private String title;
    @ApiModelProperty(notes = "The activity question statement")
    private String questionStatement;
    @ApiModelProperty(notes = "The activity registration date")
    private String registrationDate;
    @ApiModelProperty(notes = "The activity last update date")
    private String lastUpdateDate;
}
