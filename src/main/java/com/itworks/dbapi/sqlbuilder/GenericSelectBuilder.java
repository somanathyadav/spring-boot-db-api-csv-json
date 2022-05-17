package com.itworks.dbapi.sqlbuilder;

import org.apache.ibatis.jdbc.SQL;
import com.itworks.dbapi.pojo.FilterCriteria;
import com.itworks.dbapi.pojo.SelectQueryMetaData;

import java.util.HashMap;
import java.util.Map;

/**
 * The type Generic select builder.
 */
public class GenericSelectBuilder {

    /**
     * Select table data with filter string.
     *
     * @param metaData the meta data
     * @return the string
     */
    public String selectTableDataWithFilter(SelectQueryMetaData metaData) {
        Map<String, Object> param = new HashMap<>();
        if (metaData.getFilterCriteria() != null) {
            for (FilterCriteria criteria : metaData.getFilterCriteria()) {
                param.put(criteria.getColumnName(), criteria.getColumnValue());
            }
        }
        metaData.setParam(param);
        return new SQL() {{
            if (metaData.getColumnsToSelect() == null || metaData.getColumnsToSelect().isEmpty()) {
                SELECT(" * ");
            } else {
                for (String columnName : metaData.getColumnsToSelect()) {
                    SELECT(columnName);
                }
            }
            FROM(metaData.getTableName());

            if (metaData.getFilterCriteria() != null) {
                for (FilterCriteria filterCriteria : metaData.getFilterCriteria()) {
                    if (filterCriteria.getAndOr().equalsIgnoreCase("OR")) {
                        OR();
                    }
                    WHERE(filterCriteria.getColumnName() + " " + filterCriteria.getOperator() + " #{param." + filterCriteria.getColumnName() + "}");
                }
            }
            if (metaData.getLimit() > 0) {
                FETCH_FIRST_ROWS_ONLY("#{limit}");
            }
            if (metaData.getOffset() > 0) {
                OFFSET_ROWS("#{offset}");
            }

            if (metaData.getOrderBy() != null) {
                for (String columnName : metaData.getOrderBy()) {
                    ORDER_BY(columnName);
                }
            }

        }}.toString();
    }

    /**
     * Select table data count only with filter string.
     *
     * @param metaData the meta data
     * @return the string
     */
    public String selectTableDataCountOnlyWithFilter(SelectQueryMetaData metaData) {
        Map<String, Object> param = new HashMap<>();
        if (metaData.getFilterCriteria() != null) {
            for (FilterCriteria criteria : metaData.getFilterCriteria()) {
                param.put(criteria.getColumnName(), criteria.getColumnValue());
            }
        }
        metaData.setParam(param);
        return new SQL() {{
            SELECT(" count(1) ");
            FROM(metaData.getTableName());

            if (metaData.getFilterCriteria() != null) {
                for (FilterCriteria filterCriteria : metaData.getFilterCriteria()) {
                    if (filterCriteria.getAndOr().equalsIgnoreCase("OR")) {
                        OR();
                    }
                    WHERE(filterCriteria.getColumnName() + " " + filterCriteria.getOperator() + " #{param." + filterCriteria.getColumnName() + "}");
                }
            }

        }}.toString();
    }


    /**
     * Select all table data with simple filter string.
     *
     * @param tableName  the table name
     * @param parameters the parameters
     * @return the string
     */
    public String selectAllTableDataWithSimpleFilter(String tableName, Map<String, String> parameters) {
        return new SQL() {{
            SELECT(" * ");
            FROM(tableName);

            if (parameters != null) {
                for (Map.Entry<String,String> filterCriteria : parameters.entrySet()) {
                    if (!"limit".equalsIgnoreCase(filterCriteria.getKey().toString()) && !"offset".equalsIgnoreCase(filterCriteria.getKey().toString()))
                        WHERE(filterCriteria.getKey() + " = #{arg1." + filterCriteria.getKey() + "}");
                }
                for (Map.Entry filterCriteria : parameters.entrySet()) {
                    if ("offset".equalsIgnoreCase(filterCriteria.getKey().toString())) {
                        OFFSET_ROWS("#{offset}");

                    } else if ("limit".equalsIgnoreCase(filterCriteria.getKey().toString())) {
                        FETCH_FIRST_ROWS_ONLY("#{limit}");
                    }
                }
            }

        }}.toString();
    }

    /**
     * Select all table count with simple filter string.
     *
     * @param tableName  the table name
     * @param parameters the parameters
     * @return the string
     */
    public String selectAllTableCountWithSimpleFilter(String tableName, Map<String, String> parameters) {
        return new SQL() {{
            SELECT(" count(1) ");
            FROM(tableName);

            if (parameters != null) {
                for (Map.Entry filterCriteria : parameters.entrySet()) {
                    WHERE(filterCriteria.getKey() + " = #{arg1." + filterCriteria.getKey() + "}");
                }
            }

        }}.toString();
    }

    /**
     * Select all table data with given sql string.
     *
     * @param parameters the parameters
     * @return the string
     */
    public String selectAllTableDataWithGivenSQL(Map<String, String> parameters) {
        return new SQL() {{
            SELECT(" * ");
            FROM(" ( " + parameters.get("sql") + " ) t ");
            for (Map.Entry filterCriteria : parameters.entrySet()) {
                if ("offset".equalsIgnoreCase(filterCriteria.getKey().toString())) {
                    OFFSET_ROWS("#{offset}");

                } else if ("limit".equalsIgnoreCase(filterCriteria.getKey().toString())) {
                    FETCH_FIRST_ROWS_ONLY("#{limit}");
                }
            }

        }}.toString();
    }

}
