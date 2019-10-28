package com.blcultra.datainit;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by sgy05 on 2019/8/8.
 */
public class DBHelper {

    private static String driver = "com.mysql.jdbc.Driver";
    private static String user = "root";
    private static String password = "1234";
    private static String url = "jdbc:mysql://localhost:3306/patent_search?useUnicode=true&amp;characterEncoding=UTF8&allowMultiQueries=true";

    public Connection conn = null;
    public PreparedStatement pst = null;

    public DBHelper() {

    }

    public void openConnection() {
        try {
            Class.forName(driver);//指定连接类型
            conn = DriverManager.getConnection(url, user, password);//获取连接
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            this.conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static String getDriver() {
        return driver;
    }

    public static void setDriver(String driver) {
        DBHelper.driver = driver;
    }

    public static String getUser() {
        return user;
    }

    public static void setUser(String user) {
        DBHelper.user = user;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        DBHelper.password = password;
    }

    public static String getUrl() {
        return url;
    }

    public static void setUrl(String url) {
        DBHelper.url = url;
    }

    /**
     * 提供功能接口，用于获取连接对象
     * @return
     */
    public Connection getConnection(){
        return conn;
    }
}