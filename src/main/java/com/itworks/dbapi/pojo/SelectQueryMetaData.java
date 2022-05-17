package com.itworks.dbapi.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SelectQueryMetaData {
    private String tableName;
    private List<String> columnsToSelect;
    private List<FilterCriteria> filterCriteria;

    private List<String> orderBy;

    @JsonIgnore
    private Map<String, Object> param;

    private long limit;
    private long offset;
    private long totalCount;

    private String fileName;
    private String skipHeaderRow;


}
