import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class Task2Test3 {
    private Task2 task2;
    @Before
    public void startTest() {
        task2 = new Task2();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {"1 1 1 4 4 1 4 4",true},
                {"1 1 1 1 1 1",false},
                {"4 4 4 4",false},
                {"1 4 4 1 1 4 3",false},
        });
    }

    private String paramIn;
    private boolean paramRes;
    private int[] arrayIn;

    public Task2Test3(String paramIn, boolean paramRes){
        this.paramIn=paramIn;
        this.paramRes=paramRes;
        arrayIn=Arrays.stream(paramIn.split(" ")).mapToInt(Integer::parseInt).toArray();
    }

    @Test
    public void testTask2() {
        Assert.assertEquals(paramRes, task2.checkArray(arrayIn));
    }

}
