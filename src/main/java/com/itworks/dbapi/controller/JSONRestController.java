/*
 *    Copyright 2015-2021 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.itworks.dbapi.controller;

import com.itworks.dbapi.pojo.SelectQueryMetaData;
import com.itworks.dbapi.service.DataAndCountFetchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The type JSON rest controller.
 *
 * @author Somanath Yadav
 */
@RequestMapping("/json")
@RestController
@Slf4j
public class JSONRestController {


    private final DataAndCountFetchService dataAndCountFetchService;

    /**
     * Instantiates a new JSON rest controller.
     *
     * @param dataAndCountFetchService the data and count fetch service
     */
    public JSONRestController(DataAndCountFetchService dataAndCountFetchService) {
        this.dataAndCountFetchService = dataAndCountFetchService;
    }

    /**
     * Gets table full data.
     * You also can filter the data by providing multiple query parameters like  ?COLUMN_NAME=columnValue&COLUMN_TWO=someValue
     * <i>limit</i> and <i>offset</i> in query parameters can be used to limit the data in response and do pagination
     *
     * @param tableName  the table name
     * @param parameters the parameters
     * @return the table full data
     */
    @GetMapping("table-data/{tableName}")
    public List getTableFullData(@PathVariable String tableName, @RequestParam Map<String, String> parameters) {
        return dataAndCountFetchService.getDataForTableWithSimpleFilter(tableName, parameters);
    }

    /**
     * Gets table data from sql_id predefined in database.
     * Pass below parameters as query parameters:
     * query predicate placeholder along with the value
     * e.g. ?div=B&state=CA
     * Also, <i>limit</i> and <i>offset</i> in query parameters can be used to limit the data in response and do pagination
     * If query predicate for placeholder is not provided by user, default value will be assigned if given in PARAM_DEFAULT_VALUES column
     *
     * @param sqlId         the sql id
     * @param requestParams the request params as sql, sql text and key value pairs of parameters for place-holders in query
     * @return the table data from sql
     */
    @GetMapping("sql-id-data/{sqlId}")
    public List getTableDataFromSQLId(@PathVariable String sqlId, @RequestParam Map<String, String> requestParams) {
        return dataAndCountFetchService.getDataForSQLId(sqlId, requestParams);
    }

    /**
     * Gets table data from sql.
     * Pass below parameters as query parameters:
     * sql - the sql to be executed .. e.g. ?sql=select * from SOME_TABLE where SOME_COLUMN='some_value' and OTHER_COLUMN='some_value'
     * Also, <i>limit</i> and <i>offset</i> in query parameters can be used to limit the data in response and do pagination
     *
     * @param requestParams the request params as sql, sql text and key value pairs of parameters for place-holders in query
     * @return the table data from sql
     */
    @GetMapping("sql-data")
    public List getTableDataFromSQL(@RequestParam Map<String, String> requestParams) {
        return dataAndCountFetchService.getDataForSQL(requestParams);
    }


    /**
     * Gets selected data using custom query builder.
     * You can use below JSON
     * <p>
     * {
     * "tableName" : "student",
     * "columnsToSelect" : ["STUDENT_NAME", "STANDARD" , "DIVISION"],
     * "filterCriteria" : [ {
     * "columnName" : "state",
     * "operator" : "like",
     * "columnValue" : "%C%"
     * }, {
     * "columnName" : "division",
     * "operator" : "=",
     * "columnValue" : "B"
     * } ],
     * "orderBy" : ["STUDENT_NAME"],
     * "limit" : 0,
     * "offset" : 0
     * }
     * </p>
     * If columnsToSelect is null then all columns are selected.
     * You may omit filterCriteria, orderBy, limit, offset as all are optional.
     * The <i>limit</i> and <i>offset</i> values can be used to do pagination at client side.
     * It is recommended that totalCount should be set if known by client during pagination.
     *
     * @param metaData the meta data
     * @return the selected data using custom q ury builder
     */
    @PostMapping("sql-builder-data")
    public List<LinkedHashMap> getSelectedDataUsingCustomQueryBuilder(@RequestBody SelectQueryMetaData metaData) {
        return dataAndCountFetchService.getDataForSQLBuilder(metaData);
    }

}
