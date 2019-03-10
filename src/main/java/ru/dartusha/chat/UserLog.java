package ru.dartusha.chat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.TreeMap;

/*
Урок 3. Средства ввода-вывода
1. Добавить в сетевой чат запись локальной истории в текстовый файл на клиенте.
2. После загрузки клиента показывать ему последние 100 строк чата.
 */

public class UserLog  {
    RandomAccessFile logFile;
    String filePath=null;
    TreeMap<Integer,String> rsTmp = new TreeMap<Integer,String>(); //использую TreeMap, чтобы сохранить порядок строк

    public UserLog(String userName) {
        try {
            filePath="Log_"+userName+".txt";
            logFile = new RandomAccessFile(filePath, "rw");
            try {
                getArrLastMsgs();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void add(String str) throws IOException {
        logFile.seek(logFile.length());
        logFile.write((str+"\r\n").getBytes(Const.ENCODING_FROM));
    }

    public void rename(String userName) throws IOException {
        logFile.close();
        File tempfile = new File("Log_"+userName+".txt");
        File datafile = new File(filePath);
        datafile.renameTo(tempfile);
        logFile = new RandomAccessFile("Log_"+userName+".txt", "rw");
    }

    public void getArrLastMsgs() throws IOException {
        String result = null;
        int lnCnt=0;
        for (long curPos= logFile.length(); ((curPos>0)&&(lnCnt<Const.CHAT_HISTORY)) ; curPos--) {
            logFile.seek(curPos);
            char c = (char)logFile.read();
            if(c == '\n'){
                result = logFile.readLine();
                lnCnt++;
                if (!((result == null || result.length() == 0))) {
                    result=new String(result.getBytes(Const.ENCODING_TO));
                 //   System.out.println("test:"+result);
                    rsTmp.put(Const.CHAT_HISTORY-lnCnt,result);
                }
            }
        }
        }

    public void close() throws IOException {
        logFile.close();
    }

    public TreeMap<Integer,String>  getResArray() {
        return rsTmp;
    }
}
