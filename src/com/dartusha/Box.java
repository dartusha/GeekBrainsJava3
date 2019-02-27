package com.dartusha;

/*
3. Большая задача:

    Есть классы Fruit -> Apple, Orange (больше фруктов не надо);
    Класс Box, в который можно складывать фрукты. Коробки условно сортируются по типу фрукта, поэтому в одну коробку нельзя сложить и яблоки, и апельсины;
    Для хранения фруктов внутри коробки можно использовать ArrayList;
    Сделать метод getWeight(), который высчитывает вес коробки, зная количество фруктов и вес одного фрукта (вес яблока – 1.0f, апельсина – 1.5f.
    Не важно, в каких это единицах);
    Внутри класса Коробка сделать метод compare, который позволяет сравнить текущую коробку с той, которую подадут в compare в качестве параметра,
    true – если она равны по весу, false – в противном случае (коробки с яблоками мы можем сравнивать с коробками с апельсинами);
    Написать метод, который позволяет пересыпать фрукты из текущей коробки в другую (помним про сортировку фруктов: нельзя яблоки высыпать в коробку с апельсинами).
    Соответственно, в текущей коробке фруктов не остается, а в другую перекидываются объекты, которые были в этой коробке;
    Не забываем про метод добавления фрукта в коробку.

 */

import java.util.ArrayList;

public class Box <T extends Fruit>{
    ArrayList tmp = new ArrayList<T>();

 public float getWeight(){
   float result=0;
    for (int i = 0; i < tmp.size(); i++) {
        Fruit tmpFruit=(T) tmp.get(i);
        result+=tmpFruit.getWeight();
    }
   return  result;
 }

 public boolean addInternal(T fruit){
     boolean flag=false;
     if (tmp.size()>0){
         if (fruit.getClass().equals(tmp.get(0).getClass())){
             flag=true;
         }
     }
     else{
         flag=true;
     }
     if (flag) {
         tmp.add(fruit);
         System.out.format("Фрукт %s был добавлен в коробку",fruit.getName());
         System.out.println();
     }
         else
     {
         System.out.println("Нельзя добавлять в коробку разные фрукты.");
        }
      return flag;
 }

 public void add(T fruit){
     boolean flag=addInternal(fruit);
 }

 public void minus(){
     tmp.clear();
 }

 public Boolean compare(Box otherBox){
   return (getWeight()==otherBox.getWeight())?true:false;
 }

 public void move(Box otherBox){
     for (int i = 0; i < tmp.size(); i++) {
         if (!otherBox.addInternal((T)tmp.get(i))) break;
         if (i==tmp.size()-1) {
             minus();
             System.out.println("Фрукты успешно пересыпаны");
         }
     }
 }




}
