package org.joe_e.testlib;

import org.joe_e.array.PowerlessArray;
import org.joe_e.Powerless;

public class ContainsPowerless {
    final String string;
    final PowerlessArray<String> sentence;
    final Powerless whoKnows = null;
    final double number;
    
    public ContainsPowerless(String... strings) {
        sentence = PowerlessArray.array(strings);
        string = strings[0];
        number = Math.PI;
    }
    
    // Disabled constructor.
    public ContainsPowerless(Object... objects) {
        String[] values = new String[objects.length];
        for (int i = 0; i < objects.length; ++i) {
            values[i] = objects[i].toString();
        }
        sentence = PowerlessArray.array(values);
        string = values[0];
        number = Math.PI * 4;
    }
}
