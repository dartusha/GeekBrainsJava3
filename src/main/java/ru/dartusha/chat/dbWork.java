package ru.dartusha.chat;

import java.sql.*;
import java.util.Map;

public class dbWork {

    //1. Добавить в сетевой чат авторизацию через базу данных SQLite.
    public static  void refresh(Map<String, String> users) throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");

        try (Connection connection=DriverManager.getConnection("jdbc:sqlite:userDB.db")){
            Statement statement=connection.createStatement();
            ResultSet resultSet=statement.executeQuery("select * from users");
            while (resultSet.next()){
                int id=resultSet.getInt("id");
                String firstName=resultSet.getString("first_name");
                String lastName=resultSet.getString("last_name");
                String login=resultSet.getString("login");
                String password=resultSet.getString("password");
                System.out.println(login+","+password);
                users.put(login,password);
            }
            connection.close();
        }
    }

    //2.*Добавить в сетевой чат возможность смены ника.
    public static boolean checkUserExists(String userName) throws SQLException {
        boolean result = false;
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:userDB.db")) {
            //Statement statement=connection.createStatement();
            // ResultSet resultSet=statement.executeQuery("select 1 from users where username=?");
            PreparedStatement ps = connection.prepareStatement("select 1 from users where login=?");
            ps.setString(1, userName);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result = true;
            }
            connection.close();
            return result;
        }
    }

    public static void  changeUserName(String oldUserName, String newUserName) throws SQLException {
            try (Connection connection=DriverManager.getConnection("jdbc:sqlite:userDB.db")){
                PreparedStatement ps = connection.prepareStatement("update users set login=? where login=?");
                ps.setString(1, newUserName);
                ps.setString(2, oldUserName);
                ps.execute();
                connection.close();
            }

    }

}
