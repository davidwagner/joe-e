package test.library.array;

public class RunTests {
    /**
     * @param args
     */
    public static void main(String[] args) {
       test();
    }
    
    public static void test() {
        HonorariesCheck.test();
        EqualsAndHashCode.test();
        LengthGetAndToString.test();
        WithAndToArray.test();
        Builders.test();
    }
}
