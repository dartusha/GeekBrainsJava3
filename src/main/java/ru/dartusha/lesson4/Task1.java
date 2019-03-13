package main.java.ru.dartusha.lesson4;

/*
1. Создать три потока, каждый из которых выводит определенную букву (A, B и C) 5 раз (порядок – ABСABСABС). Используйте wait/notify/notifyAll.
 */

public class Task1 {

    private final Object mon = new Object();
    private volatile char currentLetter = 'A';

    public static void main(String[] args) {
        Task1 w = new Task1();
        Thread t1 = new Thread(() -> {
            w.printLetter('A', 'B');
        });
        Thread t2 = new Thread(() -> {
            w.printLetter('B', 'C');
        });
        Thread t3 = new Thread(() -> {
            w.printLetter('C', 'A');
        });
        t1.start();
        t2.start();
        t3.start();
    }

    public void printLetter(char letter, char nextLetter) {
        synchronized (mon) {
            try {
                for (int i = 0; i < 5; i++) {
                    while (currentLetter != letter) {
                        mon.wait();
                    }
                    System.out.print(letter);
                    currentLetter = nextLetter;
                    mon.notifyAll();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}