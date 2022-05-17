package com.itworks.dbapi.pojo;

import lombok.Data;

@Data
public class FilterCriteria {
    private String columnName;
    private String operator = "="; //default is equals
    private String columnValue;
    private String andOr = "AND";

}
