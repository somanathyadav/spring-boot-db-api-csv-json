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
import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The type CSV rest controller.
 *
 * @author Somanath Yadav
 */
@RequestMapping("/csv")
@RestController
@Slf4j
public class CSVRestController {


    private final DataAndCountFetchService dataAndCountFetchService;

    /**
     * Instantiates a new CSV rest controller.
     *
     * @param dataAndCountFetchService the data and count fetch service
     */
    public CSVRestController(DataAndCountFetchService dataAndCountFetchService) {
        this.dataAndCountFetchService = dataAndCountFetchService;
    }

    /**
     * Gets table full data in CSV file with column headers by default.
     * Preferred <i>fileName</i> to download  can be provided in request parameters  as ?someColumn=somValue&fileName=MyReport.csv
     * Send <i>skipHeaderRow=true</i> to skip column headers row in resultant
     * You also can filter the data by providing multiple query parameters like  ?COLUMN_NAME=columnValue&COLUMN_TWO=someValue
     * <i>limit</i> and <i>offset</i> in query parameters can be used to limit the data in response and do pagination
     *
     * @param tableName  the table name
     * @param parameters the parameters
     * @return the table full data
     */
    @GetMapping("table-data/{tableName}")
    public void getTableFullData(@PathVariable String tableName, @RequestParam Map<String, String> parameters, HttpServletResponse servletResponse) throws IOException {
        List<LinkedHashMap> dataForTableWithSimpleFilter = dataAndCountFetchService.getDataForTableWithSimpleFilter(tableName, parameters);

        writeDataToCSVResponse(parameters, servletResponse, dataForTableWithSimpleFilter);
    }

    /**
     * Gets table data from sql_id predefined in database with CSV file format and column headers by default.
     * Preferred <i>fileName</i> to download  can be provided in request parameters  as ?fileName=MyReport.csv
     * Send <i>skipHeaderRow=true</i> to skip column headers row in resultant
     * <p>
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
    public void getTableDataFromSQLId(@PathVariable String sqlId, @RequestParam Map<String, String> requestParams, HttpServletResponse servletResponse) throws IOException {
        List<LinkedHashMap> dataForSQLId = dataAndCountFetchService.getDataForSQLId(sqlId, requestParams);
        writeDataToCSVResponse(requestParams, servletResponse, dataForSQLId);
    }

    /**
     * Gets table data from sql in CSV file with column headers by default.
     * Preferred <i>fileName</i> to download  can be provided in request parameters  as ?fileName=MyReport.csv
     * Send <i>skipHeaderRow=true</i> to skip column headers row in resultant
     * <p>
     * Pass below parameters as query parameters:
     * sql - the sql to be executed .. e.g. ?sql=select * from SOME_TABLE where SOME_COLUMN='some_value' and OTHER_COLUMN='some_value'
     * Also, <i>limit</i> and <i>offset</i> in query parameters can be used to limit the data in response and do pagination
     *
     * @param requestParams the request params as sql, sql text and key value pairs of parameters for place-holders in query
     * @return the table data from sql
     */
    @GetMapping("sql-data")
    public void getTableDataFromSQL(@RequestParam Map<String, String> requestParams, HttpServletResponse servletResponse) throws IOException {
        List<LinkedHashMap> dataForSQL = dataAndCountFetchService.getDataForSQL(requestParams);
        writeDataToCSVResponse(requestParams, servletResponse, dataForSQL);
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
     * "offset" : 0,
     * "fileName" : "MyReport.csv",
     * "skipHeaderRow" : "Y"
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
    public void getSelectedDataUsingCustomQueryBuilder(@RequestBody SelectQueryMetaData metaData, HttpServletResponse servletResponse) throws IOException {
        List<LinkedHashMap> dataForSQLBuilder = dataAndCountFetchService.getDataForSQLBuilder(metaData);
        Map<String, String> parameters = new HashMap<>();
        parameters.put("fileName", metaData.getFileName());
        parameters.put("skipHeaderRow", metaData.getSkipHeaderRow());
        writeDataToCSVResponse(parameters, servletResponse, dataForSQLBuilder);
    }

    private void writeDataToCSVResponse(Map<String, String> parameters, HttpServletResponse servletResponse, List<LinkedHashMap> resultData) throws IOException {
        String downloadFileName = parameters.get("fileName");
        String addHeaderRowParam = parameters.get("skipHeaderRow");
        boolean addHeaderRow = null == addHeaderRowParam || (!"Y".equalsIgnoreCase(addHeaderRowParam) && !"true".equalsIgnoreCase(addHeaderRowParam));

        if (null == downloadFileName) {
            downloadFileName = "table-data-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmSS")) + ".csv";
        } else if (!(downloadFileName.endsWith(".csv") || downloadFileName.endsWith(".CSV"))) {
            downloadFileName += ".csv";
        }
        servletResponse.setContentType("text/csv");
        servletResponse.addHeader("Content-Disposition", "attachment; filename=\"" + downloadFileName + "\"");


        try (ICsvMapWriter csvMapWriter = new CsvMapWriter(servletResponse.getWriter(), CsvPreference.STANDARD_PREFERENCE)) {
            if (null != resultData && !resultData.isEmpty()) {
                String[] headerRow = (String[]) resultData.stream().findFirst().get().keySet().stream().map(s -> s.toString()).toArray(String[]::new);
                final String[] finalHeaderRow = headerRow;
                log.info("no records retrieved:{}", resultData.size());
                if (addHeaderRow) {
                    csvMapWriter.writeHeader(headerRow);
                }

                resultData.stream().forEach(s -> {
                    try {
                        csvMapWriter.write(s, finalHeaderRow);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            } else {
                log.info("No data found for request data: {}", parameters);
                servletResponse.getWriter().println("NO_DATA_FOUND");
            }
        }
    }

}
