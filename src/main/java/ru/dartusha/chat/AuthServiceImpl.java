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

    @Override
    public void refresh() throws SQLException, ClassNotFoundException {
        dbWork.refresh(users);
    }
}
