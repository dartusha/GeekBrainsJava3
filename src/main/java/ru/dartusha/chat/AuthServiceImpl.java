package ru.dartusha.chat;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class AuthServiceImpl implements AuthService {

    public Map<String, String> users = new HashMap<>();

    public AuthServiceImpl() throws ClassNotFoundException, SQLException {
       // users.put("ivan", "123");
       // users.put("petr", "345");
       // users.put("julia", "789");
        refresh();
    }

    @Override
    public boolean authUser(String username, String password) {
        String pwd = users.get(username);
        return pwd != null && pwd.equals(password);
    }

    public void refresh() throws ClassNotFoundException, SQLException {
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
}
