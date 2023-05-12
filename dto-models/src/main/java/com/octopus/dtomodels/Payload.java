package com.octopus.dtomodels;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Payload implements Serializable {
    private Filter filterConditions;
    private List<SortRequest> sort;
    private Integer page;
    private Integer size;

//    public List<Filter> getFilters() {
//        if (Objects.isNull(this.filterCondition)) return new ArrayList<>();
//        return this.filterCondition;
//    }

    public List<SortRequest> getSort() {
        if (Objects.isNull(this.sort)) return new ArrayList<>();
        return this.sort;
    }
}
