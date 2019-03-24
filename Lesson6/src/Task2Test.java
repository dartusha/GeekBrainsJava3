import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class Task2Test {
    private Task2 task2;

    @Parameterized.Parameters
    public static Collection data() {
        return Arrays.asList(new int[][][] {
                {{1, 2, 4, 4, 2, 3, 4, 1, 7},{1,7}},
                {{1, 2, 1, 1, 2, 4, 1, 1, 7},{1,1,7}},
                {{1, 2, 1, 1, 2, 4, 1, 1, 4},{}},
                {{4, 2, 1, 1, 2, 9, 1, 1, 0},{2, 1, 1, 2, 9, 1, 1, 0}},
        });
    }

    private int[] arrayRes, arrayIn;

    public Task2Test(int[] arrayIn, int[] arrayRes){
        this.arrayIn=arrayIn;
        this.arrayRes=arrayRes;
    }

    @Before
    public void startTest() {
        task2 = new Task2();
    }

    @Test
    public void testTask2() {
        Assert.assertArrayEquals(arrayRes, task2.getArray(arrayIn));
    }

}
