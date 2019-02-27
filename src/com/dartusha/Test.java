package com.dartusha;

public class Test {
    public static void main(String[] args) {
        System.out.println("Задание 1");
        Task1<Integer> test1;
        Integer arr1[]={1,2,3,4};
        test1=new Task1<Integer>(arr1);
        System.out.println("Тип Integer");
        System.out.println("Было:");
        test1.printObj();
        test1.changePlace(0,3);
        System.out.println("Стало:");
        test1.printObj();
        System.out.println("-------------------------------");
        Task1<String> test2;
        String arr2[]={"мама","мыла","раму","мылом"};
        test2=new Task1<String>(arr2);
        System.out.println("Тип String");
        System.out.println("Было:");
        test2.printObj();
        test2.changePlace(0,3);
        System.out.println("Стало:");
        test2.printObj();
        System.out.println("******************************");


    }
}
