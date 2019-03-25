import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

/*
Создать класс, который может выполнять «тесты». В качестве тестов выступают классы с наборами методов
с аннотациями @Test. Для этого у него должен быть статический метод start(), которому в качестве параметра
 передается или объект типа Class, или имя класса. Из «класса-теста» вначале должен быть запущен метод
 с аннотацией @BeforeSuite, если такой имеется. Далее запущены методы с аннотациями @Test, а по завершении
  всех тестов – метод с аннотацией @AfterSuite. К каждому тесту необходимо добавить приоритеты (int числа
  от 1 до 10), в соответствии с которыми будет выбираться порядок их выполнения. Если приоритет одинаковый,
   то порядок не имеет значения. Методы с аннотациями @BeforeSuite и @AfterSuite должны присутствовать
   в единственном экземпляре, иначе необходимо бросить RuntimeException при запуске «тестирования».
 */
public class TestEngine {
    public static void start(Class inClass){
        start(inClass,new Object[][]{{}});
    }

    //TODO  Сделать возможность передавать параметры методов, конструкторов в масссиве - этого пока не сделала
    public static void start(Class inClass,Object[][] params){
        Boolean flagOnly=false;
        Constructor[] constructors = inClass.getDeclaredConstructors();
        for (Constructor o : constructors) {
            if(o.getAnnotation(BeforeSuite.class) != null) {
                try {
                    if (flagOnly) {
                        throw new RuntimeException("Конструктор с аннотацией BeforeSuite должен быть единственным!");
                    }
                    System.out.println(o);
                    Class[] paramTypes = o.getParameterTypes();
                    for (Class paramType : paramTypes) {
                        System.out.print(paramType.getName() + " ");
                    }
                    System.out.println();
                    o.newInstance();
                    flagOnly=true;
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }

        if (flagOnly==false){
            Method[] methods=inClass.getDeclaredMethods();
            for (Method o : methods) {
                if(o.getAnnotation(BeforeSuite.class) != null) {
                    try {
                        if (flagOnly) {
                            throw new RuntimeException("Метод с аннотацией BeforeSuite должен быть единственным!");
                        }
                        System.out.println(o);
                        Class[] paramTypes = o.getParameterTypes();
                        for (Class paramType : paramTypes) {
                            System.out.print(paramType.getName() + " ");
                        }
                        o.invoke(inClass);
                        flagOnly=true;
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        Method[] methods=inClass.getDeclaredMethods();
        HashMap<Integer,Method> map= new HashMap<>();
        int cnt=0;
        for (Method o : methods) {
            if(o.getAnnotation(Test.class) != null) {
                map.put(o.getAnnotation(Test.class).priority(),o);
            }
        }

        for(int counter : map.keySet()) {
            System.out.println(map.get(counter));
            try {
                Class[] paramTypes = map.get(counter).getParameterTypes();
                for (Class paramType : paramTypes) {
                    System.out.print(paramType.getName() + " ");
                }
                map.get(counter).invoke(inClass);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        flagOnly=false;
        methods=inClass.getDeclaredMethods();
        for (Method o : methods) {
            if(o.getAnnotation(AfterSuite.class) != null) {
                try {
                    if (flagOnly) {
                        throw new RuntimeException("Метод с аннотацией AfterSuite должен быть единственным!");
                    }
                    System.out.println(o);
                    Class[] paramTypes = o.getParameterTypes();
                    for (Class paramType : paramTypes) {
                        System.out.print(paramType.getName() + " ");
                    }
                    o.invoke(inClass);
                    flagOnly=true;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static void start(String inClassName){
        start(inClassName,new Object[][]{{}});
    }

    public static void start(String inClassName,Object[][] params){
        try {
            start(Class.forName(inClassName),params);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
