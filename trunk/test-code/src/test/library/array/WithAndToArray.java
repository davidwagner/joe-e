package test.library.array;

import org.joe_e.array.*;
import java.util.Arrays;

public class WithAndToArray {
    public static void test() {
        String[] strings = {"foo", "bar", "baz"};
        Object[] stuff = {"foo", "bar", "baz", 1, 2, 75.0};
    
        ConstArray<Object> cao = ConstArray.array((Object)"foo", "bar", "baz");
        ConstArray<Object> newCao = cao.with(1).with(2).with(75.0);

        ConstArray<Object> cafo = ConstArray.array((Object[]) strings);
        ConstArray<Object> newCafo = cafo.with(1).with(2).with(75.0);
    
        Integer[] integers = {1, 2, 3};
        Number[] numbers = {1, 2, 3, 3L, 3.2, (byte) 23};
        
        ImmutableArray<Number> iafn = ImmutableArray.array((Number[]) integers);
        ImmutableArray<Number> newIafn = iafn.with(3L).with(3.2).with((byte) 23);    

        /* goofy grody hack! */    
        PowerlessArray<Object> pafo = PowerlessArray.array((Object[]) strings);
        PowerlessArray<Object> newPafo = pafo.with(1).with(2).with(75.0);
            
        boolean[] ttf = {true, true, false};
        Boolean[] ttfBoxed = {true, true, false};
        boolean[] ttftff = {true, true, false, true, false, false};
        
        BooleanArray ba = BooleanArray.array(true, true, false);
        BooleanArray newBa = ba.with(true).with(false).with(false);
        
        assert(newCao.length() == 6);
        assert(newCafo.length() == 6);
        assert(newIafn.length() == 6);
        
        assert(Arrays.equals(cao.toArray(new Object[]{}), strings));
        assert(Arrays.equals(cao.toArray(new String[]{}), strings));
        assert(Arrays.equals(newCao.toArray(new Object[]{}), stuff));
        
        assert(Arrays.equals(cafo.toArray(new Object[]{}), strings));
        assert(Arrays.equals(cafo.toArray(new String[]{}), strings));
        assert(Arrays.equals(newCafo.toArray(new Object[]{}), stuff));
        
        assert(Arrays.equals(pafo.toArray(new Object[]{}), strings));
        assert(Arrays.equals(pafo.toArray(new String[]{}), strings));
        assert(Arrays.equals(newPafo.toArray(new Object[]{}), stuff));
   
        assert(Arrays.equals(iafn.toArray(new Number[]{}), integers));
        assert(Arrays.equals(iafn.toArray(new Integer[]{}), integers));
        assert(Arrays.equals(newIafn.toArray(new Number[]{}), numbers));
        
        assert(Arrays.equals(ba.toBooleanArray(), ttf));
        assert(Arrays.equals(ba.toArray(new Object[]{}), ttfBoxed));
        assert(Arrays.equals(newBa.toBooleanArray(), ttftff));        
    }
}
