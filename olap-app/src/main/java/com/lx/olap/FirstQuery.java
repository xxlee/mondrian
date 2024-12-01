package com.lx.olap;

import mondrian.olap.Axis;
import mondrian.olap.Connection;
import mondrian.olap.DriverManager;
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
}
