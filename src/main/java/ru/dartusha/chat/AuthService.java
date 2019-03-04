package ru.dartusha.chat;

import java.sql.SQLException;

public interface AuthService {

    boolean authUser(String username, String password);
    public void refresh() throws SQLException, ClassNotFoundException;
}