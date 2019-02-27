package com.dartusha;
/*
1. Написать метод, который меняет два элемента массива местами (массив может быть любого ссылочного типа);
 */
public class Task1<T> {
    private T[] obj;

    public Task1(T[] оbj) {
        this.obj = оbj;
    }

    public T[] getObj() {
        return obj;
    }

    public void printObj(){
        for (T rec:obj) {
            System.out.print(rec+" ");
        }
        System.out.println("");
    }

    public void changePlace(int x, int y) {
        T tmp=obj[x];
        obj[x]=obj[y];
        obj[y]=tmp;
    }
}
