package com.lx.olap;

import mondrian.olap.Axis;
import mondrian.olap.Cell;
import mondrian.olap.Connection;
import mondrian.olap.DriverManager;
import mondrian.olap.Position;
import mondrian.olap.Query;
import mondrian.olap.Result;

/**
 * @Author: lixiang
 * @Description:
 */
public class FirstQuery {

    public static void main(String[] args) {
        // 创建数据库连接
        // 创建数据库连接
        Connection connection = DriverManager.getConnection(
                "Provider=mondrian;Jdbc=jdbc:mysql://localhost:3306/foodmart?user=foodmart&password=foodmart;" +
                        "Catalog=/Users/lixiang-pc/projects/java/MondrainFirst/src/src/main/resources/FoodMart.xml;",
                null);

        // 定义 MDX 查询语句
        Query query = connection.parseQuery("SELECT {[Measures].[Unit Sales], [Measures].[Store Sales]} ON COLUMNS, {[Product].[All Products]} ON ROWS FROM [Sales]");

        // 执行查询并获取结果
        Result result = connection.execute(query);
        Axis[] axes = result.getAxes();
        connection.close();
    }

    private static Object[][] getRowData(Result result, int columnCount) {
        int rowCount = result.getAxes()[0].getPositions().size();
        Object[][] data = new Object[rowCount][columnCount];

//        for (int row = 0; row < rowCount; row++) {
//            Position rowPos = result.getAxes()[0].getPositions();
//            for (int col = 0; col < columnCount; col++) {
//                Position colPos = result.getAxes()[1].positions[col];
//                Cell cell = result.getCell(rowPos, colPos);
//                data[row][col] = cell == null ? null : cell.getValue();
//            }
//        }
        return data;
    }
}
