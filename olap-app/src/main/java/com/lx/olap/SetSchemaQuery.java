package com.lx.olap;

import mondrian.olap.Connection;
import mondrian.olap.DriverManager;
import mondrian.olap.Query;
import mondrian.olap.Result;
import mondrian.olap.Util;
import mondrian.rolap.RolapConnectionProperties;

/**
 * @Author: lixiang
 * @Description: 如何设置schema，而不从本地文件中读取
 */
public class SetSchemaQuery {

    public static void main(String[] args) {
//        Connection connection = DriverManager.getConnection(
//                "Provider=mondrian;Jdbc=jdbc:mysql://localhost:3306/foodmart?user=foodmart&password=foodmart",
//                null);

        // 可以单独设置属性
        Util.PropertyList propertyList = new Util.PropertyList();
        propertyList.put(RolapConnectionProperties.Provider.name(), "mondrian");
        propertyList.put(RolapConnectionProperties.Jdbc.name(), "jdbc:mysql://localhost:3306/foodmart");
        propertyList.put(RolapConnectionProperties.JdbcUser.name(), "foodmart");
        propertyList.put(RolapConnectionProperties.JdbcPassword.name(), "foodmart");
        // 用catalogContent指定schema的形式，schema这里面必须设置name属性
        String catalogContent = "<Schema name=\"MySchema\">\n" +
                "<Cube name=\"Sales\">\n" +
                "<Table name=\"sales_fact_1997\"/>\n" +
                "<Dimension name=\"Gender\" foreignKey=\"customer_id\">\n" +
                "<Hierarchy hasAll=\"true\" allMemberName=\"All Genders\" primaryKey=\"customer_id\">\n" +
                "<Table name=\"customer\"/>\n" +
                "<Level name=\"Gender\" column=\"gender\" uniqueMembers=\"true\"/>\n" +
                "</Hierarchy>\n" +
                "</Dimension>\n" +
                "<Dimension name=\"Time\" foreignKey=\"time_id\">\n" +
                "<Hierarchy hasAll=\"false\" primaryKey=\"time_id\">\n" +
                "<Table name=\"time_by_day\"/>\n" +
                "<Level name=\"Year\" column=\"the_year\" type=\"Numeric\" uniqueMembers=\"true\"/>\n" +
                "<Level name=\"Quarter\" column=\"quarter\" uniqueMembers=\"false\"/>\n" +
                "<Level name=\"Month\" column=\"month_of_year\" type=\"Numeric\" uniqueMembers=\"false\"/>\n" +
                "</Hierarchy>\n" +
                "</Dimension>\n" +
                "<Measure name=\"Unit Sales\" column=\"unit_sales\" aggregator=\"sum\" formatString=\"#,###\"/>\n" +
                "<Measure name=\"Store Sales\" column=\"store_sales\" aggregator=\"sum\" formatString=\"#,###.##\"/>\n" +
                "<Measure name=\"Store Cost\" column=\"store_cost\" aggregator=\"sum\" formatString=\"#,###.00\"/>\n" +
                "<CalculatedMember name=\"Profit\" dimension=\"Measures\" formula=\"[Measures].[Store Sales] - [Measures].[Store Cost]\">\n" +
                "<CalculatedMemberProperty name=\"FORMAT_STRING\" value=\"$#,##0.00\"/>\n" +
                "</CalculatedMember>\n" +
                "</Cube>\n" +
                "</Schema>";
        propertyList.put(RolapConnectionProperties.CatalogContent.name(), catalogContent);
        Connection connection = DriverManager.getConnection(propertyList, null);
        String mdx = "SELECT {[Measures].[Unit Sales], [Measures].[Store Sales]} ON COLUMNS,\n" +
                "  {descendants([Time].[1997].[Q1])} ON ROWS\n" +
                "FROM [Sales]\n" +
                "WHERE [Gender].[F]";
        Query query = connection.parseQuery(mdx);
        Result result = connection.execute(query);
        connection.close();
        System.out.println("query result: " + result.toString());
    }

}
