package com.itworks.dbapi.mapper;

import com.itworks.dbapi.pojo.SelectQueryMetaData;
import com.itworks.dbapi.sqlbuilder.GenericSelectBuilder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The interface Generic mapper.
 */
@Mapper
public interface GenericMapper {

    /**
     * Gets sql for sql id.
     *
     * @param sqlId the sql id
     * @return the sql for sql id
     */
    @Select("select SQL_TEXT from PRE_DEFINED_SQL t where SQL_ID = #{sqlId}")
    String getSQLForSQLId(@Param("sqlId") String sqlId);

    /**
     * Gets sql parameter default values for sql id.
     *
     * @param sqlId the sql id
     * @return the sql parameter default values for sql id
     */
    @Select("select PARAM_DEFAULT_VALUES from PRE_DEFINED_SQL t where SQL_ID = #{sqlId}")
    String getSQLParameterDefaultValuesForSQLId(@Param("sqlId") String sqlId);

    /**
     * Gets all table data with simple filter.
     *
     * @param tableName  the table name
     * @param parameters the parameters
     * @return the all table data with simple filter
     */
    @SelectProvider(type = GenericSelectBuilder.class, method = "selectAllTableDataWithSimpleFilter")
    List<LinkedHashMap> getAllTableDataWithSimpleFilter(String tableName, Map<String, String> parameters);

    /**
     * Select with given sql list.
     *
     * @param requestParams the request params
     * @return the list
     */
    @SelectProvider(type = GenericSelectBuilder.class, method = "selectAllTableDataWithGivenSQL")
    List<LinkedHashMap> selectWithGivenSQL(Map<String, String> requestParams);


    /**
     * Select count with given sql long.
     *
     * @param requestParams the request params
     * @return the long
     */
    @Select("select count(*) from ( ${sql} ) t ")
    long selectCountWithGivenSQL(Map<String, String> requestParams);

    /**
     * Select data from post with selected columns list.
     *
     * @param filter the filter
     * @return the list
     */
    @SelectProvider(type = GenericSelectBuilder.class, method = "selectTableDataWithFilter")
    List<LinkedHashMap> selectDataFromPostWithSelectedColumns(SelectQueryMetaData filter);

    /**
     * Select count from post with selected columns long.
     *
     * @param filter the filter
     * @return the long
     */
    @SelectProvider(type = GenericSelectBuilder.class, method = "selectTableDataCountOnlyWithFilter")
    long selectCountFromPostWithSelectedColumns(SelectQueryMetaData filter);

}
