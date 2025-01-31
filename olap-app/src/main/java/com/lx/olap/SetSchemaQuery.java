package com.lx.olap;

import mondrian.olap.Axis;
import mondrian.olap.Cell;
import mondrian.olap.Connection;
import mondrian.olap.DriverManager;
import mondrian.olap.Member;
import mondrian.olap.Position;
import mondrian.olap.Query;
import mondrian.olap.Result;
import mondrian.olap.Util;
import mondrian.rolap.RolapConnection;
import mondrian.rolap.RolapConnectionProperties;
import mondrian.server.Execution;
import mondrian.server.Statement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.List;

/**
 * @Author: lixiang
 * @Description: 如何设置schema，而不从本地文件中读取
 */
public class SetSchemaQuery {

    private static final Logger logger = LogManager.getLogger(SetSchemaQuery.class);

    public static void main(String[] args) throws SQLException {
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
        RolapConnection rolapConnection = (RolapConnection) connection;
        // descendants 函数来获取 [Time].[1997].[Q1] 的所有后代成员
        String mdx = "SELECT {[Measures].[Unit Sales], [Measures].[Store Sales]} ON COLUMNS,\n" +
                "  {descendants([Time].[1997].[Q1])} ON ROWS\n" +
                "FROM [Sales]\n" +
                "WHERE [Gender].[F]";
        Query query = connection.parseQuery(mdx);
        Statement statement = query.getStatement();
        Execution execution = new Execution(statement, 300_000L);
        Result result = rolapConnection.execute(execution);
        // 获取所有轴
        // 0: 列轴
        // 1: 行 轴
        Axis[] axes = result.getAxes();
        Axis columnAxis = axes[0];
        Axis rowAxis = axes[1];
        // 获取维度
        int rowSize = rowAxis.getPositions().size();
        int columnSize = columnAxis.getPositions().size();
        Cell[][] cells = new Cell[rowSize][columnSize];
        for (int i = 0; i < rowSize; i++) {
            Position position = rowAxis.getPositions().get(i);
            // 获取到成员名
            System.out.print(position.get(0).getUniqueName() + " ");
            // 获取单元格
            for (int j = 0; j < columnSize; j++) {
                int[] pos = {j, i};
                Cell cell = result.getCell(pos);
                cells[i][j] = cell;
                System.out.print(cell.getValue() + " ");
            }
            System.out.println();
        }
        rolapConnection.close();
        System.out.println("result: " + cells.toString());
    }

}
