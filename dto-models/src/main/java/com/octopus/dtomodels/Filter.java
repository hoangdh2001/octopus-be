package com.octopus.dtomodels;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import javax.persistence.criteria.JoinType;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Filter {
    private String key;
    private FilterOperator operator;
    private FieldType fieldType;
    private JoinType joinType;

    private transient Object value;

    private transient Object valueTo;

    private transient List<Object> values;

}
