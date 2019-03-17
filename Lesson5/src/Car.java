import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

public class Car implements Runnable {
    private static int CARS_COUNT;
    private CountDownLatch cdlStart;
    private CountDownLatch cdlFinish;

    static {
        CARS_COUNT = 0;
    }
    private Race race;
    private int speed;
    private String name;
    public String getName() {
        return name;
    }
    public int getSpeed() {
        return speed;
    }
    private CyclicBarrier cb;


    public Car(Race race, int speed, CyclicBarrier cb, CountDownLatch cdlStart, CountDownLatch cdlFinish) {
        this.race = race;
        this.speed = speed;
        CARS_COUNT++;
        this.name = "Участник #" + CARS_COUNT;
        this.cb=cb;
        this.cdlStart=cdlStart;
        this.cdlFinish=cdlFinish;
    }
    @Override
    public void run() {
        try {
            System.out.println(this.name + " готовится");
            Thread.sleep(500 + (int)(Math.random() * 800));
            System.out.println(this.name + " готов");
            cdlStart.countDown();
            cb.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < race.getStages().size(); i++) {
            race.getStages().get(i).go(this);
        }

        cdlFinish.countDown();

        if (cdlFinish.getCount()==CARS_COUNT-1){
            System.out.printf("%s ПОБЕДИЛ!",this.name); //Если ставить сообщение до уменьшения счетчика, в полученный временной промежуток иногда возникает ситуация
            System.out.println(); //что победителя два. Уменьшаем эту вероятность
        }


    }
}