package model.entities.db;

import exceptions.DbException;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class BancoDados {

    private static final String URL = "jdbc:mysql://localhost:3306/todolist";
    private static final String USER = "root";
    private static final String PASSWORD = "Re@lmadrid7";

    private static Connection conn = null;

    // Abrir conexão com o banco de dados
    public static Connection getConnection() {
        if (conn == null) {
            try {
                conn = DriverManager.getConnection(URL, USER, PASSWORD);
            }
            catch (SQLException e) {
                throw new DbException(e.getMessage());
            }
        }

        return conn;
    }

    // Fechar conexão
    public static void closeConnection() {
        if (conn != null)  {
            try {
                conn.close();
            }
            catch (SQLException e) {
                throw new DbException(e.getMessage());
            }
        }
    }

    // Fechar statements
    public static void closeStatement(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            }
            catch (SQLException e) {
                throw new DbException(e.getMessage());
            }
        }
    }

    // Fechar ResultSet
    public static void closeResultSet(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            }
            catch (SQLException e) {
                throw new DbException(e.getMessage());
            }
        }
    }
}
