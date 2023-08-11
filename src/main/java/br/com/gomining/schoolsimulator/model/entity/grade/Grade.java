package br.com.gomining.schoolsimulator.model.entity.grade;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
@Document(collection = "Grade")
public class Grade {
    @Id
    @ApiModelProperty(notes = "The database generated grade ID")
    private String id;
    @ApiModelProperty(notes = "The grade value")
    private Double gradeValue;
}
