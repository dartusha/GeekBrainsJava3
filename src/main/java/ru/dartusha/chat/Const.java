package ru.dartusha.chat;

import java.util.regex.Pattern;

public class Const {
    final static String CHAT="Чат";
    final static String AUTH_HEADER="Авторизация";
    final static String CMD_CLOSED="$CLOSED$";
    final static String CMD_CHANGE_NAME="$CHANGE_NAME$";
    final static String LOCAL_HOST="localhost";
    final static int    PORT=7777;
    final static String SERVER="server";
    static final Pattern MESSAGE_PATTERN = Pattern.compile("^/w (\\w+) (.+)", Pattern.MULTILINE);
    static final String MESSAGE_SEND_PATTERN = "/w %s: %s";
    static final String MESSAGE_SEND_ALL_PATTERN = "/a %s: %s";
    final static int  TIMEOUT=10;
    final static String DB_CONNECTION="jdbc:sqlite:userDB.db";
}
