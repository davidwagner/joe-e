package org.joe_e.testlib;

public class ContainsNonImmutable {
    // An arbitrary-dimension point class
    public final double[] coordinates;
    
    public ContainsNonImmutable(double... coordinates) {
        this.coordinates = coordinates;
    }    
}
