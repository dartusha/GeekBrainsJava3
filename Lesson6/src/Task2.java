/*
2. Написать метод, которому в качестве аргумента передается не пустой одномерный целочисленный массив.
Метод должен вернуть новый массив, который получен путем вытаскивания из исходного массива элементов,
идущих после последней четверки. Входной массив должен содержать хотя бы одну четверку, иначе в методе
необходимо выбросить RuntimeException. Написать набор тестов для этого метода (по 3-4 варианта входных данных).
 Вх: [ 1 2 4 4 2 3 4 1 7 ] -> вых: [ 1 7 ].
 */
public class Task2 {
    public int[] getArray(int[] inArray){
        int pos4=-1;
        for (int i = 0; i < inArray.length; i++) {
            if (inArray[i]==4){
                pos4=i;
            }
        }
        if (pos4==-1)
        {
            throw new RuntimeException("Массив должен содержать хотя бы одну 4!");
        }

        else
        {
            int[] result=new int[inArray.length-pos4-1];
            for (int i = pos4+1,j=0; i < inArray.length; i++, j++ ){
                result[j]=inArray[i];
            }
            return result;
        }

    }

/*
3. Написать метод, который проверяет состав массива из чисел 1 и 4.
Если в нем нет хоть одной четверки или единицы, то метод вернет false;
Написать набор тестов для этого метода (по 3-4 варианта входных данных).
 */
  public boolean checkArray(int[] inArray){
      boolean flag1=false;
      boolean flag4=false;
      for (int i = 0; i < inArray.length; i++) {
          if (inArray[i]==1){
              flag1=true;
          }
          if (inArray[i]==4){
              flag4=true;
          }
      }
      return flag1&&flag4;
  }
}
