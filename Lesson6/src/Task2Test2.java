import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class Task2Test2 {
    private Task2 task2;
    @Before
    public void startTest() {
        task2 = new Task2();
    }
    @Test(expected = RuntimeException.class)
    public void testTask2CheckException() {
        Assert.assertArrayEquals(new int[]{},task2.getArray(new int[]{1, 2, 1, 1, 2, 3, 1, 1, 7}));
    }
}
