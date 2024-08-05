package com.g1.kumaribookshopbackend.dbConnection;
import lombok.Getter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Getter
public class DbConnection {
    private static DbConnection dbConnection;
    private final Connection connection;

    private DbConnection() throws SQLException {
        connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/kumari_book_shop_db",
                "root",
                "root"
        );
    }

    public static DbConnection getInstance() throws SQLException {
        if (dbConnection == null) {
            dbConnection = new DbConnection();
        }
        return dbConnection;
    }

}
