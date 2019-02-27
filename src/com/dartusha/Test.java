package com.dartusha;

import java.util.ArrayList;

public class Test {
    /*
    1. Написать метод, который меняет два элемента массива местами (массив может быть любого ссылочного типа);
    Для эксперимента решила попробовать и объектами и методом, поэтому 2 варианта решения
     */
    public static <T> void ChangePlace(T[] obj, int x, int y){
        T tmp=obj[x];
        obj[x]=obj[y];
        obj[y]=tmp;
    }

    public static <T> void printObj(T[] obj){
        for (T rec:obj) {
            System.out.print(rec+" ");
        }
        System.out.println("");
    }

    /*
    2. Написать метод, который преобразует массив в ArrayList;
    */
    public static <T> ArrayList<T> toArrayList (T[] obj){
        ArrayList tmp = new ArrayList<T>();
        for (T rec:obj) {
            tmp.add(rec);
        }
        return tmp;
    }


    public static void main(String[] args) {
        System.out.println("Задание 1");
        Task1<Integer> test1;
        Integer arr1[]={1,2,3,4};
        test1=new Task1<Integer>(arr1);
        System.out.println("Тип Integer");
        System.out.println("Было:");
        test1.printObj();
        test1.changePlace(0,3);
        System.out.println("Стало (классом):");
        test1.printObj();
        System.out.println("Стало (методом):");
        Integer arr1_1[]={1,2,3,4};
        ChangePlace(arr1_1, 0, 3);
        printObj(arr1_1);
        System.out.println("-------------------------------");

        Task1<String> test2;
        String arr2[]={"мама","мыла","раму","мылом"};
        test2=new Task1<String>(arr2);
        System.out.println("Тип String");
        System.out.println("Было:");
        test2.printObj();
        test2.changePlace(0,3);
        System.out.println("Стало (классом):");
        test2.printObj();
        System.out.println("Стало (методом):");
        String arr2_1[]={"мама","мыла","раму","мылом"};
        ChangePlace(arr2_1, 0, 3);
        printObj(arr2_1);
        System.out.println("******************************");

        System.out.println("Задание 2");
        System.out.println("Тип Integer");
        ArrayList test2_1 = toArrayList(arr1);
        System.out.println(test2_1);
        System.out.println("-------------------------------");
        System.out.println("Тип String");
        ArrayList test2_2 = toArrayList(arr2);
        System.out.println(test2_2);
    }
}
