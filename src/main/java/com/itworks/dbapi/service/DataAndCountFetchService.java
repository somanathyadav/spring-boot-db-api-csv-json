package com.itworks.dbapi.service;

import com.itworks.dbapi.mapper.GenericMapper;
import com.itworks.dbapi.pojo.SelectQueryMetaData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The type Data and count fetch service.
 */
@Service
@Slf4j
public class DataAndCountFetchService {
    private final GenericMapper genericMapper;

    /**
     * Instantiates a new Data and count fetch service.
     *
     * @param genericMapper the generic mapper
     */
    public DataAndCountFetchService(GenericMapper genericMapper) {
        this.genericMapper = genericMapper;
    }


    /**
     * Gets data for table with simple filter.
     *
     * @param tableName  the table name
     * @param parameters the parameters
     * @return the data for table with simple filter
     */
    public List<LinkedHashMap> getDataForTableWithSimpleFilter(String tableName, Map<String, String> parameters) {
        log.info("getDataForTableWithSimpleFilter tableName= {} and parameters:{}", tableName, parameters);
        return genericMapper.getAllTableDataWithSimpleFilter(tableName, parameters);
    }


    /**
     * Gets data for sql id.
     *
     * @param sqlId      the sql id
     * @param parameters the parameters
     * @return the data for sql id
     */
    public List<LinkedHashMap> getDataForSQLId(String sqlId, Map<String, String> parameters) {
        log.info("getDataForSQLId sqlId= {} and parameters:{}", sqlId, parameters);
        String predefinedSQL = genericMapper.getSQLForSQLId(sqlId);
        log.info("Stored SQL for sqlID:{} is: {}", sqlId, predefinedSQL);

        if (null == predefinedSQL || predefinedSQL.isEmpty()) {
            throw new IllegalArgumentException("Invalid SQL_ID. It is not defined in the configuration table");
        }

        Map<String, String> parametersUpdated = new LinkedHashMap<>();
        parametersUpdated.put("sql", predefinedSQL);
        if (null != parameters && !parameters.isEmpty()) {
            for (Map.Entry entry : parameters.entrySet()) {
                if (null == parametersUpdated.get(entry.getKey())) {
                    parametersUpdated.put(entry.getKey().toString(), entry.getValue().toString());
                }

            }
        }

        String parameterDefaultValues = genericMapper.getSQLParameterDefaultValuesForSQLId(sqlId);
        log.info("Stored parameterDefaultValues for sqlID:{} is: {}", sqlId, predefinedSQL);
        if (null != parameterDefaultValues) {
            // Now if parameter values are not given by user then let us assign default values from configuration
            for (String pairOfVariableValue : parameterDefaultValues.split(",")) {
                String[] array = pairOfVariableValue.split("=");
                if (array.length == 2) {
                    if (null == parametersUpdated.get(array[0])) {
                        parametersUpdated.put(array[0], array[1]);
                    }
                }

            }
        }

        return getDataForSQL(parametersUpdated);
    }


    /**
     * Gets data for sql.
     *
     * @param requestParams the request params
     * @return the data for sql
     */
    public List<LinkedHashMap> getDataForSQL(Map<String, String> requestParams) {
        log.info("getDataForSQL: selecting data for parameters: {}" + requestParams);
        String sql = requestParams.get("sql");
        if (null == sql || StringUtils.trimAllWhitespace(sql).length() == 0) {
            throw new IllegalArgumentException("Please pass the 'sql' parameter to the URL. e.g. http://localhost:8080/generic/sql-data?sql=select * from SOME_TABLE");
        }
        log.info("with SQL:" + sql);
        long count = genericMapper.selectCountWithGivenSQL(requestParams);
        log.info("count= " + count);
        return genericMapper.selectWithGivenSQL(requestParams);
    }

    /**
     * Gets data for sql builder.
     *
     * @param metaData the meta data
     * @return the data for sql builder
     */
    public List<LinkedHashMap> getDataForSQLBuilder(SelectQueryMetaData metaData) {
        log.info("selecting data for sql-builder:" + metaData.getTableName());
        if (null == metaData.getTableName()) {
            throw new IllegalArgumentException("tableName is required here.");
        }
        if (metaData.getTotalCount() <= 0) {
            log.info("Getting counts from DB");
            long rowCount = genericMapper.selectCountFromPostWithSelectedColumns(metaData);
            log.info("Total rows to be returned:{}", rowCount);
        }
        return genericMapper.selectDataFromPostWithSelectedColumns(metaData);
    }

}
