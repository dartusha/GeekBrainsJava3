public class ForTest {
    @Test(priority = 2)
    public static void secondTest() {
        System.out.println("Second Test");
    }
    @Test (priority = 1)
    public static void firstTest() {
        System.out.println("First Test");
    }
    public static void thirdTest() {
        System.out.println("Third Test");
    }
}
