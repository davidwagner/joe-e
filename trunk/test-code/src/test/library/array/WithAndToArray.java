package test.library.array;

import org.joe_e.array.*;
import java.util.Arrays;

public class WithAndToArray { // also without()
    
    public static void test() {
        String[] strings = {"foo", "bar", "baz"};
        Object[] stuff = {"foo", "bar", "baz", 1, 2, 75.0};
        Object[] tuff = {"bar", "baz", 1, 2, 75.0};
        Object[] stff = {"foo", "bar", "baz", 2, 75.0};
        Object[] stuf = {"foo", "bar", "baz", 1, 2};
   
        ConstArray<Object> cao = ConstArray.array((Object)"foo", "bar", "baz");
        ConstArray<Object> newCao = cao.with(1).with(2).with(75.0);

        ConstArray<Object> cafo = ConstArray.array((Object[]) strings);
        ConstArray<Object> newCafo = cafo.with(1).with(2).with(75.0);
    
        Integer[] integers = {1, 2, 3};
        Number[] numbers = {1, 2, 3, 3L, 3.2, (byte) 23, (short) 2};
        Number[] umbers = {2, 3, 3L, 3.2, (byte) 23, (short) 2};
        Number[] numers = {1, 2, 3, 3.2, (byte) 23, (short) 2};
        Number[] number = {1, 2, 3, 3L, 3.2, (byte) 23};
        
        ImmutableArray<Number> iafn = ImmutableArray.array((Number[]) integers);
        ImmutableArray<Number> newIafn = iafn.with(3L).with(3.2).with((byte) 23).with((short) 2);    

        /* goofy grody hack! */    
        PowerlessArray<Object> pafo = PowerlessArray.array((Object[]) strings);
        PowerlessArray<Object> newPafo = pafo.with(1).with(2).with(75.0);
            
        boolean[] ttf = {true, true, false};
        Boolean[] ttfBoxed = {true, true, false};
        boolean[] ttftff = {true, true, false, true, false, false};
        boolean[] tftff = {true, false, true, false, false};
        boolean[] tttff = {true, true, true, false, false};
        boolean[] ttftf = {true, true, false, true, false};
        
        BooleanArray ba = BooleanArray.array(true, true, false);
        BooleanArray newBa = ba.with(true).with(false).with(false);
        
        assert(newCao.length() == 6);
        assert(newCafo.length() == 6);
        assert(newIafn.length() == 7);
        
        assert(Arrays.equals(cao.toArray(new Object[]{}), strings));
        assert(Arrays.equals(cao.toArray(new String[]{}), strings));
        assert(Arrays.equals(newCao.toArray(new Object[]{}), stuff));
        //ConstArray<Object> newCaoMinus0 = newCao.without(0);
        //Object[] arr = newCaoMinus0.toArray(new Object[]{});
        //boolean b = Arrays.equals(arr, tuff);
        //assert(b);
        assert(Arrays.equals(newCao.without(0).toArray(new Object[]{}),
                             tuff));
        assert(Arrays.equals(newCao.without(3).toArray(new Object[]{}),
                             stff));
        assert(Arrays.equals(newCao.without(5).toArray(new Object[]{}),
                             stuf));

        assert(Arrays.equals(cafo.toArray(new Object[]{}), strings));
        assert(Arrays.equals(cafo.toArray(new String[]{}), strings));
        assert(Arrays.equals(newCafo.toArray(new Object[]{}), stuff));
        assert(Arrays.equals(newCafo.without(0).toArray(new Object[]{}),
                             tuff));
        assert(Arrays.equals(newCafo.without(3).toArray(new Object[]{}),
                             stff));
        assert(Arrays.equals(newCafo.without(5).toArray(new Object[]{}),
                             stuf));

        
        assert(Arrays.equals(pafo.toArray(new Object[]{}), strings));
        assert(Arrays.equals(pafo.toArray(new String[]{}), strings));
        assert(Arrays.equals(newPafo.toArray(new Object[]{}), stuff));
        assert(Arrays.equals(newPafo.without(0).toArray(new Object[]{}),
                             tuff));
        assert(Arrays.equals(newPafo.without(3).toArray(new Object[]{}),
                             stff));
        assert(Arrays.equals(newPafo.without(5).toArray(new Object[]{}),
                             stuf));
   
        assert(Arrays.equals(iafn.toArray(new Number[]{}), integers));
        assert(Arrays.equals(iafn.toArray(new Integer[]{}), integers));
        assert(Arrays.equals(newIafn.toArray(new Number[]{}), numbers));
        assert(Arrays.equals(newIafn.without(0).toArray(new Object[]{}),
                             umbers));
        assert(Arrays.equals(newIafn.without(3).toArray(new Object[]{}),
                             numers));
        assert(Arrays.equals(newIafn.without(6).toArray(new Object[]{}),
                             number));
        
        assert(Arrays.equals(ba.toBooleanArray(), ttf));
        assert(Arrays.equals(ba.toArray(new Object[]{}), ttfBoxed));
        assert(Arrays.equals(newBa.toBooleanArray(), ttftff));        
        assert(Arrays.equals(newBa.without(0).toBooleanArray(),
                             tftff));
        assert(Arrays.equals(newBa.without(2).toBooleanArray(),
                             tttff));
        assert(Arrays.equals(newBa.without(4).toBooleanArray(),
                             ttftf));
    }
}
