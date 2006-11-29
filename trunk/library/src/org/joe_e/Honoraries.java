package org.joe_e;

import java.util.Map;
import java.util.HashMap;

/**
 * NOT AN ENDORSED STABLE INTERFACE!
 * May change at any time!
 * 
 * Comments?
 */

public class Honoraries {
    public static final int IMPL_IMMUTABLE = 0x0001;
    public static final int IMPL_POWERLESS = 0x0002; 
    public static final int IMPL_SELFLESS    = 0x0004;
    // public static final int IMPL_DATA      = 0x0008;
    public static final int IMPL_EQUATABLE = 0x0100;
    
    static final int MAKE_IMMUTABLE = IMPL_IMMUTABLE;
    static final int MAKE_POWERLESS = IMPL_IMMUTABLE | IMPL_POWERLESS;
    static final int MAKE_SELFLESS = IMPL_SELFLESS;

    // For now MAKE_DATA just means Powerless + Selfless
    static final int MAKE_DATA      = IMPL_IMMUTABLE | IMPL_POWERLESS
                                      | IMPL_SELFLESS;
    static final int MAKE_EQUATABLE = IMPL_EQUATABLE;
    
    private static final Map<Class<?>, Integer> entries;
   
    static {
        entries = new HashMap<Class<?>, Integer>();

        entries.put(String.class, MAKE_DATA);
        
        entries.put(Byte.class, MAKE_DATA);
        entries.put(Short.class, MAKE_DATA);
        entries.put(Integer.class, MAKE_DATA);
        entries.put(Long.class, MAKE_DATA);
        entries.put(Float.class, MAKE_DATA);
        entries.put(Double.class, MAKE_DATA);
        entries.put(java.math.BigInteger.class, MAKE_DATA);
        entries.put(java.math.BigDecimal.class, MAKE_DATA);
        entries.put(Number.class, MAKE_DATA);       
        
        entries.put(Character.class, MAKE_DATA);
        entries.put(Boolean.class, MAKE_DATA);       
        
        entries.put(Throwable.class, MAKE_POWERLESS);
        entries.put(Exception.class, MAKE_POWERLESS);       
        // make sure any additional untamed exceptions are listed here as
        // Powerless
        
        entries.put(Enum.class, MAKE_POWERLESS | MAKE_EQUATABLE);
        entries.put(Class.class, MAKE_POWERLESS | MAKE_EQUATABLE);       
        
        entries.put(java.lang.reflect.Type.class, MAKE_POWERLESS);
    }
        
    /**
     * Tests whether a class honorarily implements a Joe-E marker interface.
     * This consults the list of Joe-E honoraries to see whether the class
     * <CODE>implementor</CODE> is listed as honorarily implementing the
     * interface <CODE>mi</CODE>.
     * 
     * <P>Note: In most cases, it makes more sense to use
     * {@link Utility#isSubtypeOf(java.lang.Class, java.lang.Class) Utility.instanceOf()},
     * which returns true if <CODE>implementor</CODE> implements
     * <CODE>mi</CODE> either honorarily or in the Java type system.
     * (It is rare that one would want to treat objects that honorarily
     * implement a marker interface differently from objects that directly
     * implement it.  This method is primarily for internal use and may be
     * excluded from a finalized API.)
     * 
     * @param implementor the class to test for implementation of the interface
     * @param mi the marker interface
     * 
     * @return true if the specified class honorarily implements the specified marker
     *  interface
     */
    public static boolean honorarilyImplements(Class<?> implementor, Class<?> mi) {
        // BUG: See Markm's "NullPointerException bug in auto-unboxing"
        int honoraries = entries.get(implementor);
        
        if (mi == Immutable.class) {
            return ((honoraries & IMPL_IMMUTABLE) != 0);                
        } else if (mi == Powerless.class) {
            return ((honoraries & IMPL_POWERLESS) != 0);
        } else if (mi == Selfless.class) {
            return ((honoraries & IMPL_SELFLESS) != 0);
        // } else if (mi == Data.class) {
        //    return ((honoraries & IMPL_DATA) != 0);
        } else if (mi == Equatable.class) {
            return ((honoraries & IMPL_EQUATABLE) != 0);
        } else {
            return false; // mi not a marker interface
        }
    }
}
